package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EditorialRequest;
import com.arrupeteca.dto.response.EditorialResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EditorialMapper;
import com.arrupeteca.persistence.entity.Editorial;
import com.arrupeteca.persistence.repository.EditorialRepository;
import com.arrupeteca.service.EditorialService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EditorialServiceImpl implements EditorialService {

    private final EditorialRepository editorialRepository;
    private final EditorialMapper editorialMapper;

    @Override
    @Cacheable(value = "editoriales-activas")
    public Page<EditorialResponse> obtenerTodosActivos(Pageable pageable) {
        return editorialRepository.findByBorradoLogicoFalse(pageable)
                .map(editorialMapper::toResponse);
    }

    @Override
    public EditorialResponse obtenerPorIdActivo(Long id) {
        return editorialRepository.findByIdAndBorradoLogicoFalse(id)
                .map(editorialMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Editorial activa con ID: " + id));
    }

    @Override
    public Page<EditorialResponse> obtenerTodos(Pageable pageable) {
        return editorialRepository.findAll(pageable)
                .map(editorialMapper::toResponse);
    }

    @Override
    public EditorialResponse obtenerPorId(Long id) {
        return editorialRepository.findById(id)
                .map(editorialMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Editorial con ID: " + id));
    }

    @Override
    public Page<EditorialResponse> busquedaMaestraAdmin(Boolean esBorrado, String termino, Pageable pageable) {
        return editorialRepository.busquedaMaestraAdmin(esBorrado, termino, pageable)
                .map(editorialMapper::toResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = "editoriales-activas", allEntries = true)
    public EditorialResponse crear(EditorialRequest request) {
        if (editorialRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ValidacionNegocioException("Ya existe una editorial con el nombre: " + request.nombre());
        }
        if (editorialRepository.existsByCorreoIgnoreCase(request.correo())) {
            throw new ValidacionNegocioException("Ya existe una editorial con el correo: " + request.correo());
        }
        Editorial editorial = editorialMapper.toEntity(request);
        return editorialMapper.toResponse(editorialRepository.save(editorial));
    }

    @Override
    @Transactional
    @CacheEvict(value = "editoriales-activas", allEntries = true)
    public EditorialResponse actualizar(Long id, EditorialRequest request) {
        Editorial editorialExistente = editorialRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Editorial con ID: " + id));

        if (!editorialExistente.getNombre().equalsIgnoreCase(request.nombre())
                && editorialRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ValidacionNegocioException("Ya existe otra editorial con el nombre: " + request.nombre());
        }

        if (!editorialExistente.getCorreo().equalsIgnoreCase(request.correo())
                && editorialRepository.existsByCorreoIgnoreCase(request.correo())) {
            throw new ValidacionNegocioException("Ya existe otra editorial con el correo: " + request.correo());
        }

        editorialMapper.updateEntity(request, editorialExistente);
        return editorialMapper.toResponse(editorialRepository.save(editorialExistente));
    }

    @Override
    @Transactional
    @CacheEvict(value = "editoriales-activas", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        if (editorialRepository.countByIdIncludeDeleted(id) == 0) {
            throw new ValidacionNegocioException("No se encontró Editorial con ID: " + id);
        }
        if (estado) {
            editorialRepository.activar(id);
        } else {
            editorialRepository.desactivar(id);
        }
    }
}
