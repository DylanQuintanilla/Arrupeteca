package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.IdiomaRequest;
import com.arrupeteca.dto.response.detail.IdiomaDetalleResponse;
import com.arrupeteca.dto.response.summary.IdiomaResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.IdiomaMapper;
import com.arrupeteca.persistence.entity.Idioma;
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
        return idiomaRepository.findByBorradoLogicoFalse(pageable)
                .map(idiomaMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public IdiomaDetalleResponse obtenerPorIdActivo(Long id) {
        return idiomaRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .map(idiomaMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un Idioma activo con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IdiomaResumen> obtenerTodos(Pageable pageable) {
        return idiomaRepository.findAllProjectedBy(pageable)
                .map(idiomaMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public IdiomaDetalleResponse obtenerPorId(Long id) {
        return idiomaRepository.findProjectedById(id)
                .map(idiomaMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un Idioma con ID: " + id));
    }

    @Override
    @Transactional
    public IdiomaDetalleResponse crear(IdiomaRequest request) {
        if (idiomaRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe un idioma con el nombre: " + request.getNombre());
        }
        Idioma idioma = idiomaMapper.toEntity(request);
        return idiomaMapper.toDetalleResponse(idiomaRepository.save(idioma));
    }

    @Override
    @Transactional
    public IdiomaDetalleResponse actualizar(Long id, IdiomaRequest request) {
        Idioma idiomaExistente = idiomaRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un Idioma con ID: " + id));

        if (!idiomaExistente.getNombre().equalsIgnoreCase(request.getNombre())
                && idiomaRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe otro idioma con el nombre: " + request.getNombre());
        }

        idiomaMapper.updateEntity(request, idiomaExistente);
        return idiomaMapper.toDetalleResponse(idiomaRepository.save(idiomaExistente));
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!idiomaRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró un Idioma con ID: " + id);
        }
        if (estado) {
            idiomaRepository.activar(id);
        } else {
            idiomaRepository.desactivar(id);
        }
    }
}
