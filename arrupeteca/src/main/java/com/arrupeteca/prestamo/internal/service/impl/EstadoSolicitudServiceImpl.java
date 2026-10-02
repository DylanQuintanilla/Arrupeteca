package com.arrupeteca.prestamo.internal.service.impl;

import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.prestamo.dto.request.EstadoSolicitudRequest;
import com.arrupeteca.prestamo.dto.response.EstadoSolicitudResponse;
import com.arrupeteca.prestamo.internal.entity.EstadoSolicitud;
import com.arrupeteca.prestamo.internal.mapper.EstadoSolicitudMapper;
import com.arrupeteca.prestamo.internal.repository.EstadoSolicitudRepository;
import com.arrupeteca.prestamo.internal.service.EstadoSolicitudService;
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
public class EstadoSolicitudServiceImpl implements EstadoSolicitudService {

    private final EstadoSolicitudRepository estadoSolicitudRepository;
    private final EstadoSolicitudMapper estadoSolicitudMapper;

    @Override
    @Cacheable(value = "estados-solicitud-activos", key = "#nombre != null ? #nombre : 'todos'")
    public List<EstadoSolicitudResponse> obtenerTodosActivos(String nombre) {
        log.debug("Obteniendo estados de solicitud activos (filtro nombre: '{}')", nombre);
        String filtro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return estadoSolicitudRepository.buscarActivosPorNombre(filtro).stream()
                .map(estadoSolicitudMapper::toResponse)
                .toList();
    }

    @Override
    public Page<EstadoSolicitudResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable) {
        log.debug("Búsqueda de estados de solicitud con filtros - nombre: {}, borradoLogico: {}, pageable: {}", nombre, borradoLogico, pageable);
        String nombreFiltro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return estadoSolicitudRepository.busquedaAdmin(nombreFiltro, borradoLogico, pageable)
                .map(estadoSolicitudMapper::toResponse);
    }

    @Override
    public EstadoSolicitudResponse obtenerPorId(Long id) {
        log.debug("Buscando estado de solicitud por ID: {}", id);
        return estadoSolicitudRepository.findById(id)
                .map(estadoSolicitudMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un estado de solicitud con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "estados-solicitud-activos", allEntries = true)
    public EstadoSolicitudResponse crear(EstadoSolicitudRequest request) {
        log.info("Creando nuevo estado de solicitud con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, null);

        EstadoSolicitud estado = estadoSolicitudMapper.toEntity(request);
        estado.setNombre(nombreTrimmed);

        EstadoSolicitud guardado = estadoSolicitudRepository.save(estado);
        log.info("Estado de solicitud creado exitosamente con ID: {}", guardado.getId());
        return estadoSolicitudMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "estados-solicitud-activos", allEntries = true)
    public EstadoSolicitudResponse actualizar(Long id, EstadoSolicitudRequest request) {
        log.info("Actualizando estado de solicitud con ID: {}", id);
        EstadoSolicitud estadoExistente = estadoSolicitudRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un estado de solicitud con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, estadoExistente);

        estadoSolicitudMapper.updateEntity(request, estadoExistente);
        estadoExistente.setNombre(nombreTrimmed);

        EstadoSolicitud actualizado = estadoSolicitudRepository.save(estadoExistente);
        log.info("Estado de solicitud con ID: {} actualizado exitosamente", id);
        return estadoSolicitudMapper.toResponse(actualizado);
    }

    private void validarUnicidad(String nombreNuevo, EstadoSolicitud estadoExistente) {
        boolean esNuevo = (estadoExistente == null);
        boolean nombreCambio = !esNuevo && !estadoExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && estadoSolicitudRepository.existsByNombreIgnoreCase(nombreNuevo)) {
            log.warn("Intento de duplicar estado de solicitud: {}", nombreNuevo);
            throw new ValidacionNegocioException("Ya existe un estado de solicitud con el nombre: " + nombreNuevo);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "estados-solicitud-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de estado de solicitud ID: {} a {}", id, !estado);
        if (!estadoSolicitudRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró un estado de solicitud con ID: " + id);
        }
        if (estado) {
            estadoSolicitudRepository.activar(id);
        } else {
            estadoSolicitudRepository.desactivar(id);
        }
    }
}

