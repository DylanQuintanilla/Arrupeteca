package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.DisponibilidadRequest;
import com.arrupeteca.dto.response.DisponibilidadResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.DisponibilidadMapper;
import com.arrupeteca.persistence.entity.Disponibilidad;
import com.arrupeteca.persistence.repository.DisponibilidadRepository;
import com.arrupeteca.service.DisponibilidadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DisponibilidadServiceImpl implements DisponibilidadService {

    private final DisponibilidadRepository disponibilidadRepository;
    private final DisponibilidadMapper disponibilidadMapper;

    @Override
    @Cacheable(value = "disponibilidades-activas")
    public Page<DisponibilidadResponse> obtenerTodosActivos(Pageable pageable) {
        log.debug("Obteniendo todas las disponibilidades activas con paginación: {}", pageable);
        return disponibilidadRepository.findByBorradoLogicoFalse(pageable)
                .map(disponibilidadMapper::toResponse);
    }

    @Override
    public DisponibilidadResponse obtenerPorIdActivo(Long id) {
        log.debug("Buscando disponibilidad activa con ID: {}", id);
        return disponibilidadRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .map(disponibilidadMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró una disponibilidad activa con ID: " + id));
    }

    @Override
    public Page<DisponibilidadResponse> obtenerTodos(Pageable pageable) {
        log.debug("Obteniendo todas las disponibilidades (incluyendo borrados) con paginación: {}", pageable);
        return disponibilidadRepository.findAllProjectedBy(pageable)
                .map(disponibilidadMapper::toResponse);
    }

    @Override
    public DisponibilidadResponse obtenerPorId(Long id) {
        log.debug("Buscando disponibilidad con ID: {}", id);
        return disponibilidadRepository.findProjectedById(id)
                .map(disponibilidadMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró una disponibilidad con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "disponibilidades-activas", allEntries = true)
    public DisponibilidadResponse crear(DisponibilidadRequest request) {
        log.info("Creando nueva disponibilidad con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, null);

        Disponibilidad disponibilidad = disponibilidadMapper.toEntity(request);
        disponibilidad.setNombre(nombreTrimmed);

        Disponibilidad guardada = disponibilidadRepository.save(disponibilidad);
        log.info("Disponibilidad creada exitosamente con ID: {}", guardada.getId());
        return disponibilidadMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    @CacheEvict(value = "disponibilidades-activas", allEntries = true)
    public DisponibilidadResponse actualizar(Long id, DisponibilidadRequest request) {
        log.info("Actualizando disponibilidad con ID: {}", id);
        Disponibilidad disponibilidadExistente = disponibilidadRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró una disponibilidad con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, disponibilidadExistente);

        disponibilidadMapper.updateEntity(request, disponibilidadExistente);
        disponibilidadExistente.setNombre(nombreTrimmed);

        Disponibilidad actualizada = disponibilidadRepository.save(disponibilidadExistente);
        log.info("Disponibilidad con ID: {} actualizada exitosamente", id);
        return disponibilidadMapper.toResponse(actualizada);
    }

    private void validarUnicidad(String nombreNuevo, Disponibilidad disponibilidadExistente) {
        boolean esNuevo = (disponibilidadExistente == null);
        boolean nombreCambio = !esNuevo && !disponibilidadExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && disponibilidadRepository.existsByNombreIgnoreCase(nombreNuevo)) {
            log.warn("Intento de duplicar disponibilidad: {}", nombreNuevo);
            if (esNuevo) {
                throw new ValidacionNegocioException("Ya existe una disponibilidad con el nombre: " + nombreNuevo);
            } else {
                throw new ValidacionNegocioException("Ya existe otra disponibilidad con el nombre: " + nombreNuevo);
            }
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "disponibilidades-activas", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de disponibilidad ID: {} a {}", id, !estado);
        if (disponibilidadRepository.countByIdIncludeDeleted(id) == 0) {
            throw new ValidacionNegocioException("No se encontró una disponibilidad con ID: " + id);
        }
        if (estado) {
            disponibilidadRepository.activar(id);
        } else {
            disponibilidadRepository.desactivar(id);
        }
    }
}
