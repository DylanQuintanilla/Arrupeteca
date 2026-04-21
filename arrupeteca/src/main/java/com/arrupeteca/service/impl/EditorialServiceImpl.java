package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EditorialRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EditorialMapper;
import com.arrupeteca.persistence.entity.Editorial;
import com.arrupeteca.persistence.projection.EditorialResumen;
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
        return editorialRepository.findByBorradoLogicoFalse(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public EditorialResumen obtenerPorIdActivo(Long id) {
        return editorialRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró una Editorial con ID: "
                        + id + ", o no está activa"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EditorialResumen> obtenerTodos(Pageable pageable) {
        return editorialRepository.findAllProjectedBy(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public EditorialResumen obtenerPorId(Long id) {
        return editorialRepository.findProjectedById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró una Editorial con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EditorialResumen> busquedaMaestraAdmin(Boolean esBorrado, String termino, Pageable pageable) {
        return editorialRepository.busquedaMaestraAdmin(esBorrado, termino, pageable);
    }

    @Override
    @Transactional
    public EditorialResumen crear(EditorialRequest request) {
        if (editorialRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("El nombre: " + request.getNombre() + " ya ha sido utilizado");
        }
        if (editorialRepository.existsByCorreoIgnoreCase(request.getCorreo())) {
            throw new ValidacionNegocioException("El correo: " + request.getCorreo()
                    + " ya está registrado en otra editorial");
        }
        Editorial editorialMapeada = editorialMapper.toEntity(request);
        Editorial editorialGuardada = editorialRepository.save(editorialMapeada);
        return obtenerPorId(editorialGuardada.getId());
    }

    @Override
    @Transactional
    public EditorialResumen actualizar(Long id, EditorialRequest request) {
        Editorial editorialEncontrada = editorialRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Editorial con ID: " + id));

        if (!editorialEncontrada.getNombre().equalsIgnoreCase(request.getNombre())
                && editorialRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe una editorial con el nombre: " + request.getNombre());
        }

        if (request.getCorreo() != null && !editorialEncontrada.getCorreo().equalsIgnoreCase(request.getCorreo())
                && editorialRepository.existsByCorreoIgnoreCase(request.getCorreo())) {
            throw new ValidacionNegocioException("Ya existe una editorial con el correo: " + request.getCorreo());
        }

        editorialMapper.updateEntity(request, editorialEncontrada);
        editorialRepository.save(editorialEncontrada);

        return obtenerPorId(editorialEncontrada.getId());
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
