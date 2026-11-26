package com.arrupeteca.prestamo.service.impl;

import com.arrupeteca.config.CatalogoEstadosConstants;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.persistence.entity.Usuario;
import com.arrupeteca.persistence.repository.UsuarioRepository;
import com.arrupeteca.prestamo.dto.request.SolicitudRenovacionRequest;
import com.arrupeteca.prestamo.dto.response.detail.SolicitudRenovacionDetalleResponse;
import com.arrupeteca.prestamo.dto.response.summary.SolicitudRenovacionResumen;
import com.arrupeteca.prestamo.persistence.entity.EjemplarPrestamo;
import com.arrupeteca.prestamo.persistence.entity.EstadoSolicitud;
import com.arrupeteca.prestamo.persistence.entity.Prestamo;
import com.arrupeteca.prestamo.persistence.entity.SolicitudRenovacion;
import com.arrupeteca.prestamo.mapper.SolicitudRenovacionMapper;
import com.arrupeteca.prestamo.persistence.repository.EjemplarPrestamoRepository;
import com.arrupeteca.prestamo.persistence.repository.EstadoSolicitudRepository;
import com.arrupeteca.prestamo.persistence.repository.PrestamoRepository;
import com.arrupeteca.prestamo.persistence.repository.SolicitudRenovacionRepository;
import com.arrupeteca.prestamo.service.SolicitudRenovacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SolicitudRenovacionServiceImpl implements SolicitudRenovacionService {

    private final SolicitudRenovacionRepository renovacionRepository;
    private final EjemplarPrestamoRepository ejemplarPrestamoRepository;
    private final PrestamoRepository prestamoRepository;
    private final EstadoSolicitudRepository estadoSolicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final SolicitudRenovacionMapper renovacionMapper;

    @Override
    public Page<SolicitudRenovacionResumen> obtenerTodos(
            Boolean borradoLogico,
            Long idUsuario,
            Long idEstadoSolicitud,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Pageable pageable) {
        log.debug("Búsqueda de solicitudes de renovación con filtros - borradoLogico: {}, idUsuario: {}, idEstadoSolicitud: {}, pageable: {}",
                borradoLogico, idUsuario, idEstadoSolicitud, pageable);
        return renovacionRepository.busqueda(borradoLogico, idUsuario, idEstadoSolicitud, fechaInicio, fechaFin, pageable);
    }

    @Override
    public SolicitudRenovacionDetalleResponse obtenerPorId(Long id) {
        log.debug("Buscando solicitud de renovación por ID: {}", id);
        return renovacionRepository.busquedaPorId(id, null)
                .map(renovacionMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró la solicitud de renovación con ID: " + id));
    }

    @Override
    @Transactional
    public SolicitudRenovacionDetalleResponse solicitarRenovacion(SolicitudRenovacionRequest request) {
        log.info("Creando solicitud de renovación de prórroga de ejemplar");

        if (request.idEjemplarPrestamo() == null || !request.idEjemplarPrestamo().isPresent() || request.idEjemplarPrestamo().get() == null) {
            throw new ValidacionNegocioException("El ID del ejemplar prestado es obligatorio.");
        }
        if (request.idUsuario() == null || !request.idUsuario().isPresent() || request.idUsuario().get() == null) {
            throw new ValidacionNegocioException("El ID del usuario solicitante es obligatorio.");
        }

        Long idEjemplarPrestamo = request.idEjemplarPrestamo().get();
        Long idUsuario = request.idUsuario().get();

        if (idEjemplarPrestamo <= 0 || idUsuario <= 0) {
            throw new ValidacionNegocioException("Los IDs del ejemplar y del usuario deben ser números positivos mayores a cero.");
        }

        EjemplarPrestamo ejemplarPrestamo = ejemplarPrestamoRepository.findById(idEjemplarPrestamo)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró el ejemplar prestado con ID: " + idEjemplarPrestamo));

        if (ejemplarPrestamo.isBorradoLogico()) {
            throw new ValidacionNegocioException("El ejemplar prestado se encuentra inactivo.");
        }

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un usuario con ID: " + idUsuario));

        if (usuario.isBorradoLogico()) {
            throw new ValidacionNegocioException("El usuario solicitante se encuentra inactivo.");
        }
        if (usuario.isBloqueadoPorMora()) {
            throw new ValidacionNegocioException("El usuario se encuentra bloqueado por mora y no puede solicitar prórrogas.");
        }

        Long idEstado = (request.idEstadoSolicitud() != null && request.idEstadoSolicitud().isPresent() && request.idEstadoSolicitud().get() != null)
                ? request.idEstadoSolicitud().get()
                : 1L; // 1 = PENDIENTE

        EstadoSolicitud estadoSolicitud = estadoSolicitudRepository.findById(idEstado)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró el estado de solicitud con ID: " + idEstado));

        Integer dias = (request.diasSolicitados() != null && request.diasSolicitados().isPresent() && request.diasSolicitados().get() != null)
                ? request.diasSolicitados().get()
                : 7;

        if (dias <= 0) {
            throw new ValidacionNegocioException("Los días de prórroga solicitados deben ser un valor mayor a cero.");
        }

        SolicitudRenovacion entity = renovacionMapper.toEntity(request);
        entity.setEjemplarPrestamo(ejemplarPrestamo);
        entity.setUsuario(usuario);
        entity.setEstadoSolicitud(estadoSolicitud);
        entity.setDiasSolicitados(dias);
        entity.setFechaSolicitud(LocalDateTime.now());
        entity.setBorradoLogico(false);

        if (request.motivoSolicitud() != null && request.motivoSolicitud().isPresent()) {
            entity.setMotivoSolicitud(request.motivoSolicitud().get());
        }

        SolicitudRenovacion guardado = renovacionRepository.saveAndFlush(entity);
        log.info("Solicitud de renovación registrada exitosamente con ID: {}", guardado.getId());
        return renovacionMapper.toDetalleResponse(guardado);
    }

    @Override
    @Transactional
    public SolicitudRenovacionDetalleResponse actualizar(Long id, SolicitudRenovacionRequest request) {
        log.info("Actualizando solicitud de renovación ID: {}", id);

        SolicitudRenovacion existente = renovacionRepository.busquedaPorId(id, null)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró la solicitud de renovación con ID: " + id));

        if (request.version() != null && !request.version().equals(existente.getVersion())) {
            throw new ValidacionNegocioException("La versión proporcionada no coincide con la versión actual (" + existente.getVersion() + ")");
        }

        if (request.idEjemplarPrestamo() != null && request.idEjemplarPrestamo().isPresent() && request.idEjemplarPrestamo().get() != null) {
            Long idEjemplarPrestamo = request.idEjemplarPrestamo().get();
            if (idEjemplarPrestamo <= 0) {
                throw new ValidacionNegocioException("El ID del ejemplar prestado debe ser un número positivo mayor a cero.");
            }
            EjemplarPrestamo ejemplarPrestamo = ejemplarPrestamoRepository.findById(idEjemplarPrestamo)
                    .orElseThrow(() -> new ValidacionNegocioException("No se encontró el ejemplar prestado con ID: " + idEjemplarPrestamo));
            if (ejemplarPrestamo.isBorradoLogico()) {
                throw new ValidacionNegocioException("El ejemplar prestado se encuentra inactivo.");
            }
            existente.setEjemplarPrestamo(ejemplarPrestamo);
        }

        if (request.idUsuario() != null && request.idUsuario().isPresent() && request.idUsuario().get() != null) {
            Long idUsuario = request.idUsuario().get();
            if (idUsuario <= 0) {
                throw new ValidacionNegocioException("El ID del usuario solicitante debe ser un número positivo mayor a cero.");
            }
            Usuario usuario = usuarioRepository.findById(idUsuario)
                    .orElseThrow(() -> new ValidacionNegocioException("No se encontró un usuario con ID: " + idUsuario));
            if (usuario.isBorradoLogico()) {
                throw new ValidacionNegocioException("El usuario solicitante se encuentra inactivo.");
            }
            existente.setUsuario(usuario);
        }

        if (request.diasSolicitados() != null && request.diasSolicitados().isPresent() && request.diasSolicitados().get() != null) {
            Integer dias = request.diasSolicitados().get();
            if (dias <= 0) {
                throw new ValidacionNegocioException("Los días de prórroga solicitados deben ser un valor mayor a cero.");
            }
            existente.setDiasSolicitados(dias);
        }

        if (request.motivoSolicitud() != null && request.motivoSolicitud().isPresent()) {
            existente.setMotivoSolicitud(request.motivoSolicitud().get());
        }

        aplicarCamposResolucion(existente, request);

        SolicitudRenovacion actualizado = renovacionRepository.saveAndFlush(existente);
        aplicarProrrogaSiAprobada(actualizado);
        log.info("Solicitud de renovación ID: {} actualizada exitosamente", id);
        return renovacionMapper.toDetalleResponse(actualizado);
    }

    @Override
    @Transactional
    public SolicitudRenovacionDetalleResponse responderRenovacion(Long id, SolicitudRenovacionRequest request) {
        log.info("Resolviendo solicitud de renovación ID: {}", id);

        SolicitudRenovacion existente = renovacionRepository.busquedaPorId(id, null)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró la solicitud de renovación con ID: " + id));

        if (request.version() != null && !request.version().equals(existente.getVersion())) {
            throw new ValidacionNegocioException("La versión proporcionada no coincide con la versión actual (" + existente.getVersion() + ")");
        }

        aplicarCamposResolucion(existente, request);
        existente.setFechaRevision(LocalDateTime.now());

        SolicitudRenovacion actualizado = renovacionRepository.saveAndFlush(existente);
        aplicarProrrogaSiAprobada(actualizado);
        log.info("Solicitud de renovación ID: {} resuelta exitosamente", id);
        return renovacionMapper.toDetalleResponse(actualizado);
    }

    private void aplicarCamposResolucion(SolicitudRenovacion existente, SolicitudRenovacionRequest request) {
        if (request.idEstadoSolicitud() != null && request.idEstadoSolicitud().isPresent() && request.idEstadoSolicitud().get() != null) {
            Long idEstado = request.idEstadoSolicitud().get();
            if (idEstado <= 0) {
                throw new ValidacionNegocioException("El ID del estado de solicitud debe ser un número positivo mayor a cero.");
            }
            EstadoSolicitud estado = estadoSolicitudRepository.findById(idEstado)
                    .orElseThrow(() -> new ValidacionNegocioException("No se encontró el estado de solicitud con ID: " + idEstado));
            existente.setEstadoSolicitud(estado);
        }

        if (request.revisadoPor() != null && request.revisadoPor().isPresent() && request.revisadoPor().get() != null) {
            Long idRevisor = request.revisadoPor().get();
            if (idRevisor <= 0) {
                throw new ValidacionNegocioException("El ID del bibliotecario revisor debe ser un número positivo mayor a cero.");
            }
            Usuario revisor = usuarioRepository.findById(idRevisor)
                    .orElseThrow(() -> new ValidacionNegocioException("No se encontró el bibliotecario revisor con ID: " + idRevisor));
            existente.setRevisadoPor(revisor);
        }

        if (request.motivoRespuesta() != null && request.motivoRespuesta().isPresent()) {
            existente.setMotivoRespuesta(request.motivoRespuesta().get());
        }

        if (request.nuevaFechaLimitePropuesta() != null && request.nuevaFechaLimitePropuesta().isPresent()) {
            LocalDateTime nuevaFecha = request.nuevaFechaLimitePropuesta().get();
            if (nuevaFecha != null && nuevaFecha.isBefore(LocalDateTime.now())) {
                throw new ValidacionNegocioException("La nueva fecha límite propuesta no puede ser una fecha en el pasado.");
            }
            existente.setNuevaFechaLimitePropuesta(nuevaFecha);
        }
    }

    private void aplicarProrrogaSiAprobada(SolicitudRenovacion solicitud) {
        if (solicitud.getEstadoSolicitud() == null
                || !CatalogoEstadosConstants.EstadoSolicitud.APROBADA.equalsIgnoreCase(solicitud.getEstadoSolicitud().getNombre())) {
            return;
        }
        Prestamo prestamo = solicitud.getEjemplarPrestamo() != null ? solicitud.getEjemplarPrestamo().getPrestamo() : null;
        if (prestamo == null) {
            return;
        }

        LocalDateTime fechaBase = prestamo.getFechaLimiteDevolucion() != null
                ? prestamo.getFechaLimiteDevolucion()
                : LocalDateTime.now();
        LocalDateTime nuevaFecha = solicitud.getNuevaFechaLimitePropuesta() != null
                ? solicitud.getNuevaFechaLimitePropuesta()
                : fechaBase.plusDays(solicitud.getDiasSolicitados() != null ? solicitud.getDiasSolicitados() : 7);

        if (prestamo.getFechaPrestamo() != null && !nuevaFecha.isAfter(prestamo.getFechaPrestamo())) {
            throw new ValidacionNegocioException(
                    "La nueva fecha límite de devolución debe ser estrictamente posterior a la fecha del préstamo.");
        }
        if (prestamo.getFechaLimiteDevolucion() == null || nuevaFecha.isAfter(prestamo.getFechaLimiteDevolucion())) {
            prestamo.setFechaLimiteDevolucion(nuevaFecha);
            prestamoRepository.save(prestamo);
        }
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de la renovación ID: {} a {}", id, !estado);
        if (!renovacionRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró la solicitud de renovación con ID: " + id);
        }
        if (estado) {
            renovacionRepository.activar(id);
        } else {
            renovacionRepository.desactivar(id);
        }
    }
}
