package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.IdiomaRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.IdiomaMapper;
import com.arrupeteca.persistence.entity.Idioma;
import com.arrupeteca.persistence.projection.IdiomaResumen;
import com.arrupeteca.persistence.repository.IdiomaRepository;
import com.arrupeteca.service.IdiomaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IdiomaServiceImpl implements IdiomaService {

    private final IdiomaRepository idiomaRepository;
    private final IdiomaMapper idiomaMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<IdiomaResumen> obtenerTodosActivos(Pageable pageable) {
        return idiomaRepository.findByBorradoLogicoFalse(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public IdiomaResumen obtenerPorIdActivo(Long id) {
        return idiomaRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un Idioma con ID: "
                        + id + ", o no está activo"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IdiomaResumen> obtenerTodos(Pageable pageable) {
        return idiomaRepository.findAllProjectedBy(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public IdiomaResumen obtenerPorId(Long id) {
        return idiomaRepository.findProjectedById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un Idioma con ID: " + id));
    }

    @Override
    @Transactional
    public IdiomaResumen crear(IdiomaRequest request) {
        if (idiomaRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("El nombre: " + request.getNombre() + " ya ha sido utilizado");
        }
        Idioma idiomaMapeado = idiomaMapper.toEntity(request);
        Idioma idiomaGuardado = idiomaRepository.save(idiomaMapeado);
        return obtenerPorId(idiomaGuardado.getId());
    }

    @Override
    @Transactional
    public IdiomaResumen actualizar(Long id, IdiomaRequest request) {
        Idioma idiomaEncontrado = idiomaRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Idioma con ID: " + id));

        if (!idiomaEncontrado.getNombre().equalsIgnoreCase(request.getNombre())
                && idiomaRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe un Idioma con el nombre: " + request.getNombre());
        }

        idiomaMapper.updateEntity(request, idiomaEncontrado);
        idiomaRepository.save(idiomaEncontrado);

        return obtenerPorId(idiomaEncontrado.getId());
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!idiomaRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró Idioma con ID: " + id);
        }
        if (estado) {
            idiomaRepository.activar(id);
        } else {
            idiomaRepository.desactivar(id);
        }
    }
}
