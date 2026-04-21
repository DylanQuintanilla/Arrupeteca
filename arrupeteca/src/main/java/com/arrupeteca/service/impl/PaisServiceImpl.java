package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.PaisRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.PaisMapper;
import com.arrupeteca.persistence.entity.Pais;
import com.arrupeteca.persistence.projection.PaisResumen;
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
        return paisRepository.findByBorradoLogicoFalse(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public PaisResumen obtenerPorIdActivo(Long id) {
        return paisRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new ValidacionNegocioException("No existe un país con ID: "
                        + id + ", o no está activo"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaisResumen> obtenerTodos(Pageable pageable) {
        return paisRepository.findAllProjectedBy(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public PaisResumen obtenerPorId(Long id) {
        return paisRepository.findProjectedById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No existe un país con ID: " + id));
    }

    @Override
    @Transactional
    public PaisResumen crear(PaisRequest paisRequest) {
        if (paisRepository.existsByNombreIgnoreCase(paisRequest.getNombre())) {
            throw new ValidacionNegocioException("Ya existe un país con el nombre: " + paisRequest.getNombre());
        }

        Pais paisNuevo = paisMapper.toEntity(paisRequest);
        Pais paisGuardado = paisRepository.save(paisNuevo);
        return obtenerPorId(paisGuardado.getId());
    }

    @Override
    @Transactional
    public PaisResumen actualizar(Long id, PaisRequest request) {
        Pais paisExistente = paisRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encuentra el país con ID: " + id));

        if (!paisExistente.getNombre().equalsIgnoreCase(request.getNombre())
                && paisRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe otro país con el nombre: " + request.getNombre());
        }

        paisMapper.updateEntity(request, paisExistente);
        paisRepository.save(paisExistente);

        return obtenerPorId(paisExistente.getId());
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!paisRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encuentra el país con ID: " + id);
        }

        if (estado) {
            paisRepository.activar(id);
        } else {
            paisRepository.desactivar(id);
        }
    }
}
