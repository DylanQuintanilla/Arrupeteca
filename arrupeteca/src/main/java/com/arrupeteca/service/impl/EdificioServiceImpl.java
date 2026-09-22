package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EdificioRequest;
import com.arrupeteca.dto.response.EdificioResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EdificioMapper;
import com.arrupeteca.persistence.entity.Edificio;
import com.arrupeteca.persistence.repository.EdificioRepository;
import com.arrupeteca.service.EdificioService;
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
public class EdificioServiceImpl implements EdificioService {

    private final EdificioRepository edificioRepository;
    private final EdificioMapper edificioMapper;

    @Override
    @Cacheable(value = "edificios-activos", key = "#nombre != null ? #nombre : 'todos'")
    public List<EdificioResponse> obtenerTodosActivos(String nombre) {
        log.debug("Obteniendo edificios activos(as) (filtro nombre: '{}')", nombre);
        String filtro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return edificioRepository.buscarActivosPorNombre(filtro).stream()
                .map(edificioMapper::toResponse)
                .toList();
    }

    @Override
    public Page<EdificioResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable) {
        log.debug("Búsqueda de edificios con filtros - nombre: {}, borradoLogico: {}, pageable: {}", nombre, borradoLogico, pageable);
        String nombreFiltro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return edificioRepository.busquedaAdmin(nombreFiltro, borradoLogico, pageable)
                .map(edificioMapper::toResponse);
    }

    @Override
    public EdificioResponse obtenerPorId(Long id) {
        log.debug("Buscando edificios por ID: {}", id);
        return edificioRepository.findById(id)
                .map(edificioMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) edificio con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "edificios-activos", allEntries = true)
    public EdificioResponse crear(EdificioRequest request) {
        log.info("Creando nuevo(a) edificio con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, null);

        Edificio edificio = edificioMapper.toEntity(request);
        edificio.setNombre(nombreTrimmed);

        Edificio guardado = edificioRepository.save(edificio);
        log.info("Edificio creado(a) exitosamente con ID: {}", guardado.getId());
        return edificioMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "edificios-activos", allEntries = true)
    public EdificioResponse actualizar(Long id, EdificioRequest request) {
        log.info("Actualizando edificio con ID: {}", id);
        Edificio edificioExistente = edificioRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) edificio con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, edificioExistente);

        edificioMapper.updateEntity(request, edificioExistente);
        edificioExistente.setNombre(nombreTrimmed);

        Edificio actualizado = edificioRepository.save(edificioExistente);
        log.info("Edificio con ID: {} actualizado(a) exitosamente", id);
        return edificioMapper.toResponse(actualizado);
    }

    private void validarUnicidad(String nombreNuevo, Edificio edificioExistente) {
        boolean esNuevo = (edificioExistente == null);
        boolean nombreCambio = !esNuevo && !edificioExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && edificioRepository.existsByNombreIgnoreCase(nombreNuevo)) {
            log.warn("Intento de duplicar edificios: {}", nombreNuevo);
            throw new ValidacionNegocioException("Ya existe un(a) edificio con el nombre: " + nombreNuevo);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "edificios-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de edificios ID: {} a {}", id, !estado);
        if (!edificioRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró un(a) edificio con ID: " + id);
        }
        if (estado) {
            edificioRepository.activar(id);
        } else {
            edificioRepository.desactivar(id);
        }
    }}
