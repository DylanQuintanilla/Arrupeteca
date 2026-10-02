package com.arrupeteca.prestamo.internal.service.impl;

import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.persistence.entity.Usuario;
import com.arrupeteca.persistence.repository.UsuarioRepository;
import com.arrupeteca.prestamo.dto.request.SolicitudPrestamoRequest;
import com.arrupeteca.prestamo.dto.response.detail.SolicitudPrestamoDetalleResponse;
import com.arrupeteca.prestamo.dto.response.summary.SolicitudPrestamoResumen;
import com.arrupeteca.prestamo.internal.entity.EstadoSolicitud;
import com.arrupeteca.prestamo.internal.entity.SolicitudPrestamo;
import com.arrupeteca.prestamo.internal.mapper.SolicitudPrestamoMapper;
import com.arrupeteca.prestamo.internal.repository.EstadoSolicitudRepository;
import com.arrupeteca.prestamo.internal.repository.SolicitudPrestamoRepository;
import com.arrupeteca.prestamo.internal.service.SolicitudPrestamoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import com.arrupeteca.prestamo.event.SolicitudReservadaEvent;
import org.springframework.context.ApplicationEventPublisher;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SolicitudPrestamoServiceImpl implements SolicitudPrestamoService {

    private final SolicitudPrestamoRepository solicitudPrestamoRepository;
    private final EstadoSolicitudRepository estadoSolicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final SolicitudPrestamoMapper solicitudPrestamoMapper;
    private final ApplicationEventPublisher events;

    @Override
    public Page<SolicitudPrestamoResumen> obtenerTodos(
            Boolean borradoLogico,
            Long idUsuario,
            Long idEstadoSolicitud,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Pageable pageable) {
        log.debug("Búsqueda de solicitudes de préstamo con filtros - borradoLogico: {}, idUsuario: {}, idEstadoSolicitud: {}, fechaInicio: {}, fechaFin: {}, pageable: {}",
                borradoLogico, idUsuario, idEstadoSolicitud, fechaInicio, fechaFin, pageable);
        return solicitudPrestamoRepository.busqueda(borradoLogico, idUsuario, idEstadoSolicitud, fechaInicio, fechaFin, pageable);
    }

    @Override
    public SolicitudPrestamoDetalleResponse obtenerPorId(Long id) {
        log.debug("Buscando solicitud de préstamo por ID: {}", id);
        return solicitudPrestamoRepository.busquedaPorId(id, null)
                .map(solicitudPrestamoMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró una solicitud de préstamo con ID: " + id));
    }

    @Override
    @Transactional
    public SolicitudPrestamoDetalleResponse crear(SolicitudPrestamoRequest request) {
        log.info("Creando nueva solicitud de préstamo para usuario ID: {}",
                request.idUsuario() != null && request.idUsuario().isPresent() ? request.idUsuario().get() : null);

        if (request.idUsuario() == null || !request.idUsuario().isPresent() || request.idUsuario().get() == null) {
            throw new ValidacionNegocioException("El ID de usuario es obligatorio para crear una solicitud de préstamo.");
        }

        Long idUsuario = request.idUsuario().get();
        if (idUsuario <= 0) {
            throw new ValidacionNegocioException("El ID de usuario debe ser un número positivo mayor a cero.");
        }
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un usuario con ID: " + idUsuario));

        if (usuario.isBorradoLogico()) {
            throw new ValidacionNegocioException("El usuario se encuentra inactivo.");
        }
        if (usuario.isBloqueadoPorMora()) {
            throw new ValidacionNegocioException("El usuario se encuentra bloqueado por mora y no puede realizar nuevas solicitudes.");
        }

        Long idEstadoSolicitud = (request.idEstadoSolicitud() != null && request.idEstadoSolicitud().isPresent() && request.idEstadoSolicitud().get() != null)
                ? request.idEstadoSolicitud().get()
                : 1L; // 1 = PENDIENTE por defecto

        if (idEstadoSolicitud <= 0) {
            throw new ValidacionNegocioException("El ID del estado de solicitud debe ser un número positivo mayor a cero.");
        }

        EstadoSolicitud estadoSolicitud = estadoSolicitudRepository.findById(idEstadoSolicitud)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un estado de solicitud con ID: " + idEstadoSolicitud));

        if (estadoSolicitud.isBorradoLogico()) {
            throw new ValidacionNegocioException("El estado de solicitud seleccionado se encuentra inactivo.");
        }

        SolicitudPrestamo solicitud = solicitudPrestamoMapper.toEntity(request);
        solicitud.setUsuario(usuario);
        solicitud.setEstadoSolicitud(estadoSolicitud);
        solicitud.setFechaSolicitud(LocalDateTime.now());
        solicitud.setBorradoLogico(false);

        if (request.fechaLimiteRetiro() != null && request.fechaLimiteRetiro().isPresent()) {
            solicitud.setFechaLimiteRetiro(request.fechaLimiteRetiro().get());
        }
        if (request.comentarioRespuesta() != null && request.comentarioRespuesta().isPresent()) {
            solicitud.setComentarioRespuesta(request.comentarioRespuesta().get());
        }
        if (request.creadoPorAdmin() != null && request.creadoPorAdmin().isPresent()) {
            solicitud.setCreadoPorAdmin(request.creadoPorAdmin().get());
        }

        SolicitudPrestamo guardado = solicitudPrestamoRepository.save(solicitud);
        events.publishEvent(new SolicitudReservadaEvent(guardado.getId(), usuario.getId(), List.of(), guardado.getFechaSolicitud()));
        log.info("Solicitud de préstamo creada exitosamente con ID: {}", guardado.getId());
        return solicitudPrestamoMapper.toDetalleResponse(guardado);
    }

    @Override
    @Transactional
    public SolicitudPrestamoDetalleResponse actualizar(Long id, SolicitudPrestamoRequest request) {
        log.info("Actualizando solicitud de préstamo ID: {}", id);

        SolicitudPrestamo existente = solicitudPrestamoRepository.busquedaPorId(id, null)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró una solicitud de préstamo con ID: " + id));

        if (request.version() != null && !request.version().equals(existente.getVersion())) {
            throw new ValidacionNegocioException("La versión proporcionada (" + request.version() + ") no coincide con la versión actual de la solicitud (" + existente.getVersion() + ")");
        }

        if (request.idUsuario() != null && request.idUsuario().isPresent() && request.idUsuario().get() != null) {
            Long newIdUsuario = request.idUsuario().get();
            if (newIdUsuario <= 0) {
                throw new ValidacionNegocioException("El ID de usuario debe ser un número positivo mayor a cero.");
            }
            Usuario usuario = usuarioRepository.findById(newIdUsuario)
                    .orElseThrow(() -> new ValidacionNegocioException("No se encontró un usuario con ID: " + newIdUsuario));
            existente.setUsuario(usuario);
        }

        if (request.idEstadoSolicitud() != null && request.idEstadoSolicitud().isPresent() && request.idEstadoSolicitud().get() != null) {
            Long newIdEstado = request.idEstadoSolicitud().get();
            if (newIdEstado <= 0) {
                throw new ValidacionNegocioException("El ID del estado de solicitud debe ser un número positivo mayor a cero.");
            }
            EstadoSolicitud estado = estadoSolicitudRepository.findById(newIdEstado)
                    .orElseThrow(() -> new ValidacionNegocioException("No se encontró un estado de solicitud con ID: " + newIdEstado));
            existente.setEstadoSolicitud(estado);
        }

        if (request.fechaLimiteRetiro() != null && request.fechaLimiteRetiro().isPresent()) {
            existente.setFechaLimiteRetiro(request.fechaLimiteRetiro().get());
        }

        if (request.comentarioRespuesta() != null && request.comentarioRespuesta().isPresent()) {
            existente.setComentarioRespuesta(request.comentarioRespuesta().get());
        }

        if (request.creadoPorAdmin() != null && request.creadoPorAdmin().isPresent()) {
            existente.setCreadoPorAdmin(request.creadoPorAdmin().get());
        }

        SolicitudPrestamo actualizado = solicitudPrestamoRepository.save(existente);
        log.info("Solicitud de préstamo ID: {} actualizada exitosamente", id);
        return solicitudPrestamoMapper.toDetalleResponse(actualizado);
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de solicitud de préstamo ID: {} a {}", id, !estado);
        if (!solicitudPrestamoRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró una solicitud de préstamo con ID: " + id);
        }
        if (estado) {
            solicitudPrestamoRepository.activar(id);
        } else {
            solicitudPrestamoRepository.desactivar(id);
        }
    }
}
