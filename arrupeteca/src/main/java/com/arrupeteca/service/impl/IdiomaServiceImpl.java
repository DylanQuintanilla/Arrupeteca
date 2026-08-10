package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.IdiomaRequest;
import com.arrupeteca.dto.response.IdiomaResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.IdiomaMapper;
import com.arrupeteca.persistence.entity.Idioma;
import com.arrupeteca.persistence.repository.IdiomaRepository;
import com.arrupeteca.service.IdiomaService;
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
public class IdiomaServiceImpl implements IdiomaService {

    private final IdiomaRepository idiomaRepository;
    private final IdiomaMapper idiomaMapper;

    @Override
    @Cacheable(value = "idiomas-activos", key = "#nombre != null ? #nombre : 'todos'")
    public List<IdiomaResponse> obtenerTodosActivos(String nombre) {
        log.debug("Obteniendo idiomas activos(as) (filtro nombre: '{}')", nombre);
        String filtro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return idiomaRepository.buscarActivosPorNombre(filtro).stream()
                .map(idiomaMapper::toResponse)
                .toList();
    }

    @Override
    public Page<IdiomaResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable) {
        log.debug("Búsqueda de idiomas con filtros - nombre: {}, borradoLogico: {}, pageable: {}", nombre, borradoLogico, pageable);
        String nombreFiltro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return idiomaRepository.busquedaAdmin(nombreFiltro, borradoLogico, pageable)
                .map(idiomaMapper::toResponse);
    }

    @Override
    public IdiomaResponse obtenerPorId(Long id) {
        log.debug("Buscando idiomas por ID: {}", id);
        return idiomaRepository.findById(id)
                .map(idiomaMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) idioma con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "idiomas-activos", allEntries = true)
    public IdiomaResponse crear(IdiomaRequest request) {
        log.info("Creando nuevo(a) idioma con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, null);

        Idioma idioma = idiomaMapper.toEntity(request);
        idioma.setNombre(nombreTrimmed);

        Idioma guardado = idiomaRepository.save(idioma);
        log.info("Idioma creado(a) exitosamente con ID: {}", guardado.getId());
        return idiomaMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "idiomas-activos", allEntries = true)
    public IdiomaResponse actualizar(Long id, IdiomaRequest request) {
        log.info("Actualizando idioma con ID: {}", id);
        Idioma idiomaExistente = idiomaRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) idioma con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, idiomaExistente);

        idiomaMapper.updateEntity(request, idiomaExistente);
        idiomaExistente.setNombre(nombreTrimmed);

        Idioma actualizado = idiomaRepository.save(idiomaExistente);
        log.info("Idioma con ID: {} actualizado(a) exitosamente", id);
        return idiomaMapper.toResponse(actualizado);
    }

    private void validarUnicidad(String nombreNuevo, Idioma idiomaExistente) {
        boolean esNuevo = (idiomaExistente == null);
        boolean nombreCambio = !esNuevo && !idiomaExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && idiomaRepository.existsByNombreIgnoreCase(nombreNuevo)) {
            log.warn("Intento de duplicar idiomas: {}", nombreNuevo);
            throw new ValidacionNegocioException("Ya existe un(a) idioma con el nombre: " + nombreNuevo);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "idiomas-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de idiomas ID: {} a {}", id, !estado);
        if (!idiomaRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró un(a) idioma con ID: " + id);
        }
        if (estado) {
            idiomaRepository.activar(id);
        } else {
            idiomaRepository.desactivar(id);
        }
    }}
