package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.FormatoRequest;
import com.arrupeteca.dto.response.FormatoResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.FormatoMapper;
import com.arrupeteca.persistence.entity.Formato;
import com.arrupeteca.persistence.repository.FormatoRepository;
import com.arrupeteca.service.FormatoService;
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
public class FormatoServiceImpl implements FormatoService {

    private final FormatoRepository formatoRepository;
    private final FormatoMapper formatoMapper;

    @Override
    @Cacheable(value = "formatos-activos", key = "#nombre != null ? #nombre : 'todos'")
    public List<FormatoResponse> obtenerTodosActivos(String nombre) {
        log.debug("Obteniendo formatos de libros activos(as) (filtro nombre: '{}')", nombre);
        String filtro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return formatoRepository.buscarActivosPorNombre(filtro).stream()
                .map(formatoMapper::toResponse)
                .toList();
    }

    @Override
    public Page<FormatoResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable) {
        log.debug("Búsqueda de formatos de libros con filtros - nombre: {}, borradoLogico: {}, pageable: {}", nombre, borradoLogico, pageable);
        String nombreFiltro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return formatoRepository.busquedaAdmin(nombreFiltro, borradoLogico, pageable)
                .map(formatoMapper::toResponse);
    }

    @Override
    public FormatoResponse obtenerPorId(Long id) {
        log.debug("Buscando formatos de libros por ID: {}", id);
        return formatoRepository.findById(id)
                .map(formatoMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) formato con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "formatos-activos", allEntries = true)
    public FormatoResponse crear(FormatoRequest request) {
        log.info("Creando nuevo(a) formato con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, null);

        Formato formato = formatoMapper.toEntity(request);
        formato.setNombre(nombreTrimmed);

        Formato guardado = formatoRepository.save(formato);
        log.info("Formato creado(a) exitosamente con ID: {}", guardado.getId());
        return formatoMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "formatos-activos", allEntries = true)
    public FormatoResponse actualizar(Long id, FormatoRequest request) {
        log.info("Actualizando formato con ID: {}", id);
        Formato formatoExistente = formatoRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) formato con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, formatoExistente);

        formatoMapper.updateEntity(request, formatoExistente);
        formatoExistente.setNombre(nombreTrimmed);

        Formato actualizado = formatoRepository.save(formatoExistente);
        log.info("Formato con ID: {} actualizado(a) exitosamente", id);
        return formatoMapper.toResponse(actualizado);
    }

    private void validarUnicidad(String nombreNuevo, Formato formatoExistente) {
        boolean esNuevo = (formatoExistente == null);
        boolean nombreCambio = !esNuevo && !formatoExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && formatoRepository.existsByNombreIgnoreCase(nombreNuevo)) {
            log.warn("Intento de duplicar formatos de libros: {}", nombreNuevo);
            throw new ValidacionNegocioException("Ya existe un(a) formato con el nombre: " + nombreNuevo);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "formatos-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de formatos de libros ID: {} a {}", id, !estado);
        if (!formatoRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró un(a) formato con ID: " + id);
        }
        if (estado) {
            formatoRepository.activar(id);
        } else {
            formatoRepository.desactivar(id);
        }
    }}
