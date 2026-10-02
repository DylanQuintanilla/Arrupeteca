package com.arrupeteca.transferencia.internal.service;

import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.persistence.entity.EjemplarLibro;
import com.arrupeteca.persistence.entity.EstadoFisico;
import com.arrupeteca.persistence.entity.Mueble;
import com.arrupeteca.persistence.entity.Usuario;
import com.arrupeteca.persistence.repository.EjemplarLibroRepository;
import com.arrupeteca.persistence.repository.EstadoFisicoRepository;
import com.arrupeteca.persistence.repository.MuebleRepository;
import com.arrupeteca.persistence.repository.UsuarioRepository;
import com.arrupeteca.transferencia.dto.request.EjemplarTransferenciaRequest;
import com.arrupeteca.transferencia.dto.request.TransferenciaRequest;
import com.arrupeteca.transferencia.dto.response.TransferenciaDetalleResponse;
import com.arrupeteca.transferencia.dto.response.TransferenciaResumenResponse;
import com.arrupeteca.transferencia.event.TransferenciaConfirmadaEvent;
import com.arrupeteca.transferencia.event.TransferenciaDespachadaEvent;
import com.arrupeteca.transferencia.internal.entity.EjemplarTransferencia;
import com.arrupeteca.transferencia.internal.entity.EstadoTransferencia;
import com.arrupeteca.transferencia.internal.entity.SolicitudTransferencia;
import com.arrupeteca.transferencia.internal.entity.Transferencia;
import com.arrupeteca.transferencia.internal.mapper.TransferenciaMapper;
import com.arrupeteca.transferencia.internal.repository.EjemplarTransferenciaRepository;
import com.arrupeteca.transferencia.internal.repository.EstadoTransferenciaRepository;
import com.arrupeteca.transferencia.internal.repository.SolicitudTransferenciaRepository;
import com.arrupeteca.transferencia.internal.repository.TransferenciaRepository;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransferenciaServiceImpl implements TransferenciaService {

    private final TransferenciaRepository transferenciaRepository;
    private final EjemplarTransferenciaRepository ejemplarTransferenciaRepository;
    private final SolicitudTransferenciaRepository solicitudTransferenciaRepository;
    private final EstadoTransferenciaRepository estadoTransferenciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MuebleRepository muebleRepository;
    private final EjemplarLibroRepository ejemplarLibroRepository;
    private final EstadoFisicoRepository estadoFisicoRepository;
    private final TransferenciaMapper mapper;
    private final ApplicationEventPublisher events;

    @Override
    public Page<TransferenciaResumenResponse> busquedaGlobal(
            Long idUsuarioEnvia, Long idUsuarioRecibe, Long idMuebleOrigen, Long idMuebleDestino, Long idEstadoTransferencia, Boolean confirmada, Pageable pageable) {
        log.debug("Iniciando busqueda global de transferencias activas");
        return transferenciaRepository.busqueda(idUsuarioEnvia, idUsuarioRecibe, idMuebleOrigen, idMuebleDestino, idEstadoTransferencia, confirmada, false, pageable);
    }

    @Override
    public Page<TransferenciaResumenResponse> busquedaAdmin(
            Long idUsuarioEnvia, Long idUsuarioRecibe, Long idMuebleOrigen, Long idMuebleDestino, Long idEstadoTransferencia, Boolean confirmada, Boolean borradoLogico, Pageable pageable) {
        log.debug("Iniciando busqueda admin de transferencias con borradoLogico: {}", borradoLogico);
        return transferenciaRepository.busqueda(idUsuarioEnvia, idUsuarioRecibe, idMuebleOrigen, idMuebleDestino, idEstadoTransferencia, confirmada, borradoLogico, pageable);
    }

    @Override
    @Cacheable(value = "transferencias", key = "#id")
    public TransferenciaDetalleResponse busquedaGlobalPorId(Long id) {
        log.debug("Buscando transferencia activa por ID: {}", id);
        return transferenciaRepository.busquedaPorId(id, false)
                .map(mapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontro una transferencia con ID: " + id));
    }

    @Override
    public TransferenciaDetalleResponse busquedaAdminPorId(Long id) {
        log.debug("Buscando transferencia admin por ID: {}", id);
        return transferenciaRepository.busquedaPorId(id, null)
                .map(mapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontro una transferencia con ID: " + id));
    }

    @Override
    @Transactional
    public TransferenciaDetalleResponse crear(TransferenciaRequest request) {
        log.info("Creando nueva transferencia fisica");
        if (request.idUsuarioEnvia() == null || !request.idUsuarioEnvia().isPresent()) {
            throw new ValidacionNegocioException("El ID del usuario que envia es obligatorio");
        }
        if (request.idMuebleOrigen() == null || !request.idMuebleOrigen().isPresent()) {
            throw new ValidacionNegocioException("El ID del mueble origen es obligatorio");
        }
        if (request.idMuebleDestino() == null || !request.idMuebleDestino().isPresent()) {
            throw new ValidacionNegocioException("El ID del mueble destino es obligatorio");
        }
        if (request.idEstadoTransferencia() == null || !request.idEstadoTransferencia().isPresent()) {
            throw new ValidacionNegocioException("El ID del estado de transferencia es obligatorio");
        }

        if (request.idMuebleOrigen().get().equals(request.idMuebleDestino().get())) {
            throw new ValidacionNegocioException("El mueble origen y el mueble destino no pueden ser el mismo");
        }
        if (request.idUsuarioEnvia().get() <= 0 || request.idMuebleOrigen().get() <= 0 || request.idMuebleDestino().get() <= 0 || request.idEstadoTransferencia().get() <= 0) {
            throw new ValidacionNegocioException("Los IDs de la transferencia deben ser números positivos mayores a cero");
        }

        Usuario usuarioEnvia = usuarioRepository.findById(request.idUsuarioEnvia().get())
                .orElseThrow(() -> new ValidacionNegocioException("No existe el usuario emisor con ID: " + request.idUsuarioEnvia().get()));
        Mueble muebleOrigen = muebleRepository.findById(request.idMuebleOrigen().get())
                .orElseThrow(() -> new ValidacionNegocioException("No existe el mueble origen con ID: " + request.idMuebleOrigen().get()));
        Mueble muebleDestino = muebleRepository.findById(request.idMuebleDestino().get())
                .orElseThrow(() -> new ValidacionNegocioException("No existe el mueble destino con ID: " + request.idMuebleDestino().get()));
        EstadoTransferencia estado = estadoTransferenciaRepository.findById(request.idEstadoTransferencia().get())
                .orElseThrow(() -> new ValidacionNegocioException("No existe el estado de transferencia con ID: " + request.idEstadoTransferencia().get()));

        Transferencia entidad = mapper.toEntity(request);
        entidad.setUsuarioEnvia(usuarioEnvia);
        entidad.setMuebleOrigen(muebleOrigen);
        entidad.setMuebleDestino(muebleDestino);
        entidad.setEstadoTransferencia(estado);
        entidad.setFechaEnvio(request.fechaEnvio() != null && request.fechaEnvio().isPresent() ? request.fechaEnvio().get() : LocalDateTime.now());

        if (request.idSolicitudTransferencia() != null && request.idSolicitudTransferencia().isPresent()) {
            SolicitudTransferencia sol = solicitudTransferenciaRepository.findById(request.idSolicitudTransferencia().get())
                    .orElseThrow(() -> new ValidacionNegocioException("No existe la solicitud de transferencia con ID: " + request.idSolicitudTransferencia().get()));
            entidad.setSolicitudTransferencia(sol);
        }

        if (request.idUsuarioRecibe() != null && request.idUsuarioRecibe().isPresent()) {
            Usuario usuarioRecibe = usuarioRepository.findById(request.idUsuarioRecibe().get())
                    .orElseThrow(() -> new ValidacionNegocioException("No existe el usuario receptor con ID: " + request.idUsuarioRecibe().get()));
            entidad.setUsuarioRecibe(usuarioRecibe);
        }

        if (request.motivo() != null && request.motivo().isPresent()) {
            entidad.setMotivo(request.motivo().get());
        }

        Transferencia guardada = transferenciaRepository.saveAndFlush(entidad);
        List<Long> idsEjemplaresList = new ArrayList<>();

        if (request.ejemplares() != null && request.ejemplares().isPresent() && request.ejemplares().get() != null) {
            for (EjemplarTransferenciaRequest ejReq : request.ejemplares().get()) {
                if (ejReq.idEjemplarLibro() != null && ejReq.idEjemplarLibro().isPresent() && ejReq.idEstadoFisicoAlMomento() != null && ejReq.idEstadoFisicoAlMomento().isPresent()) {
                    if (ejReq.idEjemplarLibro().get() <= 0 || ejReq.idEstadoFisicoAlMomento().get() <= 0) {
                        throw new ValidacionNegocioException("Los IDs del ejemplar y estado físico deben ser positivos mayores a cero");
                    }
                    EjemplarLibro ejemplar = ejemplarLibroRepository.findById(ejReq.idEjemplarLibro().get())
                            .orElseThrow(() -> new ValidacionNegocioException("No existe el ejemplar con ID: " + ejReq.idEjemplarLibro().get()));
                    EstadoFisico estFisico = estadoFisicoRepository.findById(ejReq.idEstadoFisicoAlMomento().get())
                            .orElseThrow(() -> new ValidacionNegocioException("No existe el estado fisico con ID: " + ejReq.idEstadoFisicoAlMomento().get()));

                    EjemplarTransferencia ejTrans = mapper.toEjemplarEntity(ejReq);
                    ejTrans.setTransferencia(guardada);
                    ejTrans.setEjemplarLibro(ejemplar);
                    ejTrans.setEstadoFisicoAlMomento(estFisico);
                    if (ejReq.recibido() != null && ejReq.recibido().isPresent()) {
                        ejTrans.setRecibido(ejReq.recibido().get());
                    }
                    ejemplarTransferenciaRepository.save(ejTrans);
                    idsEjemplaresList.add(ejemplar.getId());
                }
            }
        }

        Transferencia actualizadaConEjemplares = transferenciaRepository.busquedaPorId(guardada.getId(), null)
                .orElse(guardada);

        Long idSolicitud = (actualizadaConEjemplares.getSolicitudTransferencia() != null) ? actualizadaConEjemplares.getSolicitudTransferencia().getId() : null;
        events.publishEvent(new TransferenciaDespachadaEvent(
                actualizadaConEjemplares.getId(),
                idSolicitud,
                actualizadaConEjemplares.getMuebleOrigen().getId(),
                actualizadaConEjemplares.getMuebleDestino().getId(),
                actualizadaConEjemplares.getUsuarioEnvia().getId(),
                idsEjemplaresList,
                actualizadaConEjemplares.getFechaEnvio()
        ));

        log.info("Transferencia creada exitosamente con ID: {} (Evento TransferenciaDespachadaEvent emitido)", actualizadaConEjemplares.getId());
        return mapper.toDetalleResponse(actualizadaConEjemplares);
    }

    @Override
    @Transactional
    @CacheEvict(value = "transferencias", key = "#id")
    public TransferenciaDetalleResponse actualizar(Long id, TransferenciaRequest request) {
        log.info("Actualizando transferencia con ID: {}", id);
        Transferencia existente = transferenciaRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontro una transferencia con ID: " + id));

        if (request.version() != null && !request.version().equals(existente.getVersion())) {
            throw new ValidacionNegocioException("El registro fue modificado por otro usuario. Por favor recargue e intente nuevamente.");
        }

        mapper.updateEntity(request, existente);

        if (request.idUsuarioEnvia() != null && request.idUsuarioEnvia().isPresent()) {
            Usuario envia = usuarioRepository.findById(request.idUsuarioEnvia().get())
                    .orElseThrow(() -> new ValidacionNegocioException("No existe el usuario emisor con ID: " + request.idUsuarioEnvia().get()));
            existente.setUsuarioEnvia(envia);
        }
        if (request.idUsuarioRecibe() != null && request.idUsuarioRecibe().isPresent()) {
            Usuario recibe = usuarioRepository.findById(request.idUsuarioRecibe().get())
                    .orElseThrow(() -> new ValidacionNegocioException("No existe el usuario receptor con ID: " + request.idUsuarioRecibe().get()));
            existente.setUsuarioRecibe(recibe);
        }
        if (request.idMuebleOrigen() != null && request.idMuebleOrigen().isPresent()) {
            Mueble mOrig = muebleRepository.findById(request.idMuebleOrigen().get())
                    .orElseThrow(() -> new ValidacionNegocioException("No existe el mueble origen con ID: " + request.idMuebleOrigen().get()));
            existente.setMuebleOrigen(mOrig);
        }
        if (request.idMuebleDestino() != null && request.idMuebleDestino().isPresent()) {
            Mueble mDest = muebleRepository.findById(request.idMuebleDestino().get())
                    .orElseThrow(() -> new ValidacionNegocioException("No existe el mueble destino con ID: " + request.idMuebleDestino().get()));
            existente.setMuebleDestino(mDest);
        }
        if (request.idEstadoTransferencia() != null && request.idEstadoTransferencia().isPresent()) {
            EstadoTransferencia estado = estadoTransferenciaRepository.findById(request.idEstadoTransferencia().get())
                    .orElseThrow(() -> new ValidacionNegocioException("No existe el estado con ID: " + request.idEstadoTransferencia().get()));
            existente.setEstadoTransferencia(estado);
        }
        if (request.observacionRecepcion() != null) {
            existente.setObservacionRecepcion(request.observacionRecepcion().orElse(null));
        }
        if (request.confirmada() != null && request.confirmada().isPresent()) {
            existente.setConfirmada(request.confirmada().get());
        }

        if (existente.getMuebleOrigen() != null && existente.getMuebleDestino() != null
                && existente.getMuebleOrigen().getId().equals(existente.getMuebleDestino().getId())) {
            throw new ValidacionNegocioException("El mueble origen y el mueble destino no pueden ser el mismo");
        }

        if (request.ejemplares() != null && request.ejemplares().isPresent()) {
            ejemplarTransferenciaRepository.deleteByTransferenciaId(existente.getId());
            if (existente.getEjemplares() == null) {
                existente.setEjemplares(new HashSet<>());
            } else {
                existente.getEjemplares().clear();
            }

            for (EjemplarTransferenciaRequest ejReq : request.ejemplares().get()) {
                if (ejReq.idEjemplarLibro() != null && ejReq.idEjemplarLibro().isPresent() && ejReq.idEstadoFisicoAlMomento() != null && ejReq.idEstadoFisicoAlMomento().isPresent()) {
                    if (ejReq.idEjemplarLibro().get() <= 0 || ejReq.idEstadoFisicoAlMomento().get() <= 0) {
                        throw new ValidacionNegocioException("Los IDs del ejemplar y estado físico deben ser positivos mayores a cero");
                    }
                    EjemplarLibro ejemplar = ejemplarLibroRepository.findById(ejReq.idEjemplarLibro().get())
                            .orElseThrow(() -> new ValidacionNegocioException("No existe el ejemplar con ID: " + ejReq.idEjemplarLibro().get()));
                    EstadoFisico estFisico = estadoFisicoRepository.findById(ejReq.idEstadoFisicoAlMomento().get())
                            .orElseThrow(() -> new ValidacionNegocioException("No existe el estado fisico con ID: " + ejReq.idEstadoFisicoAlMomento().get()));

                    EjemplarTransferencia ejTrans = mapper.toEjemplarEntity(ejReq);
                    ejTrans.setTransferencia(existente);
                    ejTrans.setEjemplarLibro(ejemplar);
                    ejTrans.setEstadoFisicoAlMomento(estFisico);
                    if (ejReq.recibido() != null && ejReq.recibido().isPresent()) {
                        ejTrans.setRecibido(ejReq.recibido().get());
                    }
                    ejemplarTransferenciaRepository.save(ejTrans);
                }
            }
        }

        Transferencia guardada = transferenciaRepository.saveAndFlush(existente);
        log.info("Transferencia con ID: {} actualizada exitosamente", id);
        return mapper.toDetalleResponse(guardada);
    }

    @Override
    @Transactional
    @CacheEvict(value = "transferencias", key = "#id")
    public TransferenciaDetalleResponse confirmarRecepcion(Long id, Long idUsuarioRecibe, String observacionRecepcion) {
        log.info("Confirmando recepcion de transferencia ID: {} por usuario ID: {}", id, idUsuarioRecibe);
        Transferencia transferencia = transferenciaRepository.busquedaPorId(id, null)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontro una transferencia con ID: " + id));

        Usuario usuarioRecibe = usuarioRepository.findById(idUsuarioRecibe)
                .orElseThrow(() -> new ValidacionNegocioException("No existe el usuario receptor con ID: " + idUsuarioRecibe));

        transferencia.setUsuarioRecibe(usuarioRecibe);
        transferencia.setFechaRecepcion(LocalDateTime.now());
        transferencia.setObservacionRecepcion(observacionRecepcion);
        transferencia.setConfirmada(true);

        List<Long> idsEjemplaresRecibidos = new ArrayList<>();

        if (transferencia.getEjemplares() != null) {
            for (EjemplarTransferencia ejTrans : transferencia.getEjemplares()) {
                ejTrans.setRecibido(true);
                EjemplarLibro ejemplar = ejTrans.getEjemplarLibro();
                if (ejemplar != null) {
                    idsEjemplaresRecibidos.add(ejemplar.getId());
                    if (transferencia.getMuebleDestino() != null) {
                        ejemplar.setMueble(transferencia.getMuebleDestino());
                        ejemplarLibroRepository.save(ejemplar);
                    }
                }
            }
        }

        Transferencia guardada = transferenciaRepository.saveAndFlush(transferencia);

        Long idSolicitud = (guardada.getSolicitudTransferencia() != null) ? guardada.getSolicitudTransferencia().getId() : null;
        events.publishEvent(new TransferenciaConfirmadaEvent(
                guardada.getId(),
                idSolicitud,
                guardada.getMuebleDestino().getId(),
                usuarioRecibe.getId(),
                idsEjemplaresRecibidos,
                observacionRecepcion,
                guardada.getFechaRecepcion()
        ));

        log.info("Recepcion de transferencia ID: {} confirmada (Evento TransferenciaConfirmadaEvent emitido)", id);
        return mapper.toDetalleResponse(guardada);
    }

    @Override
    @Transactional
    @CacheEvict(value = "transferencias", key = "#id")
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado borradoLogico de transferencia ID: {} a {}", id, !estado);
        if (!transferenciaRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontro una transferencia con ID: " + id);
        }
        if (estado) {
            transferenciaRepository.activar(id);
        } else {
            transferenciaRepository.desactivar(id);
        }
    }
}
