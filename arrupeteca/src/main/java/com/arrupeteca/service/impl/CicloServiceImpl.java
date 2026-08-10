package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.CicloRequest;
import com.arrupeteca.dto.response.CicloResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.CicloMapper;
import com.arrupeteca.persistence.entity.Ciclo;
import com.arrupeteca.persistence.repository.CicloRepository;
import com.arrupeteca.service.CicloService;
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
public class CicloServiceImpl implements CicloService {

    private final CicloRepository cicloRepository;
    private final CicloMapper cicloMapper;

    @Override
    @Cacheable(value = "ciclos-activos", key = "#nombre != null ? #nombre : 'todos'")
    public List<CicloResponse> obtenerTodosActivos(String nombre) {
        log.debug("Obteniendo ciclos escolares activos(as) (filtro nombre: '{}')", nombre);
        String filtro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return cicloRepository.buscarActivosPorNombre(filtro).stream()
                .map(cicloMapper::toResponse)
                .toList();
    }

    @Override
    public Page<CicloResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable) {
        log.debug("Búsqueda de ciclos escolares con filtros - nombre: {}, borradoLogico: {}, pageable: {}", nombre, borradoLogico, pageable);
        String nombreFiltro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return cicloRepository.busquedaAdmin(nombreFiltro, borradoLogico, pageable)
                .map(cicloMapper::toResponse);
    }

    @Override
    public CicloResponse obtenerPorId(Long id) {
        log.debug("Buscando ciclos escolares por ID: {}", id);
        return cicloRepository.findById(id)
                .map(cicloMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) ciclo con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "ciclos-activos", allEntries = true)
    public CicloResponse crear(CicloRequest request) {
        log.info("Creando nuevo(a) ciclo con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, null);

        Ciclo ciclo = cicloMapper.toEntity(request);
        ciclo.setNombre(nombreTrimmed);

        Ciclo guardado = cicloRepository.save(ciclo);
        log.info("Ciclo creado(a) exitosamente con ID: {}", guardado.getId());
        return cicloMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "ciclos-activos", allEntries = true)
    public CicloResponse actualizar(Long id, CicloRequest request) {
        log.info("Actualizando ciclo con ID: {}", id);
        Ciclo cicloExistente = cicloRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) ciclo con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, cicloExistente);

        cicloMapper.updateEntity(request, cicloExistente);
        cicloExistente.setNombre(nombreTrimmed);

        Ciclo actualizado = cicloRepository.save(cicloExistente);
        log.info("Ciclo con ID: {} actualizado(a) exitosamente", id);
        return cicloMapper.toResponse(actualizado);
    }

    private void validarUnicidad(String nombreNuevo, Ciclo cicloExistente) {
        boolean esNuevo = (cicloExistente == null);
        boolean nombreCambio = !esNuevo && !cicloExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && cicloRepository.existsByNombreIgnoreCase(nombreNuevo)) {
            log.warn("Intento de duplicar ciclos escolares: {}", nombreNuevo);
            throw new ValidacionNegocioException("Ya existe un(a) ciclo con el nombre: " + nombreNuevo);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "ciclos-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de ciclos escolares ID: {} a {}", id, !estado);
        if (!cicloRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró un(a) ciclo con ID: " + id);
        }
        if (estado) {
            cicloRepository.activar(id);
        } else {
            cicloRepository.desactivar(id);
        }
    }}
