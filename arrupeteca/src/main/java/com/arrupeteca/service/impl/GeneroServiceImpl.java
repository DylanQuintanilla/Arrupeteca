package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.GeneroRequest;
import com.arrupeteca.dto.response.GeneroResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.GeneroMapper;
import com.arrupeteca.persistence.entity.Genero;
import com.arrupeteca.persistence.repository.GeneroRepository;
import com.arrupeteca.service.GeneroService;
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
public class GeneroServiceImpl implements GeneroService {

    private final GeneroRepository generoRepository;
    private final GeneroMapper generoMapper;

    @Override
    @Cacheable(value = "generos-activos")
    public Page<GeneroResponse> obtenerTodosActivos(Pageable pageable) {
        return generoRepository.findByBorradoLogicoFalse(pageable)
                .map(generoMapper::toResponse);
    }

    @Override
    public GeneroResponse obtenerPorIdActivo(Long id) {
        return generoRepository.findByIdAndBorradoLogicoFalse(id)
                .map(generoMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un género activo con ID: " + id));
    }

    @Override
    public Page<GeneroResponse> obtenerTodos(Pageable pageable) {
        return generoRepository.findAll(pageable)
                .map(generoMapper::toResponse);
    }

    @Override
    public GeneroResponse obtenerPorId(Long id) {
        return generoRepository.findById(id)
                .map(generoMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un género con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "generos-activos", allEntries = true)
    public GeneroResponse crear(GeneroRequest request) {
        if (generoRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ValidacionNegocioException("Ya existe un género con el nombre: " + request.nombre());
        }
        Genero genero = generoMapper.toEntity(request);
        return generoMapper.toResponse(generoRepository.save(genero));
    }

    @Override
    @Transactional
    @CacheEvict(value = "generos-activos", allEntries = true)
    public GeneroResponse actualizar(Long id, GeneroRequest request) {
        Genero generoExistente = generoRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un género con ID: " + id));

        if (!generoExistente.getNombre().equalsIgnoreCase(request.nombre())
                && generoRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ValidacionNegocioException("Ya existe otro género con el nombre: " + request.nombre());
        }

        generoMapper.updateEntity(request, generoExistente);
        return generoMapper.toResponse(generoRepository.save(generoExistente));
    }

    @Override
    @Transactional
    @CacheEvict(value = "generos-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        if (generoRepository.countByIdIncludeDeleted(id) == 0) {
            throw new ValidacionNegocioException("No se encontró un género con ID: " + id);
        }
        if (estado) {
            generoRepository.activar(id);
        } else {
            generoRepository.desactivar(id);
        }
    }
}
