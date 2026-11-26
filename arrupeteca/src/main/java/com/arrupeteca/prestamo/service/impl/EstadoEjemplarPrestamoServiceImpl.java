package com.arrupeteca.prestamo.service.impl;

import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.prestamo.dto.request.EstadoEjemplarPrestamoRequest;
import com.arrupeteca.prestamo.dto.response.EstadoEjemplarPrestamoResponse;
import com.arrupeteca.prestamo.persistence.entity.EstadoEjemplarPrestamo;
import com.arrupeteca.prestamo.mapper.EstadoEjemplarPrestamoMapper;
import com.arrupeteca.prestamo.persistence.repository.EstadoEjemplarPrestamoRepository;
import com.arrupeteca.prestamo.service.EstadoEjemplarPrestamoService;
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
public class EstadoEjemplarPrestamoServiceImpl implements EstadoEjemplarPrestamoService {

    private final EstadoEjemplarPrestamoRepository estadoEjemplarPrestamoRepository;
    private final EstadoEjemplarPrestamoMapper estadoEjemplarPrestamoMapper;

    @Override
    @Cacheable(value = "estados-ejemplar-prestamo-activos", key = "#nombre != null ? #nombre : 'todos'")
    public List<EstadoEjemplarPrestamoResponse> obtenerTodosActivos(String nombre) {
        log.debug("Obteniendo estados de ejemplar de préstamo activos (filtro nombre: '{}')", nombre);
        String filtro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return estadoEjemplarPrestamoRepository.buscarActivosPorNombre(filtro).stream()
                .map(estadoEjemplarPrestamoMapper::toResponse)
                .toList();
    }

    @Override
    public Page<EstadoEjemplarPrestamoResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable) {
        log.debug("Búsqueda de estados de ejemplar de préstamo con filtros - nombre: {}, borradoLogico: {}, pageable: {}", nombre, borradoLogico, pageable);
        String nombreFiltro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return estadoEjemplarPrestamoRepository.busquedaAdmin(nombreFiltro, borradoLogico, pageable)
                .map(estadoEjemplarPrestamoMapper::toResponse);
    }

    @Override
    public EstadoEjemplarPrestamoResponse obtenerPorId(Long id) {
        log.debug("Buscando estado de ejemplar de préstamo por ID: {}", id);
        return estadoEjemplarPrestamoRepository.findById(id)
                .map(estadoEjemplarPrestamoMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un estado de ejemplar de préstamo con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "estados-ejemplar-prestamo-activos", allEntries = true)
    public EstadoEjemplarPrestamoResponse crear(EstadoEjemplarPrestamoRequest request) {
        log.info("Creando nuevo estado de ejemplar de préstamo con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, null);

        EstadoEjemplarPrestamo estado = estadoEjemplarPrestamoMapper.toEntity(request);
        estado.setNombre(nombreTrimmed);

        EstadoEjemplarPrestamo guardado = estadoEjemplarPrestamoRepository.save(estado);
        log.info("Estado de ejemplar de préstamo creado exitosamente con ID: {}", guardado.getId());
        return estadoEjemplarPrestamoMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "estados-ejemplar-prestamo-activos", allEntries = true)
    public EstadoEjemplarPrestamoResponse actualizar(Long id, EstadoEjemplarPrestamoRequest request) {
        log.info("Actualizando estado de ejemplar de préstamo con ID: {}", id);
        EstadoEjemplarPrestamo estadoExistente = estadoEjemplarPrestamoRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un estado de ejemplar de préstamo con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, estadoExistente);

        estadoEjemplarPrestamoMapper.updateEntity(request, estadoExistente);
        estadoExistente.setNombre(nombreTrimmed);

        EstadoEjemplarPrestamo actualizado = estadoEjemplarPrestamoRepository.save(estadoExistente);
        log.info("Estado de ejemplar de préstamo con ID: {} actualizado exitosamente", id);
        return estadoEjemplarPrestamoMapper.toResponse(actualizado);
    }

    private void validarUnicidad(String nombreNuevo, EstadoEjemplarPrestamo estadoExistente) {
        boolean esNuevo = (estadoExistente == null);
        boolean nombreCambio = !esNuevo && !estadoExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && estadoEjemplarPrestamoRepository.existsByNombreIgnoreCase(nombreNuevo)) {
            log.warn("Intento de duplicar estado de ejemplar de préstamo: {}", nombreNuevo);
            throw new ValidacionNegocioException("Ya existe un estado de ejemplar de préstamo con el nombre: " + nombreNuevo);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "estados-ejemplar-prestamo-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de estado de ejemplar de préstamo ID: {} a {}", id, !estado);
        if (!estadoEjemplarPrestamoRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró un estado de ejemplar de préstamo con ID: " + id);
        }
        if (estado) {
            estadoEjemplarPrestamoRepository.activar(id);
        } else {
            estadoEjemplarPrestamoRepository.desactivar(id);
        }
    }
}

