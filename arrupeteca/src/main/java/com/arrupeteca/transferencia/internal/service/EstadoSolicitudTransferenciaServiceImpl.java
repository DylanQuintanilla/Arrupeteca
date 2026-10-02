package com.arrupeteca.transferencia.internal.service;

import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.transferencia.dto.request.EstadoSolicitudTransferenciaRequest;
import com.arrupeteca.transferencia.dto.response.EstadoSolicitudTransferenciaResponse;
import com.arrupeteca.transferencia.internal.entity.EstadoSolicitudTransferencia;
import com.arrupeteca.transferencia.internal.mapper.EstadoSolicitudTransferenciaMapper;
import com.arrupeteca.transferencia.internal.repository.EstadoSolicitudTransferenciaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EstadoSolicitudTransferenciaServiceImpl implements EstadoSolicitudTransferenciaService {

    private final EstadoSolicitudTransferenciaRepository repository;
    private final EstadoSolicitudTransferenciaMapper mapper;

    @Override
    @Cacheable(value = "estados-solicitud-transferencia-activos", key = "#nombre != null ? #nombre : 'todos'")
    public List<EstadoSolicitudTransferenciaResponse> obtenerTodosActivos(String nombre) {
        log.debug("Obteniendo estados de solicitud de transferencia activos (filtro nombre: '{}')", nombre);
        String filtro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return repository.buscarActivosPorNombre(filtro).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public Page<EstadoSolicitudTransferenciaResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable) {
        log.debug("Busqueda de estados de solicitud de transferencia con filtros - nombre: {}, borradoLogico: {}, pageable: {}", nombre, borradoLogico, pageable);
        String nombreFiltro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return repository.busquedaAdmin(nombreFiltro, borradoLogico, pageable)
                .map(mapper::toResponse);
    }

    @Override
    public EstadoSolicitudTransferenciaResponse obtenerPorId(Long id) {
        log.debug("Buscando estado de solicitud de transferencia por ID: {}", id);
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontro un estado de solicitud de transferencia con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "estados-solicitud-transferencia-activos", allEntries = true)
    public EstadoSolicitudTransferenciaResponse crear(EstadoSolicitudTransferenciaRequest request) {
        log.info("Creando nuevo estado de solicitud de transferencia con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, null);

        EstadoSolicitudTransferencia entidad = mapper.toEntity(request);
        entidad.setNombre(nombreTrimmed);

        EstadoSolicitudTransferencia guardada = repository.save(entidad);
        log.info("Estado de solicitud de transferencia creado exitosamente con ID: {}", guardada.getId());
        return mapper.toResponse(guardada);
    }

    @Override
    @Transactional
    @CacheEvict(value = "estados-solicitud-transferencia-activos", allEntries = true)
    public EstadoSolicitudTransferenciaResponse actualizar(Long id, EstadoSolicitudTransferenciaRequest request) {
        log.info("Actualizando estado de solicitud de transferencia con ID: {}", id);
        EstadoSolicitudTransferencia entidadExistente = repository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontro un estado de solicitud de transferencia con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, entidadExistente);

        mapper.updateEntity(request, entidadExistente);
        entidadExistente.setNombre(nombreTrimmed);

        EstadoSolicitudTransferencia actualizada = repository.save(entidadExistente);
        log.info("Estado de solicitud de transferencia con ID: {} actualizado exitosamente", id);
        return mapper.toResponse(actualizada);
    }

    private void validarUnicidad(String nombreNuevo, EstadoSolicitudTransferencia entidadExistente) {
        boolean esNuevo = (entidadExistente == null);
        boolean nombreCambio = !esNuevo && !entidadExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && repository.existsByNombreIgnoreCase(nombreNuevo)) {
            log.warn("Intento de duplicar estado de solicitud de transferencia: {}", nombreNuevo);
            throw new ValidacionNegocioException("Ya existe un estado de solicitud de transferencia con el nombre: " + nombreNuevo);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "estados-solicitud-transferencia-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de estado de solicitud de transferencia ID: {} a {}", id, !estado);
        if (!repository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontro un estado de solicitud de transferencia con ID: " + id);
        }
        if (estado) {
            repository.activar(id);
        } else {
            repository.desactivar(id);
        }
    }
}
