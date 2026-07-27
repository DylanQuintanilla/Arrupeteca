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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TipoUsuarioServiceImpl implements TipoUsuarioService {

    private final TipoUsuarioRepository tipoUsuarioRepository;
    private final TipoUsuarioMapper tipoUsuarioMapper;

    @Override
    @Cacheable(value = "tipos-usuario-activos")
    public Page<TipoUsuarioResponse> obtenerTodosActivos(Pageable pageable) {
        log.debug("Obteniendo todos los tipos de usuario activos con paginación: {}", pageable);
        return tipoUsuarioRepository.findByBorradoLogicoFalse(pageable)
                .map(tipoUsuarioMapper::toResponse);
    }

    @Override
    public TipoUsuarioResponse obtenerPorIdActivo(Long id) {
        log.debug("Buscando tipo de usuario activo con ID: {}", id);
        return tipoUsuarioRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .map(tipoUsuarioMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un tipo de usuario activo con ID: " + id));
    }

    @Override
    public Page<TipoUsuarioResponse> obtenerTodos(Pageable pageable) {
        log.debug("Obteniendo todos los tipos de usuario (incluyendo borrados) con paginación: {}", pageable);
        return tipoUsuarioRepository.findAllProjectedBy(pageable)
                .map(tipoUsuarioMapper::toResponse);
    }

    @Override
    public TipoUsuarioResponse obtenerPorId(Long id) {
        log.debug("Buscando tipo de usuario con ID: {}", id);
        return tipoUsuarioRepository.findProjectedById(id)
                .map(tipoUsuarioMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un tipo de usuario con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "tipos-usuario-activos", allEntries = true)
    public TipoUsuarioResponse crear(TipoUsuarioRequest request) {
        log.info("Creando nuevo tipo de usuario con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, null);

        TipoUsuario tipoUsuario = tipoUsuarioMapper.toEntity(request);
        tipoUsuario.setNombre(nombreTrimmed);

        TipoUsuario guardado = tipoUsuarioRepository.save(tipoUsuario);
        log.info("Tipo de usuario creado exitosamente con ID: {}", guardado.getId());
        return tipoUsuarioMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "tipos-usuario-activos", allEntries = true)
    public TipoUsuarioResponse actualizar(Long id, TipoUsuarioRequest request) {
        log.info("Actualizando tipo de usuario con ID: {}", id);
        TipoUsuario tipoExistente = tipoUsuarioRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un tipo de usuario con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, tipoExistente);

        tipoUsuarioMapper.updateEntity(request, tipoExistente);
        tipoExistente.setNombre(nombreTrimmed);

        TipoUsuario actualizado = tipoUsuarioRepository.save(tipoExistente);
        log.info("Tipo de usuario con ID: {} actualizado exitosamente", id);
        return tipoUsuarioMapper.toResponse(actualizado);
    }

    private void validarUnicidad(String nombreNuevo, TipoUsuario tipoExistente) {
        boolean esNuevo = (tipoExistente == null);
        boolean nombreCambio = !esNuevo && !tipoExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && tipoUsuarioRepository.existsByNombreIgnoreCase(nombreNuevo)) {
            log.warn("Intento de duplicar tipo de usuario: {}", nombreNuevo);
            if (esNuevo) {
                throw new ValidacionNegocioException("Ya existe un tipo de usuario con el nombre: " + nombreNuevo);
            } else {
                throw new ValidacionNegocioException("Ya existe otro tipo de usuario con el nombre: " + nombreNuevo);
            }
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "tipos-usuario-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de tipo de usuario ID: {} a {}", id, !estado);
        if (tipoUsuarioRepository.countByIdIncludeDeleted(id) == 0) {
            throw new ValidacionNegocioException("No se encontró un tipo de usuario con ID: " + id);
        }
        if (estado) {
            tipoUsuarioRepository.activar(id);
        } else {
            tipoUsuarioRepository.desactivar(id);
        }
    }

}
