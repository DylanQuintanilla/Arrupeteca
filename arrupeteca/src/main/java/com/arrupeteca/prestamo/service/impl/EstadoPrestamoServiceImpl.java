package com.arrupeteca.prestamo.service.impl;

import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.prestamo.dto.request.EstadoPrestamoRequest;
import com.arrupeteca.prestamo.dto.response.EstadoPrestamoResponse;
import com.arrupeteca.prestamo.persistence.entity.EstadoPrestamo;
import com.arrupeteca.prestamo.mapper.EstadoPrestamoMapper;
import com.arrupeteca.prestamo.persistence.repository.EstadoPrestamoRepository;
import com.arrupeteca.prestamo.service.EstadoPrestamoService;
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
public class EstadoPrestamoServiceImpl implements EstadoPrestamoService {

    private final EstadoPrestamoRepository estadoPrestamoRepository;
    private final EstadoPrestamoMapper estadoPrestamoMapper;

    @Override
    @Cacheable(value = "estados-prestamo-activos", key = "#nombre != null ? #nombre : 'todos'")
    public List<EstadoPrestamoResponse> obtenerTodosActivos(String nombre) {
        log.debug("Obteniendo estados de préstamo activos (filtro nombre: '{}')", nombre);
        String filtro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return estadoPrestamoRepository.buscarActivosPorNombre(filtro).stream()
                .map(estadoPrestamoMapper::toResponse)
                .toList();
    }

    @Override
    public Page<EstadoPrestamoResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable) {
        log.debug("Búsqueda de estados de préstamo con filtros - nombre: {}, borradoLogico: {}, pageable: {}", nombre, borradoLogico, pageable);
        String nombreFiltro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return estadoPrestamoRepository.busquedaAdmin(nombreFiltro, borradoLogico, pageable)
                .map(estadoPrestamoMapper::toResponse);
    }

    @Override
    public EstadoPrestamoResponse obtenerPorId(Long id) {
        log.debug("Buscando estado de préstamo por ID: {}", id);
        return estadoPrestamoRepository.findById(id)
                .map(estadoPrestamoMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un estado de préstamo con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "estados-prestamo-activos", allEntries = true)
    public EstadoPrestamoResponse crear(EstadoPrestamoRequest request) {
        log.info("Creando nuevo estado de préstamo con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, null);

        EstadoPrestamo estadoPrestamo = estadoPrestamoMapper.toEntity(request);
        estadoPrestamo.setNombre(nombreTrimmed);

        EstadoPrestamo guardado = estadoPrestamoRepository.save(estadoPrestamo);
        log.info("Estado de préstamo creado exitosamente con ID: {}", guardado.getId());
        return estadoPrestamoMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "estados-prestamo-activos", allEntries = true)
    public EstadoPrestamoResponse actualizar(Long id, EstadoPrestamoRequest request) {
        log.info("Actualizando estado de préstamo con ID: {}", id);
        EstadoPrestamo estadoPrestamoExistente = estadoPrestamoRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un estado de préstamo con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, estadoPrestamoExistente);

        estadoPrestamoMapper.updateEntity(request, estadoPrestamoExistente);
        estadoPrestamoExistente.setNombre(nombreTrimmed);

        EstadoPrestamo actualizado = estadoPrestamoRepository.save(estadoPrestamoExistente);
        log.info("Estado de préstamo con ID: {} actualizado exitosamente", id);
        return estadoPrestamoMapper.toResponse(actualizado);
    }

    private void validarUnicidad(String nombreNuevo, EstadoPrestamo estadoPrestamoExistente) {
        boolean esNuevo = (estadoPrestamoExistente == null);
        boolean nombreCambio = !esNuevo && !estadoPrestamoExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && estadoPrestamoRepository.existsByNombreIgnoreCase(nombreNuevo)) {
            log.warn("Intento de duplicar estado de préstamo: {}", nombreNuevo);
            throw new ValidacionNegocioException("Ya existe un estado de préstamo con el nombre: " + nombreNuevo);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "estados-prestamo-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de estado de préstamo ID: {} a {}", id, !estado);
        if (!estadoPrestamoRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró un estado de préstamo con ID: " + id);
        }
        if (estado) {
            estadoPrestamoRepository.activar(id);
        } else {
            estadoPrestamoRepository.desactivar(id);
        }
    }
}

