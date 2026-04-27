package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EditorialRequest;
import com.arrupeteca.dto.response.detail.EditorialDetalleResponse;
import com.arrupeteca.dto.response.summary.EditorialResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EditorialMapper;
import com.arrupeteca.persistence.entity.Editorial;
import com.arrupeteca.persistence.repository.EditorialRepository;
import com.arrupeteca.service.EditorialService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EditorialServiceImpl implements EditorialService {

    private final EditorialRepository editorialRepository;
    private final EditorialMapper editorialMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<EditorialResumen> obtenerTodosActivos(Pageable pageable) {
        return editorialRepository.findByBorradoLogicoFalse(pageable)
                .map(editorialMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public EditorialDetalleResponse obtenerPorIdActivo(Long id) {
        return editorialRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .map(editorialMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Editorial activa con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EditorialResumen> obtenerTodos(Pageable pageable) {
        return editorialRepository.findAllProjectedBy(pageable)
                .map(editorialMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public EditorialDetalleResponse obtenerPorId(Long id) {
        return editorialRepository.findProjectedById(id)
                .map(editorialMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Editorial con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EditorialResumen> busquedaMaestraAdmin(Boolean esBorrado, String termino, Pageable pageable) {
        return editorialRepository.busquedaMaestraAdmin(esBorrado, termino, pageable)
                .map(editorialMapper::toResumen);
    }

    @Override
    @Transactional
    public EditorialDetalleResponse crear(EditorialRequest request) {
        if (editorialRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe una editorial con el nombre: " + request.getNombre());
        }
        if (editorialRepository.existsByCorreoIgnoreCase(request.getCorreo())) {
            throw new ValidacionNegocioException("Ya existe una editorial con el correo: " + request.getCorreo());
        }
        Editorial editorial = editorialMapper.toEntity(request);
        return editorialMapper.toDetalleResponse(editorialRepository.save(editorial));
    }

    @Override
    @Transactional
    public EditorialDetalleResponse actualizar(Long id, EditorialRequest request) {
        Editorial editorialExistente = editorialRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Editorial con ID: " + id));

        if (!editorialExistente.getNombre().equalsIgnoreCase(request.getNombre())
                && editorialRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe otra editorial con el nombre: " + request.getNombre());
        }

        if (!editorialExistente.getCorreo().equalsIgnoreCase(request.getCorreo())
                && editorialRepository.existsByCorreoIgnoreCase(request.getCorreo())) {
            throw new ValidacionNegocioException("Ya existe otra editorial con el correo: " + request.getCorreo());
        }

        editorialMapper.updateEntity(request, editorialExistente);
        return editorialMapper.toDetalleResponse(editorialRepository.save(editorialExistente));
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!editorialRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró Editorial con ID: " + id);
        }
        if (estado) {
            editorialRepository.activar(id);
        } else {
            editorialRepository.desactivar(id);
        }
    }
}
