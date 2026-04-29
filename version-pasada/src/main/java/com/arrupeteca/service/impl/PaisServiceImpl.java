package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.PaisRequest;
import com.arrupeteca.dto.response.summary.PaisResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.PaisMapper;
import com.arrupeteca.persistence.entity.Pais;
import com.arrupeteca.persistence.repository.PaisRepository;
import com.arrupeteca.service.PaisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaisServiceImpl implements PaisService {

    private final PaisRepository paisRepository;
    private final PaisMapper paisMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<PaisResumen> obtenerTodosActivos(Pageable pageable) {
        return paisRepository.findByBorradoLogicoFalse(pageable)
                .map(paisMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public PaisResumen obtenerPorIdActivo(Long id) {
        return paisRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .map(paisMapper::toResumen)
                .orElseThrow(() -> new ValidacionNegocioException("No existe un país activo con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaisResumen> obtenerTodos(Pageable pageable) {
        return paisRepository.findAllProjectedBy(pageable)
                .map(paisMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public PaisResumen obtenerPorId(Long id) {
        return paisRepository.findProjectedById(id)
                .map(paisMapper::toResumen)
                .orElseThrow(() -> new ValidacionNegocioException("No existe un país con ID: " + id));
    }

    @Override
    @Transactional
    public PaisResumen crear(PaisRequest request) {
        if (paisRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe un país con el nombre: " + request.getNombre());
        }
        Pais pais = paisMapper.toEntity(request);
        return paisMapper.toResumen(paisRepository.save(pais));
    }

    @Override
    @Transactional
    public PaisResumen actualizar(Long id, PaisRequest request) {
        Pais paisExistente = paisRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No existe un país con ID: " + id));

        if (!paisExistente.getNombre().equalsIgnoreCase(request.getNombre())
                && paisRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe otro país con el nombre: " + request.getNombre());
        }

        paisMapper.updateEntity(request, paisExistente);
        return paisMapper.toResumen(paisRepository.save(paisExistente));
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!paisRepository.existsById(id)) {
            throw new ValidacionNegocioException("No existe un país con ID: " + id);
        }
        if (estado) {
            paisRepository.activar(id);
        } else {
            paisRepository.desactivar(id);
        }
    }
}
