package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.TipoAutoriaRequest;
import com.arrupeteca.dto.response.TipoAutoriaResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.TipoAutoriaMapper;
import com.arrupeteca.persistence.entity.TipoAutoria;
import com.arrupeteca.persistence.repository.TipoAutoriaRepository;
import com.arrupeteca.service.TipoAutoriaService;
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
public class TipoAutoriaServiceImpl implements TipoAutoriaService {

    private final TipoAutoriaRepository tipoAutoriaRepository;
    private final TipoAutoriaMapper tipoAutoriaMapper;

    @Override
    @Cacheable(value = "tipos-autoria-activos", key = "#nombre != null ? #nombre : 'todos'")
    public List<TipoAutoriaResponse> obtenerTodosActivos(String nombre) {
        log.debug("Obteniendo tipos de autoría activos(as) (filtro nombre: '{}')", nombre);
        String filtro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return tipoAutoriaRepository.buscarActivosPorNombre(filtro).stream()
                .map(tipoAutoriaMapper::toResponse)
                .toList();
    }

    @Override
    public Page<TipoAutoriaResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable) {
        log.debug("Búsqueda de tipos de autoría con filtros - nombre: {}, borradoLogico: {}, pageable: {}", nombre, borradoLogico, pageable);
        String nombreFiltro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return tipoAutoriaRepository.busquedaAdmin(nombreFiltro, borradoLogico, pageable)
                .map(tipoAutoriaMapper::toResponse);
    }

    @Override
    public TipoAutoriaResponse obtenerPorId(Long id) {
        log.debug("Buscando tipos de autoría por ID: {}", id);
        return tipoAutoriaRepository.findById(id)
                .map(tipoAutoriaMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) tipoautoria con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "tipos-autoria-activos", allEntries = true)
    public TipoAutoriaResponse crear(TipoAutoriaRequest request) {
        log.info("Creando nuevo(a) tipoautoria con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, null);

        TipoAutoria tipoAutoria = tipoAutoriaMapper.toEntity(request);
        tipoAutoria.setNombre(nombreTrimmed);

        TipoAutoria guardado = tipoAutoriaRepository.save(tipoAutoria);
        log.info("TipoAutoria creado(a) exitosamente con ID: {}", guardado.getId());
        return tipoAutoriaMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "tipos-autoria-activos", allEntries = true)
    public TipoAutoriaResponse actualizar(Long id, TipoAutoriaRequest request) {
        log.info("Actualizando tipoautoria con ID: {}", id);
        TipoAutoria tipoAutoriaExistente = tipoAutoriaRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) tipoautoria con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, tipoAutoriaExistente);

        tipoAutoriaMapper.updateEntity(request, tipoAutoriaExistente);
        tipoAutoriaExistente.setNombre(nombreTrimmed);

        TipoAutoria actualizado = tipoAutoriaRepository.save(tipoAutoriaExistente);
        log.info("TipoAutoria con ID: {} actualizado(a) exitosamente", id);
        return tipoAutoriaMapper.toResponse(actualizado);
    }

    private void validarUnicidad(String nombreNuevo, TipoAutoria tipoAutoriaExistente) {
        boolean esNuevo = (tipoAutoriaExistente == null);
        boolean nombreCambio = !esNuevo && !tipoAutoriaExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && tipoAutoriaRepository.existsByNombreIgnoreCase(nombreNuevo)) {
            log.warn("Intento de duplicar tipos de autoría: {}", nombreNuevo);
            throw new ValidacionNegocioException("Ya existe un(a) tipoautoria con el nombre: " + nombreNuevo);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "tipos-autoria-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de tipos de autoría ID: {} a {}", id, !estado);
        if (!tipoAutoriaRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró un(a) tipoautoria con ID: " + id);
        }
        if (estado) {
            tipoAutoriaRepository.activar(id);
        } else {
            tipoAutoriaRepository.desactivar(id);
        }
    }}
