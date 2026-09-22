package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.GeneroRequest;
import com.arrupeteca.dto.response.GeneroResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.GeneroMapper;
import com.arrupeteca.persistence.entity.Genero;
import com.arrupeteca.persistence.repository.GeneroRepository;
import com.arrupeteca.service.GeneroService;
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
public class GeneroServiceImpl implements GeneroService {

    private final GeneroRepository generoRepository;
    private final GeneroMapper generoMapper;

    @Override
    @Cacheable(value = "generos-activos", key = "#nombre != null ? #nombre : 'todos'")
    public List<GeneroResponse> obtenerTodosActivos(String nombre) {
        log.debug("Obteniendo géneros literarios activos(as) (filtro nombre: '{}')", nombre);
        String filtro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return generoRepository.buscarActivosPorNombre(filtro).stream()
                .map(generoMapper::toResponse)
                .toList();
    }

    @Override
    public Page<GeneroResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable) {
        log.debug("Búsqueda de géneros literarios con filtros - nombre: {}, borradoLogico: {}, pageable: {}", nombre, borradoLogico, pageable);
        String nombreFiltro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return generoRepository.busquedaAdmin(nombreFiltro, borradoLogico, pageable)
                .map(generoMapper::toResponse);
    }

    @Override
    public GeneroResponse obtenerPorId(Long id) {
        log.debug("Buscando géneros literarios por ID: {}", id);
        return generoRepository.findById(id)
                .map(generoMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) genero con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "generos-activos", allEntries = true)
    public GeneroResponse crear(GeneroRequest request) {
        log.info("Creando nuevo(a) genero con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, null);

        Genero genero = generoMapper.toEntity(request);
        genero.setNombre(nombreTrimmed);

        Genero guardado = generoRepository.save(genero);
        log.info("Genero creado(a) exitosamente con ID: {}", guardado.getId());
        return generoMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "generos-activos", allEntries = true)
    public GeneroResponse actualizar(Long id, GeneroRequest request) {
        log.info("Actualizando genero con ID: {}", id);
        Genero generoExistente = generoRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) genero con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, generoExistente);

        generoMapper.updateEntity(request, generoExistente);
        generoExistente.setNombre(nombreTrimmed);

        Genero actualizado = generoRepository.save(generoExistente);
        log.info("Genero con ID: {} actualizado(a) exitosamente", id);
        return generoMapper.toResponse(actualizado);
    }

    private void validarUnicidad(String nombreNuevo, Genero generoExistente) {
        boolean esNuevo = (generoExistente == null);
        boolean nombreCambio = !esNuevo && !generoExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && generoRepository.existsByNombreIgnoreCase(nombreNuevo)) {
            log.warn("Intento de duplicar géneros literarios: {}", nombreNuevo);
            throw new ValidacionNegocioException("Ya existe un(a) genero con el nombre: " + nombreNuevo);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "generos-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de géneros literarios ID: {} a {}", id, !estado);
        if (!generoRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró un(a) genero con ID: " + id);
        }
        if (estado) {
            generoRepository.activar(id);
        } else {
            generoRepository.desactivar(id);
        }
    }}
