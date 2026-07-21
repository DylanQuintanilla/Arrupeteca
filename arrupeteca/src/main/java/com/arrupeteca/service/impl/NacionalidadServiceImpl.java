package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.NacionalidadRequest;
import com.arrupeteca.dto.response.NacionalidadResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.NacionalidadMapper;
import com.arrupeteca.persistence.entity.Nacionalidad;
import com.arrupeteca.persistence.repository.NacionalidadRepository;
import com.arrupeteca.service.NacionalidadService;
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
public class NacionalidadServiceImpl implements NacionalidadService {

    private final NacionalidadRepository nacionalidadRepository;
    private final NacionalidadMapper nacionalidadMapper;

    @Override
    @Cacheable(value = "nacionalidades-activa")
    public Page<NacionalidadResponse> obtenerTodosActivos(Pageable pageable) {
        return nacionalidadRepository.findByBorradoLogicoFalse(pageable)
                .map(nacionalidadMapper::toResponse);
    }

    @Override
    public NacionalidadResponse obtenerPorIdActivo(Long id) {
        return nacionalidadRepository.findByIdAndBorradoLogicoFalse(id)
                .map(nacionalidadMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró una nacionalidad activa con ID: " + id));
    }

    @Override
    public Page<NacionalidadResponse> obtenerTodos(Pageable pageable) {
        return nacionalidadRepository.findAll(pageable)
                .map(nacionalidadMapper::toResponse);
    }

    @Override
    public NacionalidadResponse obtenerPorId(Long id) {
        return nacionalidadRepository.findById(id)
                .map(nacionalidadMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró una nacionalidad con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "nacionalidades-activa", allEntries = true)
    public NacionalidadResponse crear(NacionalidadRequest request) {
        if (nacionalidadRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ValidacionNegocioException("Ya existe una nacionalidad con el nombre: " + request.nombre());
        }
        Nacionalidad nacionalidad = nacionalidadMapper.toEntity(request);
        return nacionalidadMapper.toResponse(nacionalidadRepository.save(nacionalidad));
    }

    @Override
    @Transactional
    @CacheEvict(value = "nacionalidades-activa", allEntries = true)
    public NacionalidadResponse actualizar(Long id, NacionalidadRequest request) {
        Nacionalidad nacionalidadExistente = nacionalidadRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró una nacionalidad con ID: " + id));

        if (!nacionalidadExistente.getNombre().equalsIgnoreCase(request.nombre())
                && nacionalidadRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ValidacionNegocioException("Ya existe otra nacionalidad con el nombre: " + request.nombre());
        }

        nacionalidadMapper.updateEntity(request, nacionalidadExistente);
        return nacionalidadMapper.toResponse(nacionalidadRepository.save(nacionalidadExistente));
    }

    @Override
    @Transactional
    @CacheEvict(value = "nacionalidades-activa", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        if (nacionalidadRepository.countByIdIncludeDeleted(id) == 0) {
            throw new ValidacionNegocioException("No se encontró una nacionalidad con ID: " + id);
        }
        if (estado) {
            nacionalidadRepository.activar(id);
        } else {
            nacionalidadRepository.desactivar(id);
        }
    }
}
