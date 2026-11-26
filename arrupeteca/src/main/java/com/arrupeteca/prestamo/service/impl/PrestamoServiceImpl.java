package com.arrupeteca.prestamo.service.impl;

import com.arrupeteca.config.CatalogoEstadosConstants;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.persistence.entity.Usuario;
import com.arrupeteca.persistence.repository.UsuarioRepository;
import com.arrupeteca.prestamo.dto.request.PrestamoRequest;
import com.arrupeteca.prestamo.dto.response.detail.PrestamoDetalleResponse;
import com.arrupeteca.prestamo.dto.response.summary.PrestamoResumen;
import com.arrupeteca.prestamo.persistence.entity.EstadoPrestamo;
import com.arrupeteca.prestamo.persistence.entity.EstadoSolicitud;
import com.arrupeteca.prestamo.persistence.entity.Prestamo;
import com.arrupeteca.prestamo.persistence.entity.SolicitudPrestamo;
import com.arrupeteca.prestamo.mapper.PrestamoMapper;
import com.arrupeteca.prestamo.persistence.repository.EstadoPrestamoRepository;
import com.arrupeteca.prestamo.persistence.repository.EstadoSolicitudRepository;
import com.arrupeteca.prestamo.persistence.repository.PrestamoRepository;
import com.arrupeteca.prestamo.persistence.repository.SolicitudPrestamoRepository;
import com.arrupeteca.prestamo.service.PrestamoService;
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
public class PrestamoServiceImpl implements PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final EstadoPrestamoRepository estadoPrestamoRepository;
    private final EstadoSolicitudRepository estadoSolicitudRepository;
    private final SolicitudPrestamoRepository solicitudPrestamoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PrestamoMapper prestamoMapper;

    @Override
    public Page<PrestamoResumen> obtenerTodos(
            Boolean borradoLogico,
            Long idUsuario,
            Long idEstadoPrestamo,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Pageable pageable) {
        log.debug("Búsqueda de préstamos con filtros - borradoLogico: {}, idUsuario: {}, idEstadoPrestamo: {}, pageable: {}",
                borradoLogico, idUsuario, idEstadoPrestamo, pageable);
        return prestamoRepository.busqueda(borradoLogico, idUsuario, idEstadoPrestamo, fechaInicio, fechaFin, pageable);
    }

    @Override
    public PrestamoDetalleResponse obtenerPorId(Long id) {
        log.debug("Buscando préstamo por ID: {}", id);
        return prestamoRepository.busquedaPorId(id, null)
                .map(prestamoMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un préstamo con ID: " + id));
    }

    @Override
    @Transactional
    public PrestamoDetalleResponse crear(PrestamoRequest request) {
        log.info("Creando nuevo préstamo en ventanilla");

        if (request.idUsuario() == null || !request.idUsuario().isPresent() || request.idUsuario().get() == null) {
            throw new ValidacionNegocioException("El ID de usuario titular es obligatorio para registrar un préstamo.");
        }
        if (request.entregadoPor() == null || !request.entregadoPor().isPresent() || request.entregadoPor().get() == null) {
            throw new ValidacionNegocioException("El ID del bibliotecario que entrega (entregadoPor) es obligatorio.");
        }

        Long idUsuario = request.idUsuario().get();
        Long idEntregadoPor = request.entregadoPor().get();

        if (idUsuario <= 0 || idEntregadoPor <= 0) {
            throw new ValidacionNegocioException("Los IDs de usuario y bibliotecario deben ser números positivos mayores a cero.");
        }
        if (request.idSolicitud() != null && request.idSolicitud().isPresent() && request.idSolicitud().get() != null && request.idSolicitud().get() <= 0) {
            throw new ValidacionNegocioException("El ID de solicitud de préstamo debe ser un número positivo mayor a cero.");
        }

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un usuario prestatario con ID: " + idUsuario));

        if (usuario.isBorradoLogico()) {
            throw new ValidacionNegocioException("El usuario prestatario se encuentra inactivo.");
        }
        if (usuario.isBloqueadoPorMora()) {
            throw new ValidacionNegocioException("El usuario prestatario se encuentra bloqueado por mora.");
        }

        Usuario entregadoPor = usuarioRepository.findById(idEntregadoPor)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró el bibliotecario con ID: " + idEntregadoPor));

        if (entregadoPor.isBorradoLogico()) {
            throw new ValidacionNegocioException("El usuario bibliotecario (entregadoPor) se encuentra inactivo.");
        }

        Long idEstado = (request.idEstadoPrestamo() != null && request.idEstadoPrestamo().isPresent() && request.idEstadoPrestamo().get() != null)
                ? request.idEstadoPrestamo().get()
                : 1L; // 1 = ACTIVO

        EstadoPrestamo estadoPrestamo = estadoPrestamoRepository.findById(idEstado)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un estado de préstamo con ID: " + idEstado));

        if (estadoPrestamo.isBorradoLogico()) {
            throw new ValidacionNegocioException("El estado de préstamo seleccionado se encuentra inactivo.");
        }

        SolicitudPrestamo solicitud = null;
        if (request.idSolicitud() != null && request.idSolicitud().isPresent() && request.idSolicitud().get() != null) {
            solicitud = validarYMarcarSolicitudEntregada(request.idSolicitud().get());
        }

        LocalDateTime fechaPrestamo = (request.fechaPrestamo() != null && request.fechaPrestamo().isPresent() && request.fechaPrestamo().get() != null)
                ? request.fechaPrestamo().get()
                : LocalDateTime.now();

        if (fechaPrestamo.isAfter(LocalDateTime.now().plusMinutes(5))) {
            throw new ValidacionNegocioException("La fecha de préstamo no puede ser posterior a la fecha y hora actual.");
        }

        LocalDateTime fechaLimite = (request.fechaLimiteDevolucion() != null && request.fechaLimiteDevolucion().isPresent() && request.fechaLimiteDevolucion().get() != null)
                ? request.fechaLimiteDevolucion().get()
                : fechaPrestamo.plusDays(7);

        if (!fechaLimite.isAfter(fechaPrestamo)) {
            throw new ValidacionNegocioException("La fecha límite de devolución debe ser estrictamente posterior a la fecha del préstamo.");
        }

        Prestamo prestamo = prestamoMapper.toEntity(request);
        prestamo.setUsuario(usuario);
        prestamo.setEntregadoPor(entregadoPor);
        prestamo.setEstadoPrestamo(estadoPrestamo);
        prestamo.setSolicitudPrestamo(solicitud);
        prestamo.setFechaPrestamo(fechaPrestamo);
        prestamo.setFechaLimiteDevolucion(fechaLimite);
        prestamo.setBorradoLogico(false);

        if (request.comentarios() != null && request.comentarios().isPresent()) {
            prestamo.setComentarios(request.comentarios().get());
        }

        Prestamo guardado = prestamoRepository.saveAndFlush(prestamo);
        log.info("Préstamo registrado exitosamente con ID: {}", guardado.getId());
        return prestamoMapper.toDetalleResponse(guardado);
    }

    @Override
    @Transactional
    public PrestamoDetalleResponse actualizar(Long id, PrestamoRequest request) {
        log.info("Actualizando préstamo ID: {}", id);

        Prestamo existente = prestamoRepository.busquedaPorId(id, null)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un préstamo con ID: " + id));

        if (request.version() != null && !request.version().equals(existente.getVersion())) {
            throw new ValidacionNegocioException("La versión proporcionada no coincide con la versión actual del préstamo (" + existente.getVersion() + ")");
        }

        if (request.idUsuario() != null && request.idUsuario().isPresent() && request.idUsuario().get() != null) {
            Long newIdUsuario = request.idUsuario().get();
            if (newIdUsuario <= 0) {
                throw new ValidacionNegocioException("El ID de usuario debe ser un número positivo mayor a cero.");
            }
            Usuario usuario = usuarioRepository.findById(newIdUsuario)
                    .orElseThrow(() -> new ValidacionNegocioException("No se encontró un usuario prestatario con ID: " + newIdUsuario));
            if (usuario.isBorradoLogico()) {
                throw new ValidacionNegocioException("El usuario prestatario se encuentra inactivo.");
            }
            existente.setUsuario(usuario);
        }

        if (request.entregadoPor() != null && request.entregadoPor().isPresent()) {
            Long newIdEntregadoPor = request.entregadoPor().get();
            if (newIdEntregadoPor == null || newIdEntregadoPor <= 0) {
                throw new ValidacionNegocioException("El ID del bibliotecario que entrega (entregadoPor) debe ser un número positivo mayor a cero.");
            }
            Usuario entregadoPor = usuarioRepository.findById(newIdEntregadoPor)
                    .orElseThrow(() -> new ValidacionNegocioException("No se encontró el bibliotecario con ID: " + newIdEntregadoPor));
            if (entregadoPor.isBorradoLogico()) {
                throw new ValidacionNegocioException("El usuario bibliotecario (entregadoPor) se encuentra inactivo.");
            }
            existente.setEntregadoPor(entregadoPor);
        }

        if (request.idSolicitud() != null && request.idSolicitud().isPresent()) {
            Long newIdSolicitud = request.idSolicitud().get();
            if (newIdSolicitud == null) {
                existente.setSolicitudPrestamo(null);
            } else {
                if (newIdSolicitud <= 0) {
                    throw new ValidacionNegocioException("El ID de solicitud de préstamo debe ser un número positivo mayor a cero.");
                }
                Long actualIdSolicitud = existente.getSolicitudPrestamo() != null ? existente.getSolicitudPrestamo().getId() : null;
                if (!newIdSolicitud.equals(actualIdSolicitud)) {
                    existente.setSolicitudPrestamo(validarYMarcarSolicitudEntregada(newIdSolicitud));
                }
            }
        }

        if (request.idEstadoPrestamo() != null && request.idEstadoPrestamo().isPresent() && request.idEstadoPrestamo().get() != null) {
            Long newIdEstado = request.idEstadoPrestamo().get();
            if (newIdEstado <= 0) {
                throw new ValidacionNegocioException("El ID del estado de préstamo debe ser un número positivo mayor a cero.");
            }
            EstadoPrestamo estado = estadoPrestamoRepository.findById(newIdEstado)
                    .orElseThrow(() -> new ValidacionNegocioException("No se encontró un estado de préstamo con ID: " + newIdEstado));
            existente.setEstadoPrestamo(estado);
        }

        LocalDateTime nuevaFechaPrestamo = (request.fechaPrestamo() != null && request.fechaPrestamo().isPresent() && request.fechaPrestamo().get() != null)
                ? request.fechaPrestamo().get()
                : existente.getFechaPrestamo();

        LocalDateTime nuevaFechaLimite = (request.fechaLimiteDevolucion() != null && request.fechaLimiteDevolucion().isPresent() && request.fechaLimiteDevolucion().get() != null)
                ? request.fechaLimiteDevolucion().get()
                : existente.getFechaLimiteDevolucion();

        if (nuevaFechaPrestamo != null && nuevaFechaPrestamo.isAfter(LocalDateTime.now().plusMinutes(5))) {
            throw new ValidacionNegocioException("La fecha de préstamo no puede ser posterior a la fecha y hora actual.");
        }
        if (nuevaFechaPrestamo != null && nuevaFechaLimite != null && !nuevaFechaLimite.isAfter(nuevaFechaPrestamo)) {
            throw new ValidacionNegocioException("La fecha límite de devolución debe ser estrictamente posterior a la fecha del préstamo.");
        }

        existente.setFechaPrestamo(nuevaFechaPrestamo);
        existente.setFechaLimiteDevolucion(nuevaFechaLimite);

        if (request.comentarios() != null && request.comentarios().isPresent()) {
            existente.setComentarios(request.comentarios().get());
        }

        Prestamo actualizado = prestamoRepository.saveAndFlush(existente);
        log.info("Préstamo ID: {} actualizado exitosamente", id);
        return prestamoMapper.toDetalleResponse(actualizado);
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado del préstamo ID: {} a {}", id, !estado);
        if (!prestamoRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró un préstamo con ID: " + id);
        }
        if (estado) {
            prestamoRepository.activar(id);
        } else {
            prestamoRepository.desactivar(id);
        }
    }

    private SolicitudPrestamo validarYMarcarSolicitudEntregada(Long idSolicitud) {
        SolicitudPrestamo solicitud = solicitudPrestamoRepository.findById(idSolicitud)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró la solicitud de préstamo con ID: " + idSolicitud));

        if (solicitud.isBorradoLogico()) {
            throw new ValidacionNegocioException("La solicitud de préstamo se encuentra inactiva.");
        }

        String nombreEstadoActual = solicitud.getEstadoSolicitud() != null ? solicitud.getEstadoSolicitud().getNombre() : null;
        if (nombreEstadoActual != null && (CatalogoEstadosConstants.EstadoSolicitud.RECHAZADA.equalsIgnoreCase(nombreEstadoActual)
                || CatalogoEstadosConstants.EstadoSolicitud.CANCELADA.equalsIgnoreCase(nombreEstadoActual)
                || CatalogoEstadosConstants.EstadoSolicitud.ENTREGADA.equalsIgnoreCase(nombreEstadoActual))) {
            throw new ValidacionNegocioException(
                    "La solicitud de préstamo en estado '" + nombreEstadoActual + "' no puede asociarse a una nueva entrega.");
        }

        EstadoSolicitud estadoEntregada = estadoSolicitudRepository
                .findFirstByNombreIgnoreCaseAndBorradoLogicoFalse(CatalogoEstadosConstants.EstadoSolicitud.ENTREGADA)
                .orElse(null);
        if (estadoEntregada != null) {
            solicitud.setEstadoSolicitud(estadoEntregada);
            solicitud = solicitudPrestamoRepository.save(solicitud);
        }

        return solicitud;
    }
}
