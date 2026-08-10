package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EtiquetaRequest;
import com.arrupeteca.dto.response.EtiquetaResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EtiquetaMapper;
import com.arrupeteca.persistence.entity.Etiqueta;
import com.arrupeteca.persistence.repository.EtiquetaRepository;
import com.arrupeteca.service.EtiquetaService;
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
public class EtiquetaServiceImpl implements EtiquetaService {

    private final EtiquetaRepository etiquetaRepository;
    private final EtiquetaMapper etiquetaMapper;

    @Override
    @Cacheable(value = "etiquetas-activas", key = "#nombre != null ? #nombre : 'todos'")
    public List<EtiquetaResponse> obtenerTodosActivos(String nombre) {
        log.debug("Obteniendo etiquetas de obras literarias activos(as) (filtro nombre: '{}')", nombre);
        String filtro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return etiquetaRepository.buscarActivosPorNombre(filtro).stream()
                .map(etiquetaMapper::toResponse)
                .toList();
    }

    @Override
    public Page<EtiquetaResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable) {
        log.debug("Búsqueda de etiquetas de obras literarias con filtros - nombre: {}, borradoLogico: {}, pageable: {}", nombre, borradoLogico, pageable);
        String nombreFiltro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return etiquetaRepository.busquedaAdmin(nombreFiltro, borradoLogico, pageable)
                .map(etiquetaMapper::toResponse);
    }

    @Override
    public EtiquetaResponse obtenerPorId(Long id) {
        log.debug("Buscando etiquetas de obras literarias por ID: {}", id);
        return etiquetaRepository.findById(id)
                .map(etiquetaMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) etiqueta con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "etiquetas-activas", allEntries = true)
    public EtiquetaResponse crear(EtiquetaRequest request) {
        log.info("Creando nuevo(a) etiqueta con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, null);

        Etiqueta etiqueta = etiquetaMapper.toEntity(request);
        etiqueta.setNombre(nombreTrimmed);

        Etiqueta guardado = etiquetaRepository.save(etiqueta);
        log.info("Etiqueta creado(a) exitosamente con ID: {}", guardado.getId());
        return etiquetaMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "etiquetas-activas", allEntries = true)
    public EtiquetaResponse actualizar(Long id, EtiquetaRequest request) {
        log.info("Actualizando etiqueta con ID: {}", id);
        Etiqueta etiquetaExistente = etiquetaRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) etiqueta con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, etiquetaExistente);

        etiquetaMapper.updateEntity(request, etiquetaExistente);
        etiquetaExistente.setNombre(nombreTrimmed);

        Etiqueta actualizado = etiquetaRepository.save(etiquetaExistente);
        log.info("Etiqueta con ID: {} actualizado(a) exitosamente", id);
        return etiquetaMapper.toResponse(actualizado);
    }

    private void validarUnicidad(String nombreNuevo, Etiqueta etiquetaExistente) {
        boolean esNuevo = (etiquetaExistente == null);
        boolean nombreCambio = !esNuevo && !etiquetaExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && etiquetaRepository.existsByNombreIgnoreCase(nombreNuevo)) {
            log.warn("Intento de duplicar etiquetas de obras literarias: {}", nombreNuevo);
            throw new ValidacionNegocioException("Ya existe un(a) etiqueta con el nombre: " + nombreNuevo);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "etiquetas-activas", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de etiquetas de obras literarias ID: {} a {}", id, !estado);
        if (!etiquetaRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró un(a) etiqueta con ID: " + id);
        }
        if (estado) {
            etiquetaRepository.activar(id);
        } else {
            etiquetaRepository.desactivar(id);
        }
    }}
