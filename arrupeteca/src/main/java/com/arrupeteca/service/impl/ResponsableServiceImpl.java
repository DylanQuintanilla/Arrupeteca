package com.arrupeteca.service.impl;

import java.util.List;

import com.arrupeteca.dto.request.ResponsableRequest;
import com.arrupeteca.dto.response.ResponsableResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.ResponsableMapper;
import com.arrupeteca.persistence.entity.Parentesco;
import com.arrupeteca.persistence.entity.Responsable;
import com.arrupeteca.persistence.entity.Usuario;
import com.arrupeteca.persistence.repository.ParentescoRepository;
import com.arrupeteca.persistence.repository.ResponsableRepository;
import com.arrupeteca.persistence.repository.UsuarioRepository;
import com.arrupeteca.service.ResponsableService;
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
public class ResponsableServiceImpl implements ResponsableService {

    private final ResponsableRepository responsableRepository;
    private final UsuarioRepository usuarioRepository;
    private final ParentescoRepository parentescoRepository;
    private final ResponsableMapper responsableMapper;

    @Override
    @Cacheable(value = "responsables-activos")
    public Page<ResponsableResponse> obtenerTodosActivos(Pageable pageable) {
        log.debug("Obteniendo todos los responsables activos con paginación: {}", pageable);
        return responsableRepository.findByBorradoLogicoFalse(pageable)
                .map(responsableMapper::toResponse);
    }

    @Override
    public ResponsableResponse obtenerPorIdActivo(Long id) {
        log.debug("Buscando responsable activo con ID: {}", id);
        return responsableRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .map(responsableMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un responsable activo con ID: " + id));
    }

    @Override
    public Page<ResponsableResponse> obtenerTodos(Pageable pageable) {
        log.debug("Obteniendo todos los responsables (incluyendo borrados) con paginación: {}", pageable);
        return responsableRepository.findAllProjectedBy(pageable)
                .map(responsableMapper::toResponse);
    }

    @Override
    public ResponsableResponse obtenerPorId(Long id) {
        log.debug("Buscando responsable con ID: {}", id);
        return responsableRepository.findProjectedById(id)
                .map(responsableMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un responsable con ID: " + id));
    }

    @Override
    public List<ResponsableResponse> obtenerPorUsuario(Long idUsuario) {
        log.debug("Buscando responsables del usuario ID: {}", idUsuario);
        return responsableRepository.findByUsuarioIdAndBorradoLogicoFalse(idUsuario).stream()
                .map(responsableMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = "responsables-activos", allEntries = true)
    public ResponsableResponse crear(ResponsableRequest request) {
        log.info("Creando nuevo responsable para usuario ID: {}", request.idUsuario());

        Usuario usuario = usuarioRepository.findById(request.idUsuario())
                .filter(u -> !u.isBorradoLogico())
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró el usuario con ID: " + request.idUsuario()));

        Parentesco parentesco = parentescoRepository.findById(request.idParentesco())
                .filter(p -> !p.isBorradoLogico())
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró el parentesco con ID: " + request.idParentesco()));

        Responsable responsable = responsableMapper.toEntity(request);
        responsable.setUsuario(usuario);
        responsable.setParentesco(parentesco);
        responsable.setNombres(request.nombres().trim());
        responsable.setApellidos(request.apellidos().trim());
        responsable.setCorreo(request.correo().trim().toLowerCase());
        responsable.setTelefono(request.telefono() != null ? request.telefono().trim() : null);
        responsable.setDui(request.dui() != null ? request.dui().trim() : null);

        Responsable guardado = responsableRepository.save(responsable);
        log.info("Responsable creado exitosamente con ID: {}", guardado.getId());
        return responsableMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "responsables-activos", allEntries = true)
    public ResponsableResponse actualizar(Long id, ResponsableRequest request) {
        log.info("Actualizando responsable con ID: {}", id);
        Responsable responsableExistente = responsableRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un responsable con ID: " + id));

        Usuario usuario = usuarioRepository.findById(request.idUsuario())
                .filter(u -> !u.isBorradoLogico())
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró el usuario con ID: " + request.idUsuario()));

        Parentesco parentesco = parentescoRepository.findById(request.idParentesco())
                .filter(p -> !p.isBorradoLogico())
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró el parentesco con ID: " + request.idParentesco()));

        responsableMapper.updateEntity(request, responsableExistente);
        responsableExistente.setUsuario(usuario);
        responsableExistente.setParentesco(parentesco);
        responsableExistente.setNombres(request.nombres().trim());
        responsableExistente.setApellidos(request.apellidos().trim());
        responsableExistente.setCorreo(request.correo().trim().toLowerCase());
        responsableExistente.setTelefono(request.telefono() != null ? request.telefono().trim() : null);
        responsableExistente.setDui(request.dui() != null ? request.dui().trim() : null);

        Responsable actualizado = responsableRepository.save(responsableExistente);
        log.info("Responsable con ID: {} actualizado exitosamente", id);
        return responsableMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "responsables-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de responsable ID: {} a {}", id, !estado);
        if (responsableRepository.countByIdIncludeDeleted(id) == 0) {
            throw new ValidacionNegocioException("No se encontró un responsable con ID: " + id);
        }
        if (estado) {
            responsableRepository.activar(id);
        } else {
            responsableRepository.desactivar(id);
        }
    }

}
