package com.arrupeteca.transferencia.internal.service;

import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.transferencia.dto.request.EstadoTransferenciaRequest;
import com.arrupeteca.transferencia.dto.response.EstadoTransferenciaResponse;
import com.arrupeteca.transferencia.internal.entity.EstadoTransferencia;
import com.arrupeteca.transferencia.internal.mapper.EstadoTransferenciaMapper;
import com.arrupeteca.transferencia.internal.repository.EstadoTransferenciaRepository;
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
public class EstadoTransferenciaServiceImpl implements EstadoTransferenciaService {

    private final EstadoTransferenciaRepository repository;
    private final EstadoTransferenciaMapper mapper;

    @Override
    @Cacheable(value = "estados-transferencia-activos", key = "#nombre != null ? #nombre : 'todos'")
    public List<EstadoTransferenciaResponse> obtenerTodosActivos(String nombre) {
        log.debug("Obteniendo estados de transferencia activos (filtro nombre: '{}')", nombre);
        String filtro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return repository.buscarActivosPorNombre(filtro).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public Page<EstadoTransferenciaResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable) {
        log.debug("Busqueda de estados de transferencia con filtros - nombre: {}, borradoLogico: {}, pageable: {}", nombre, borradoLogico, pageable);
        String nombreFiltro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return repository.busquedaAdmin(nombreFiltro, borradoLogico, pageable)
                .map(mapper::toResponse);
    }

    @Override
    public EstadoTransferenciaResponse obtenerPorId(Long id) {
        log.debug("Buscando estado de transferencia por ID: {}", id);
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontro un estado de transferencia con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "estados-transferencia-activos", allEntries = true)
    public EstadoTransferenciaResponse crear(EstadoTransferenciaRequest request) {
        log.info("Creando nuevo estado de transferencia con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, null);

        EstadoTransferencia entidad = mapper.toEntity(request);
        entidad.setNombre(nombreTrimmed);

        EstadoTransferencia guardada = repository.save(entidad);
        log.info("Estado de transferencia creado exitosamente con ID: {}", guardada.getId());
        return mapper.toResponse(guardada);
    }

    @Override
    @Transactional
    @CacheEvict(value = "estados-transferencia-activos", allEntries = true)
    public EstadoTransferenciaResponse actualizar(Long id, EstadoTransferenciaRequest request) {
        log.info("Actualizando estado de transferencia con ID: {}", id);
        EstadoTransferencia entidadExistente = repository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontro un estado de transferencia con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, entidadExistente);

        mapper.updateEntity(request, entidadExistente);
        entidadExistente.setNombre(nombreTrimmed);

        EstadoTransferencia actualizada = repository.save(entidadExistente);
        log.info("Estado de transferencia con ID: {} actualizado exitosamente", id);
        return mapper.toResponse(actualizada);
    }

    private void validarUnicidad(String nombreNuevo, EstadoTransferencia entidadExistente) {
        boolean esNuevo = (entidadExistente == null);
        boolean nombreCambio = !esNuevo && !entidadExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && repository.existsByNombreIgnoreCase(nombreNuevo)) {
            log.warn("Intento de duplicar estado de transferencia: {}", nombreNuevo);
            throw new ValidacionNegocioException("Ya existe un estado de transferencia con el nombre: " + nombreNuevo);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "estados-transferencia-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de estado de transferencia ID: {} a {}", id, !estado);
        if (!repository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontro un estado de transferencia con ID: " + id);
        }
        if (estado) {
            repository.activar(id);
        } else {
            repository.desactivar(id);
        }
    }
}
