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

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DisponibilidadServiceImpl implements DisponibilidadService {

    private final DisponibilidadRepository disponibilidadRepository;
    private final DisponibilidadMapper disponibilidadMapper;

    @Override
    @Cacheable(value = "disponibilidades-activas", key = "#nombre != null ? #nombre : 'todos'")
    public List<DisponibilidadResponse> obtenerTodosActivos(String nombre) {
        log.debug("Obteniendo estados de disponibilidad de ejemplares activos(as) (filtro nombre: '{}')", nombre);
        String filtro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return disponibilidadRepository.buscarActivosPorNombre(filtro).stream()
                .map(disponibilidadMapper::toResponse)
                .toList();
    }

    @Override
    public Page<DisponibilidadResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable) {
        log.debug("Búsqueda de estados de disponibilidad de ejemplares con filtros - nombre: {}, borradoLogico: {}, pageable: {}", nombre, borradoLogico, pageable);
        String nombreFiltro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return disponibilidadRepository.busquedaAdmin(nombreFiltro, borradoLogico, pageable)
                .map(disponibilidadMapper::toResponse);
    }

    @Override
    public DisponibilidadResponse obtenerPorId(Long id) {
        log.debug("Buscando estados de disponibilidad de ejemplares por ID: {}", id);
        return disponibilidadRepository.findById(id)
                .map(disponibilidadMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) disponibilidad con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "disponibilidades-activas", allEntries = true)
    public DisponibilidadResponse crear(DisponibilidadRequest request) {
        log.info("Creando nuevo(a) disponibilidad con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, null);

        Disponibilidad disponibilidad = disponibilidadMapper.toEntity(request);
        disponibilidad.setNombre(nombreTrimmed);

        Disponibilidad guardado = disponibilidadRepository.save(disponibilidad);
        log.info("Disponibilidad creado(a) exitosamente con ID: {}", guardado.getId());
        return disponibilidadMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "disponibilidades-activas", allEntries = true)
    public DisponibilidadResponse actualizar(Long id, DisponibilidadRequest request) {
        log.info("Actualizando disponibilidad con ID: {}", id);
        Disponibilidad disponibilidadExistente = disponibilidadRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) disponibilidad con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, disponibilidadExistente);

        disponibilidadMapper.updateEntity(request, disponibilidadExistente);
        disponibilidadExistente.setNombre(nombreTrimmed);

        Disponibilidad actualizado = disponibilidadRepository.save(disponibilidadExistente);
        log.info("Disponibilidad con ID: {} actualizado(a) exitosamente", id);
        return disponibilidadMapper.toResponse(actualizado);
    }

    private void validarUnicidad(String nombreNuevo, Disponibilidad disponibilidadExistente) {
        boolean esNuevo = (disponibilidadExistente == null);
        boolean nombreCambio = !esNuevo && !disponibilidadExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && disponibilidadRepository.existsByNombreIgnoreCase(nombreNuevo)) {
            log.warn("Intento de duplicar estados de disponibilidad de ejemplares: {}", nombreNuevo);
            throw new ValidacionNegocioException("Ya existe un(a) disponibilidad con el nombre: " + nombreNuevo);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "disponibilidades-activas", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de estados de disponibilidad de ejemplares ID: {} a {}", id, !estado);
        if (!disponibilidadRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró un(a) disponibilidad con ID: " + id);
        }
        if (estado) {
            disponibilidadRepository.activar(id);
        } else {
            disponibilidadRepository.desactivar(id);
        }
    }}
