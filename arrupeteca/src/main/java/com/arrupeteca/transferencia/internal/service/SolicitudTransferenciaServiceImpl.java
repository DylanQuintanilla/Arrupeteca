package com.arrupeteca.transferencia.internal.service;

import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.persistence.entity.Libro;
import com.arrupeteca.persistence.entity.Salon;
import com.arrupeteca.persistence.entity.Usuario;
import com.arrupeteca.persistence.repository.LibroRepository;
import com.arrupeteca.persistence.repository.SalonRepository;
import com.arrupeteca.persistence.repository.UsuarioRepository;
import com.arrupeteca.transferencia.dto.request.DetalleSolicitudTransferenciaRequest;
import com.arrupeteca.transferencia.dto.request.SolicitudTransferenciaRequest;
import com.arrupeteca.transferencia.dto.response.SolicitudTransferenciaDetalleResponse;
import com.arrupeteca.transferencia.dto.response.SolicitudTransferenciaResumenResponse;
import com.arrupeteca.transferencia.event.SolicitudTransferenciaCreadaEvent;
import com.arrupeteca.transferencia.event.SolicitudTransferenciaEvaluadaEvent;
import com.arrupeteca.transferencia.internal.entity.DetalleSolicitudTransferencia;
import com.arrupeteca.transferencia.internal.entity.EstadoSolicitudTransferencia;
import com.arrupeteca.transferencia.internal.entity.SolicitudTransferencia;
import com.arrupeteca.transferencia.internal.mapper.SolicitudTransferenciaMapper;
import com.arrupeteca.transferencia.internal.repository.DetalleSolicitudTransferenciaRepository;
import com.arrupeteca.transferencia.internal.repository.EstadoSolicitudTransferenciaRepository;
import com.arrupeteca.transferencia.internal.repository.SolicitudTransferenciaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SolicitudTransferenciaServiceImpl implements SolicitudTransferenciaService {

    private final SolicitudTransferenciaRepository solicitudRepository;
    private final DetalleSolicitudTransferenciaRepository detalleRepository;
    private final EstadoSolicitudTransferenciaRepository estadoSolicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final SalonRepository salonRepository;
    private final LibroRepository libroRepository;
    private final SolicitudTransferenciaMapper mapper;
    private final ApplicationEventPublisher events;

    @Override
    public Page<SolicitudTransferenciaResumenResponse> busquedaGlobal(
            Long idUsuario, Long idSalonDestino, Long idEstadoSolicitud, String motivo, Pageable pageable) {
        log.debug("Iniciando busqueda global de solicitudes de transferencia activas");
        String motivoFiltro = (motivo != null && !motivo.isBlank()) ? motivo.trim() : null;
        return solicitudRepository.busqueda(idUsuario, idSalonDestino, idEstadoSolicitud, motivoFiltro, false, pageable);
    }

    @Override
    public Page<SolicitudTransferenciaResumenResponse> busquedaAdmin(
            Long idUsuario, Long idSalonDestino, Long idEstadoSolicitud, String motivo, Boolean borradoLogico, Pageable pageable) {
        log.debug("Iniciando busqueda admin de solicitudes de transferencia con borradoLogico: {}", borradoLogico);
        String motivoFiltro = (motivo != null && !motivo.isBlank()) ? motivo.trim() : null;
        return solicitudRepository.busqueda(idUsuario, idSalonDestino, idEstadoSolicitud, motivoFiltro, borradoLogico, pageable);
    }

    @Override
    @Cacheable(value = "solicitudes-transferencia", key = "#id")
    public SolicitudTransferenciaDetalleResponse busquedaGlobalPorId(Long id) {
        log.debug("Buscando solicitud de transferencia activa por ID: {}", id);
        return solicitudRepository.busquedaPorId(id, false)
                .map(mapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontro una solicitud de transferencia con ID: " + id));
    }

    @Override
    public SolicitudTransferenciaDetalleResponse busquedaAdminPorId(Long id) {
        log.debug("Buscando solicitud de transferencia admin por ID: {}", id);
        return solicitudRepository.busquedaPorId(id, null)
                .map(mapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontro una solicitud de transferencia con ID: " + id));
    }

    @Override
    @Transactional
    public SolicitudTransferenciaDetalleResponse crear(SolicitudTransferenciaRequest request) {
        log.info("Creando nueva solicitud de transferencia");
        if (request.idUsuario() == null || !request.idUsuario().isPresent()) {
            throw new ValidacionNegocioException("El ID del usuario es obligatorio para crear la solicitud");
        }
        if (request.idSalonDestino() == null || !request.idSalonDestino().isPresent()) {
            throw new ValidacionNegocioException("El ID del salon destino es obligatorio para crear la solicitud");
        }
        if (request.idEstadoSolicitudTransferencia() == null || !request.idEstadoSolicitudTransferencia().isPresent()) {
            throw new ValidacionNegocioException("El ID del estado de solicitud es obligatorio");
        }

        if (request.idUsuario().get() <= 0 || request.idSalonDestino().get() <= 0 || request.idEstadoSolicitudTransferencia().get() <= 0) {
            throw new ValidacionNegocioException("Los IDs de la solicitud de transferencia deben ser números positivos mayores a cero");
        }

        Usuario usuario = usuarioRepository.findById(request.idUsuario().get())
                .orElseThrow(() -> new ValidacionNegocioException("No existe el usuario con ID: " + request.idUsuario().get()));
        Salon salonDestino = salonRepository.findById(request.idSalonDestino().get())
                .orElseThrow(() -> new ValidacionNegocioException("No existe el salon destino con ID: " + request.idSalonDestino().get()));
        EstadoSolicitudTransferencia estado = estadoSolicitudRepository.findById(request.idEstadoSolicitudTransferencia().get())
                .orElseThrow(() -> new ValidacionNegocioException("No existe el estado de solicitud con ID: " + request.idEstadoSolicitudTransferencia().get()));

        SolicitudTransferencia entidad = mapper.toEntity(request);
        entidad.setUsuario(usuario);
        entidad.setSalonDestino(salonDestino);
        entidad.setEstadoSolicitudTransferencia(estado);

        if (request.motivo() != null && request.motivo().isPresent()) {
            entidad.setMotivo(request.motivo().get());
        }

        SolicitudTransferencia guardada = solicitudRepository.saveAndFlush(entidad);

        if (request.detalles() != null && request.detalles().isPresent() && request.detalles().get() != null) {
            for (DetalleSolicitudTransferenciaRequest detReq : request.detalles().get()) {
                if (detReq.idLibro() != null && detReq.idLibro().isPresent() && detReq.cantidad() != null && detReq.cantidad().isPresent()) {
                    if (detReq.cantidad().get() <= 0) {
                        throw new ValidacionNegocioException("La cantidad de libros solicitados para transferencia no puede ser negativa ni cero");
                    }
                    if (detReq.idLibro().get() <= 0) {
                        throw new ValidacionNegocioException("El ID del libro debe ser un número positivo");
                    }
                    Libro libro = libroRepository.findById(detReq.idLibro().get())
                            .orElseThrow(() -> new ValidacionNegocioException("No existe el libro con ID: " + detReq.idLibro().get()));
                    DetalleSolicitudTransferencia detalle = mapper.toDetalleEntity(detReq);
                    detalle.setSolicitudTransferencia(guardada);
                    detalle.setLibro(libro);
                    detalle.setCantidad(detReq.cantidad().get());
                    detalleRepository.save(detalle);
                }
            }
        }

        SolicitudTransferencia actualizadaConDetalles = solicitudRepository.busquedaPorId(guardada.getId(), null)
                .orElse(guardada);

        int totalLibros = (actualizadaConDetalles.getDetalles() != null)
                ? actualizadaConDetalles.getDetalles().stream().mapToInt(DetalleSolicitudTransferencia::getCantidad).sum()
                : 0;

        events.publishEvent(new SolicitudTransferenciaCreadaEvent(
                actualizadaConDetalles.getId(),
                actualizadaConDetalles.getUsuario().getId(),
                actualizadaConDetalles.getSalonDestino().getId(),
                actualizadaConDetalles.getFechaCreacion(),
                totalLibros
        ));

        log.info("Solicitud de transferencia creada exitosamente con ID: {} (Evento de dominio emitido)", actualizadaConDetalles.getId());
        return mapper.toDetalleResponse(actualizadaConDetalles);
    }

    @Override
    @Transactional
    @CacheEvict(value = "solicitudes-transferencia", key = "#id")
    public SolicitudTransferenciaDetalleResponse actualizar(Long id, SolicitudTransferenciaRequest request) {
        log.info("Actualizando solicitud de transferencia con ID: {}", id);
        SolicitudTransferencia existente = solicitudRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontro una solicitud de transferencia con ID: " + id));

        if (request.version() != null && !request.version().equals(existente.getVersion())) {
            throw new ValidacionNegocioException("El registro fue modificado por otro usuario. Por favor recargue e intente nuevamente.");
        }

        mapper.updateEntity(request, existente);

        if (request.idUsuario() != null && request.idUsuario().isPresent() && request.idUsuario().get() != null) {
            Long newIdUsuario = request.idUsuario().get();
            if (newIdUsuario <= 0) {
                throw new ValidacionNegocioException("El ID de usuario debe ser un número positivo mayor a cero");
            }
            Usuario usuario = usuarioRepository.findById(newIdUsuario)
                    .orElseThrow(() -> new ValidacionNegocioException("No existe el usuario con ID: " + newIdUsuario));
            existente.setUsuario(usuario);
        }
        if (request.idSalonDestino() != null && request.idSalonDestino().isPresent() && request.idSalonDestino().get() != null) {
            Long newIdSalon = request.idSalonDestino().get();
            if (newIdSalon <= 0) {
                throw new ValidacionNegocioException("El ID del salón destino debe ser un número positivo mayor a cero");
            }
            Salon salon = salonRepository.findById(newIdSalon)
                    .orElseThrow(() -> new ValidacionNegocioException("No existe el salon destino con ID: " + newIdSalon));
            existente.setSalonDestino(salon);
        }

        boolean estadoModificado = false;
        EstadoSolicitudTransferencia nuevoEstado = null;
        if (request.idEstadoSolicitudTransferencia() != null && request.idEstadoSolicitudTransferencia().isPresent() && request.idEstadoSolicitudTransferencia().get() != null) {
            Long newIdEstado = request.idEstadoSolicitudTransferencia().get();
            if (newIdEstado <= 0) {
                throw new ValidacionNegocioException("El ID del estado de solicitud debe ser un número positivo mayor a cero");
            }
            nuevoEstado = estadoSolicitudRepository.findById(newIdEstado)
                    .orElseThrow(() -> new ValidacionNegocioException("No existe el estado con ID: " + newIdEstado));
            existente.setEstadoSolicitudTransferencia(nuevoEstado);
            estadoModificado = true;
        }
        if (request.motivo() != null) {
            existente.setMotivo(request.motivo().orElse(null));
        }
        if (request.motivoRechazo() != null) {
            existente.setMotivoRechazo(request.motivoRechazo().orElse(null));
        }

        if (request.detalles() != null && request.detalles().isPresent()) {
            detalleRepository.deleteBySolicitudTransferenciaId(existente.getId());
            if (existente.getDetalles() == null) {
                existente.setDetalles(new HashSet<>());
            } else {
                existente.getDetalles().clear();
            }

            for (DetalleSolicitudTransferenciaRequest detReq : request.detalles().get()) {
                if (detReq.idLibro() != null && detReq.idLibro().isPresent() && detReq.cantidad() != null && detReq.cantidad().isPresent()) {
                    if (detReq.cantidad().get() <= 0) {
                        throw new ValidacionNegocioException("La cantidad de libros solicitados para transferencia no puede ser negativa ni cero");
                    }
                    if (detReq.idLibro().get() <= 0) {
                        throw new ValidacionNegocioException("El ID del libro debe ser un número positivo");
                    }
                    Libro libro = libroRepository.findById(detReq.idLibro().get())
                            .orElseThrow(() -> new ValidacionNegocioException("No existe el libro con ID: " + detReq.idLibro().get()));
                    DetalleSolicitudTransferencia detalle = mapper.toDetalleEntity(detReq);
                    detalle.setSolicitudTransferencia(existente);
                    detalle.setLibro(libro);
                    detalle.setCantidad(detReq.cantidad().get());
                    detalleRepository.save(detalle);
                }
            }
        }

        SolicitudTransferencia guardada = solicitudRepository.saveAndFlush(existente);

        if (estadoModificado && nuevoEstado != null) {
            boolean aprobada = nuevoEstado.getNombre() != null && nuevoEstado.getNombre().equalsIgnoreCase("APROBADA");
            events.publishEvent(new SolicitudTransferenciaEvaluadaEvent(
                    guardada.getId(),
                    guardada.getUsuario().getId(),
                    nuevoEstado.getId(),
                    aprobada,
                    guardada.getMotivoRechazo(),
                    LocalDateTime.now()
            ));
            log.info("Evento SolicitudTransferenciaEvaluadaEvent emitido para ID: {}", guardada.getId());
        }

        log.info("Solicitud de transferencia con ID: {} actualizada exitosamente", id);
        return mapper.toDetalleResponse(guardada);
    }

    @Override
    @Transactional
    @CacheEvict(value = "solicitudes-transferencia", key = "#id")
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado borradoLogico de solicitud de transferencia ID: {} a {}", id, !estado);
        if (!solicitudRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontro una solicitud de transferencia con ID: " + id);
        }
        if (estado) {
            solicitudRepository.activar(id);
        } else {
            solicitudRepository.desactivar(id);
        }
    }
}
