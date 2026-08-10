package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.GradoRequest;
import com.arrupeteca.dto.response.GradoResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.GradoMapper;
import com.arrupeteca.persistence.entity.Grado;
import com.arrupeteca.persistence.repository.GradoRepository;
import com.arrupeteca.service.GradoService;
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
public class GradoServiceImpl implements GradoService {

    private final GradoRepository gradoRepository;
    private final GradoMapper gradoMapper;

    @Override
    @Cacheable(value = "grados-activos", key = "#nombre != null ? #nombre : 'todos'")
    public List<GradoResponse> obtenerTodosActivos(String nombre) {
        log.debug("Obteniendo grados escolares activos(as) (filtro nombre: '{}')", nombre);
        String filtro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return gradoRepository.buscarActivosPorNombre(filtro).stream()
                .map(gradoMapper::toResponse)
                .toList();
    }

    @Override
    public Page<GradoResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable) {
        log.debug("Búsqueda de grados escolares con filtros - nombre: {}, borradoLogico: {}, pageable: {}", nombre, borradoLogico, pageable);
        String nombreFiltro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return gradoRepository.busquedaAdmin(nombreFiltro, borradoLogico, pageable)
                .map(gradoMapper::toResponse);
    }

    @Override
    public GradoResponse obtenerPorId(Long id) {
        log.debug("Buscando grados escolares por ID: {}", id);
        return gradoRepository.findById(id)
                .map(gradoMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) grado con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "grados-activos", allEntries = true)
    public GradoResponse crear(GradoRequest request) {
        log.info("Creando nuevo(a) grado con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, null);

        Grado grado = gradoMapper.toEntity(request);
        grado.setNombre(nombreTrimmed);

        Grado guardado = gradoRepository.save(grado);
        log.info("Grado creado(a) exitosamente con ID: {}", guardado.getId());
        return gradoMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "grados-activos", allEntries = true)
    public GradoResponse actualizar(Long id, GradoRequest request) {
        log.info("Actualizando grado con ID: {}", id);
        Grado gradoExistente = gradoRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) grado con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, gradoExistente);

        gradoMapper.updateEntity(request, gradoExistente);
        gradoExistente.setNombre(nombreTrimmed);

        Grado actualizado = gradoRepository.save(gradoExistente);
        log.info("Grado con ID: {} actualizado(a) exitosamente", id);
        return gradoMapper.toResponse(actualizado);
    }

    private void validarUnicidad(String nombreNuevo, Grado gradoExistente) {
        boolean esNuevo = (gradoExistente == null);
        boolean nombreCambio = !esNuevo && !gradoExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && gradoRepository.existsByNombreIgnoreCase(nombreNuevo)) {
            log.warn("Intento de duplicar grados escolares: {}", nombreNuevo);
            throw new ValidacionNegocioException("Ya existe un(a) grado con el nombre: " + nombreNuevo);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "grados-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de grados escolares ID: {} a {}", id, !estado);
        if (!gradoRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró un(a) grado con ID: " + id);
        }
        if (estado) {
            gradoRepository.activar(id);
        } else {
            gradoRepository.desactivar(id);
        }
    }}
