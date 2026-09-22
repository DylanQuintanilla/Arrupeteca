package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.ParentescoRequest;
import com.arrupeteca.dto.response.ParentescoResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.ParentescoMapper;
import com.arrupeteca.persistence.entity.Parentesco;
import com.arrupeteca.persistence.repository.ParentescoRepository;
import com.arrupeteca.service.ParentescoService;
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
public class ParentescoServiceImpl implements ParentescoService {

    private final ParentescoRepository parentescoRepository;
    private final ParentescoMapper parentescoMapper;

    @Override
    @Cacheable(value = "parentescos-activos", key = "#nombre != null ? #nombre : 'todos'")
    public List<ParentescoResponse> obtenerTodosActivos(String nombre) {
        log.debug("Obteniendo parentescos activos(as) (filtro nombre: '{}')", nombre);
        String filtro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return parentescoRepository.buscarActivosPorNombre(filtro).stream()
                .map(parentescoMapper::toResponse)
                .toList();
    }

    @Override
    public Page<ParentescoResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable) {
        log.debug("Búsqueda de parentescos con filtros - nombre: {}, borradoLogico: {}, pageable: {}", nombre, borradoLogico, pageable);
        String nombreFiltro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return parentescoRepository.busquedaAdmin(nombreFiltro, borradoLogico, pageable)
                .map(parentescoMapper::toResponse);
    }

    @Override
    public ParentescoResponse obtenerPorId(Long id) {
        log.debug("Buscando parentescos por ID: {}", id);
        return parentescoRepository.findById(id)
                .map(parentescoMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) parentesco con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "parentescos-activos", allEntries = true)
    public ParentescoResponse crear(ParentescoRequest request) {
        log.info("Creando nuevo(a) parentesco con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, null);

        Parentesco parentesco = parentescoMapper.toEntity(request);
        parentesco.setNombre(nombreTrimmed);

        Parentesco guardado = parentescoRepository.save(parentesco);
        log.info("Parentesco creado(a) exitosamente con ID: {}", guardado.getId());
        return parentescoMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "parentescos-activos", allEntries = true)
    public ParentescoResponse actualizar(Long id, ParentescoRequest request) {
        log.info("Actualizando parentesco con ID: {}", id);
        Parentesco parentescoExistente = parentescoRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) parentesco con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, parentescoExistente);

        parentescoMapper.updateEntity(request, parentescoExistente);
        parentescoExistente.setNombre(nombreTrimmed);

        Parentesco actualizado = parentescoRepository.save(parentescoExistente);
        log.info("Parentesco con ID: {} actualizado(a) exitosamente", id);
        return parentescoMapper.toResponse(actualizado);
    }

    private void validarUnicidad(String nombreNuevo, Parentesco parentescoExistente) {
        boolean esNuevo = (parentescoExistente == null);
        boolean nombreCambio = !esNuevo && !parentescoExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && parentescoRepository.existsByNombreIgnoreCase(nombreNuevo)) {
            log.warn("Intento de duplicar parentescos: {}", nombreNuevo);
            throw new ValidacionNegocioException("Ya existe un(a) parentesco con el nombre: " + nombreNuevo);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "parentescos-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de parentescos ID: {} a {}", id, !estado);
        if (!parentescoRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró un(a) parentesco con ID: " + id);
        }
        if (estado) {
            parentescoRepository.activar(id);
        } else {
            parentescoRepository.desactivar(id);
        }
    }}
