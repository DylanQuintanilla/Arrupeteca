package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.PaisRequest;
import com.arrupeteca.dto.response.PaisResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.PaisMapper;
import com.arrupeteca.persistence.entity.Pais;
import com.arrupeteca.persistence.repository.PaisRepository;
import com.arrupeteca.service.PaisService;
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
public class PaisServiceImpl implements PaisService {

    private final PaisRepository paisRepository;
    private final PaisMapper paisMapper;

    @Override
    @Cacheable(value = "paises-activos", key = "#nombre != null ? #nombre : 'todos'")
    public List<PaisResponse> obtenerTodosActivos(String nombre) {
        log.debug("Obteniendo países activos(as) (filtro nombre: '{}')", nombre);
        String filtro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return paisRepository.buscarActivosPorNombre(filtro).stream()
                .map(paisMapper::toResponse)
                .toList();
    }

    @Override
    public Page<PaisResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable) {
        log.debug("Búsqueda de países con filtros - nombre: {}, borradoLogico: {}, pageable: {}", nombre, borradoLogico, pageable);
        String nombreFiltro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return paisRepository.busquedaAdmin(nombreFiltro, borradoLogico, pageable)
                .map(paisMapper::toResponse);
    }

    @Override
    public PaisResponse obtenerPorId(Long id) {
        log.debug("Buscando países por ID: {}", id);
        return paisRepository.findById(id)
                .map(paisMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) pais con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "paises-activos", allEntries = true)
    public PaisResponse crear(PaisRequest request) {
        log.info("Creando nuevo(a) pais con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, null);

        Pais pais = paisMapper.toEntity(request);
        pais.setNombre(nombreTrimmed);

        Pais guardado = paisRepository.save(pais);
        log.info("Pais creado(a) exitosamente con ID: {}", guardado.getId());
        return paisMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "paises-activos", allEntries = true)
    public PaisResponse actualizar(Long id, PaisRequest request) {
        log.info("Actualizando pais con ID: {}", id);
        Pais paisExistente = paisRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) pais con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, paisExistente);

        paisMapper.updateEntity(request, paisExistente);
        paisExistente.setNombre(nombreTrimmed);

        Pais actualizado = paisRepository.save(paisExistente);
        log.info("Pais con ID: {} actualizado(a) exitosamente", id);
        return paisMapper.toResponse(actualizado);
    }

    private void validarUnicidad(String nombreNuevo, Pais paisExistente) {
        boolean esNuevo = (paisExistente == null);
        boolean nombreCambio = !esNuevo && !paisExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && paisRepository.existsByNombreIgnoreCase(nombreNuevo)) {
            log.warn("Intento de duplicar países: {}", nombreNuevo);
            throw new ValidacionNegocioException("Ya existe un(a) pais con el nombre: " + nombreNuevo);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "paises-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de países ID: {} a {}", id, !estado);
        if (!paisRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró un(a) pais con ID: " + id);
        }
        if (estado) {
            paisRepository.activar(id);
        } else {
            paisRepository.desactivar(id);
        }
    }}
