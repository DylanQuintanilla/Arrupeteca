package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.GeneroRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.GeneroMapper;
import com.arrupeteca.persistence.entity.Genero;
import com.arrupeteca.persistence.projection.GeneroResumen;
import com.arrupeteca.persistence.repository.GeneroRepository;
import com.arrupeteca.service.GeneroService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GeneroServiceImpl implements GeneroService {

    private final GeneroRepository generoRepository;
    private final GeneroMapper generoMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<GeneroResumen> obtenerTodosActivos(Pageable pageable) {
        return generoRepository.findByBorradoLogicoFalse(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public GeneroResumen obtenerPorIdActivo(Long id) {
        return generoRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new ValidacionNegocioException("No existe un género con el ID: " + id + ", o no está activo"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GeneroResumen> obtenerTodos(Pageable pageable) {
        return generoRepository.findAllProjectedBy(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public GeneroResumen obtenerPorId(Long id) {
        return generoRepository.findProjectedById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No existe un género con el ID: " + id));
    }

    @Override
    @Transactional
    public GeneroResumen crear(GeneroRequest request) {
        if (generoRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe un género con el nombre: " + request.getNombre());
        }

        Genero generoMapeado = generoMapper.toEntity(request);
        Genero generoGuardado = generoRepository.save(generoMapeado);

        return obtenerPorId(generoGuardado.getId());
    }

    @Override
    @Transactional
    public GeneroResumen actualizar(Long id, GeneroRequest request) {
        Genero generoExistente = generoRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No existe un género con el ID: " + id));

        if (!generoExistente.getNombre().equalsIgnoreCase(request.getNombre())
                && generoRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe un género con el nombre: " + request.getNombre());
        }

        generoMapper.updateEntity(request, generoExistente);
        generoRepository.save(generoExistente);
        return obtenerPorId(generoExistente.getId());
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!generoRepository.existsById(id)) {
            throw new ValidacionNegocioException("No existe un género con el ID: " + id);
        }

        if (estado) {
            generoRepository.activar(id);
        } else {
            generoRepository.desactivar(id);
        }
    }
}
