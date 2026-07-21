package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.PaisRequest;
import com.arrupeteca.dto.response.PaisResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.PaisMapper;
import com.arrupeteca.persistence.entity.Pais;
import com.arrupeteca.persistence.repository.PaisRepository;
import com.arrupeteca.service.PaisService;
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
public class PaisServiceImpl implements PaisService {

    private final PaisRepository paisRepository;
    private final PaisMapper paisMapper;

    @Override
    @Cacheable(value = "paises-activos")
    public Page<PaisResponse> obtenerTodosActivos(Pageable pageable) {
        return paisRepository.findByBorradoLogicoFalse(pageable)
                .map(paisMapper::toResponse);
    }

    @Override
    public PaisResponse obtenerPorIdActivo(Long id) {
        return paisRepository.findByIdAndBorradoLogicoFalse(id)
                .map(paisMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un país activo con ID: " + id));
    }

    @Override
    public Page<PaisResponse> obtenerTodos(Pageable pageable) {
        return paisRepository.findAll(pageable)
                .map(paisMapper::toResponse);
    }

    @Override
    public PaisResponse obtenerPorId(Long id) {
        return paisRepository.findById(id)
                .map(paisMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un país con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "paises-activos", allEntries = true)
    public PaisResponse crear(PaisRequest request) {
        if (paisRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ValidacionNegocioException("Ya existe un país con el nombre: " + request.nombre());
        }
        Pais pais = paisMapper.toEntity(request);
        return paisMapper.toResponse(paisRepository.save(pais));
    }

    @Override
    @Transactional
    @CacheEvict(value = "paises-activos", allEntries = true)
    public PaisResponse actualizar(Long id, PaisRequest request) {
        Pais paisExistente = paisRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un país con ID: " + id));

        if (!paisExistente.getNombre().equalsIgnoreCase(request.nombre())
                && paisRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ValidacionNegocioException("Ya existe otro país con el nombre: " + request.nombre());
        }

        paisMapper.updateEntity(request, paisExistente);
        return paisMapper.toResponse(paisRepository.save(paisExistente));
    }

    @Override
    @Transactional
    @CacheEvict(value = "paises-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        if (paisRepository.countByIdIncludeDeleted(id) == 0) {
            throw new ValidacionNegocioException("No se encontró un país con ID: " + id);
        }
        if (estado) {
            paisRepository.activar(id);
        } else {
            paisRepository.desactivar(id);
        }
    }
}
