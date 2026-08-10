package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.NacionalidadRequest;
import com.arrupeteca.dto.response.NacionalidadResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.NacionalidadMapper;
import com.arrupeteca.persistence.entity.Nacionalidad;
import com.arrupeteca.persistence.repository.NacionalidadRepository;
import com.arrupeteca.service.NacionalidadService;
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
public class NacionalidadServiceImpl implements NacionalidadService {

    private final NacionalidadRepository nacionalidadRepository;
    private final NacionalidadMapper nacionalidadMapper;

    @Override
    @Cacheable(value = "nacionalidades-activas", key = "#nombre != null ? #nombre : 'todos'")
    public List<NacionalidadResponse> obtenerTodosActivos(String nombre) {
        log.debug("Obteniendo nacionalidades activos(as) (filtro nombre: '{}')", nombre);
        String filtro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return nacionalidadRepository.buscarActivosPorNombre(filtro).stream()
                .map(nacionalidadMapper::toResponse)
                .toList();
    }

    @Override
    public Page<NacionalidadResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable) {
        log.debug("Búsqueda de nacionalidades con filtros - nombre: {}, borradoLogico: {}, pageable: {}", nombre, borradoLogico, pageable);
        String nombreFiltro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return nacionalidadRepository.busquedaAdmin(nombreFiltro, borradoLogico, pageable)
                .map(nacionalidadMapper::toResponse);
    }

    @Override
    public NacionalidadResponse obtenerPorId(Long id) {
        log.debug("Buscando nacionalidades por ID: {}", id);
        return nacionalidadRepository.findById(id)
                .map(nacionalidadMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) nacionalidad con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "nacionalidades-activas", allEntries = true)
    public NacionalidadResponse crear(NacionalidadRequest request) {
        log.info("Creando nuevo(a) nacionalidad con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, null);

        Nacionalidad nacionalidad = nacionalidadMapper.toEntity(request);
        nacionalidad.setNombre(nombreTrimmed);

        Nacionalidad guardado = nacionalidadRepository.save(nacionalidad);
        log.info("Nacionalidad creado(a) exitosamente con ID: {}", guardado.getId());
        return nacionalidadMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "nacionalidades-activas", allEntries = true)
    public NacionalidadResponse actualizar(Long id, NacionalidadRequest request) {
        log.info("Actualizando nacionalidad con ID: {}", id);
        Nacionalidad nacionalidadExistente = nacionalidadRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) nacionalidad con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, nacionalidadExistente);

        nacionalidadMapper.updateEntity(request, nacionalidadExistente);
        nacionalidadExistente.setNombre(nombreTrimmed);

        Nacionalidad actualizado = nacionalidadRepository.save(nacionalidadExistente);
        log.info("Nacionalidad con ID: {} actualizado(a) exitosamente", id);
        return nacionalidadMapper.toResponse(actualizado);
    }

    private void validarUnicidad(String nombreNuevo, Nacionalidad nacionalidadExistente) {
        boolean esNuevo = (nacionalidadExistente == null);
        boolean nombreCambio = !esNuevo && !nacionalidadExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && nacionalidadRepository.existsByNombreIgnoreCase(nombreNuevo)) {
            log.warn("Intento de duplicar nacionalidades: {}", nombreNuevo);
            throw new ValidacionNegocioException("Ya existe un(a) nacionalidad con el nombre: " + nombreNuevo);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "nacionalidades-activas", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de nacionalidades ID: {} a {}", id, !estado);
        if (!nacionalidadRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró un(a) nacionalidad con ID: " + id);
        }
        if (estado) {
            nacionalidadRepository.activar(id);
        } else {
            nacionalidadRepository.desactivar(id);
        }
    }}
