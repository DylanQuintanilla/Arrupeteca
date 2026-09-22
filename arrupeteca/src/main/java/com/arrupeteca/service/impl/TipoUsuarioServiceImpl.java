package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.TipoUsuarioRequest;
import com.arrupeteca.dto.response.TipoUsuarioResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.TipoUsuarioMapper;
import com.arrupeteca.persistence.entity.TipoUsuario;
import com.arrupeteca.persistence.repository.TipoUsuarioRepository;
import com.arrupeteca.service.TipoUsuarioService;
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
public class TipoUsuarioServiceImpl implements TipoUsuarioService {

    private final TipoUsuarioRepository tipoUsuarioRepository;
    private final TipoUsuarioMapper tipoUsuarioMapper;

    @Override
    @Cacheable(value = "tipos-usuario-activos", key = "#nombre != null ? #nombre : 'todos'")
    public List<TipoUsuarioResponse> obtenerTodosActivos(String nombre) {
        log.debug("Obteniendo tipos de usuario activos(as) (filtro nombre: '{}')", nombre);
        String filtro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return tipoUsuarioRepository.buscarActivosPorNombre(filtro).stream()
                .map(tipoUsuarioMapper::toResponse)
                .toList();
    }

    @Override
    public Page<TipoUsuarioResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable) {
        log.debug("Búsqueda de tipos de usuario con filtros - nombre: {}, borradoLogico: {}, pageable: {}", nombre, borradoLogico, pageable);
        String nombreFiltro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return tipoUsuarioRepository.busquedaAdmin(nombreFiltro, borradoLogico, pageable)
                .map(tipoUsuarioMapper::toResponse);
    }

    @Override
    public TipoUsuarioResponse obtenerPorId(Long id) {
        log.debug("Buscando tipos de usuario por ID: {}", id);
        return tipoUsuarioRepository.findById(id)
                .map(tipoUsuarioMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) tipousuario con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "tipos-usuario-activos", allEntries = true)
    public TipoUsuarioResponse crear(TipoUsuarioRequest request) {
        log.info("Creando nuevo(a) tipousuario con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, null);

        TipoUsuario tipoUsuario = tipoUsuarioMapper.toEntity(request);
        tipoUsuario.setNombre(nombreTrimmed);

        TipoUsuario guardado = tipoUsuarioRepository.save(tipoUsuario);
        log.info("TipoUsuario creado(a) exitosamente con ID: {}", guardado.getId());
        return tipoUsuarioMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "tipos-usuario-activos", allEntries = true)
    public TipoUsuarioResponse actualizar(Long id, TipoUsuarioRequest request) {
        log.info("Actualizando tipousuario con ID: {}", id);
        TipoUsuario tipoUsuarioExistente = tipoUsuarioRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) tipousuario con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, tipoUsuarioExistente);

        tipoUsuarioMapper.updateEntity(request, tipoUsuarioExistente);
        tipoUsuarioExistente.setNombre(nombreTrimmed);

        TipoUsuario actualizado = tipoUsuarioRepository.save(tipoUsuarioExistente);
        log.info("TipoUsuario con ID: {} actualizado(a) exitosamente", id);
        return tipoUsuarioMapper.toResponse(actualizado);
    }

    private void validarUnicidad(String nombreNuevo, TipoUsuario tipoUsuarioExistente) {
        boolean esNuevo = (tipoUsuarioExistente == null);
        boolean nombreCambio = !esNuevo && !tipoUsuarioExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && tipoUsuarioRepository.existsByNombreIgnoreCase(nombreNuevo)) {
            log.warn("Intento de duplicar tipos de usuario: {}", nombreNuevo);
            throw new ValidacionNegocioException("Ya existe un(a) tipousuario con el nombre: " + nombreNuevo);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "tipos-usuario-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de tipos de usuario ID: {} a {}", id, !estado);
        if (!tipoUsuarioRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró un(a) tipousuario con ID: " + id);
        }
        if (estado) {
            tipoUsuarioRepository.activar(id);
        } else {
            tipoUsuarioRepository.desactivar(id);
        }
    }}
