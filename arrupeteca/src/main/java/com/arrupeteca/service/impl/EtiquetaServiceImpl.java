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
    @Cacheable(value = "etiquetas-activas")
    public List<EtiquetaResponse> obtenerTodosActivos() {
        log.debug("Obteniendo todas las etiquetas activas en lista plana");
        return etiquetaRepository.findByBorradoLogicoFalseOrderByNombreAsc().stream()
                .map(etiquetaMapper::toResponse)
                .toList();
    }

    @Override
    public Page<EtiquetaResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable) {
        log.debug("Búsqueda de etiquetas con filtros - nombre: {}, borradoLogico: {}, pageable: {}", nombre, borradoLogico, pageable);
        String nombreFiltro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return etiquetaRepository.busquedaAdmin(nombreFiltro, borradoLogico, pageable)
                .map(etiquetaMapper::toResponse);
    }

    @Override
    public EtiquetaResponse obtenerPorId(Long id) {
        log.debug("Buscando etiqueta por ID: {}", id);
        return etiquetaRepository.findById(id)
                .map(etiquetaMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró una etiqueta con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "etiquetas-activas", allEntries = true)
    public EtiquetaResponse crear(EtiquetaRequest request) {
        log.info("Creando nueva etiqueta con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, null);

        Etiqueta etiqueta = etiquetaMapper.toEntity(request);
        etiqueta.setNombre(nombreTrimmed);

        Etiqueta guardada = etiquetaRepository.save(etiqueta);
        log.info("Etiqueta creada exitosamente con ID: {}", guardada.getId());
        return etiquetaMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    @CacheEvict(value = "etiquetas-activas", allEntries = true)
    public EtiquetaResponse actualizar(Long id, EtiquetaRequest request) {
        log.info("Actualizando etiqueta con ID: {}", id);
        Etiqueta etiquetaExistente = etiquetaRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró una etiqueta con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, etiquetaExistente);

        etiquetaMapper.updateEntity(request, etiquetaExistente);
        etiquetaExistente.setNombre(nombreTrimmed);

        Etiqueta actualizada = etiquetaRepository.save(etiquetaExistente);
        log.info("Etiqueta con ID: {} actualizada exitosamente", id);
        return etiquetaMapper.toResponse(actualizada);
    }

    private void validarUnicidad(String nombreNuevo, Etiqueta etiquetaExistente) {
        boolean esNuevo = (etiquetaExistente == null);
        boolean nombreCambio = !esNuevo && !etiquetaExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && etiquetaRepository.existsByNombreIgnoreCase(nombreNuevo)) {
            log.warn("Intento de duplicar etiqueta: {}", nombreNuevo);
            throw new ValidacionNegocioException("Ya existe una etiqueta con el nombre: " + nombreNuevo);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "etiquetas-activas", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de etiqueta ID: {} a {}", id, !estado);
        if (etiquetaRepository.countByIdIncludeDeleted(id) == 0) {
            throw new ValidacionNegocioException("No se encontró una etiqueta con ID: " + id);
        }
        if (estado) {
            etiquetaRepository.activar(id);
        } else {
            etiquetaRepository.desactivar(id);
        }
    }
}
