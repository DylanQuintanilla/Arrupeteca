package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.NacionalidadRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.NacionalidadMapper;
import com.arrupeteca.persistence.entity.Nacionalidad;
import com.arrupeteca.persistence.projection.NacionalidadResumen;
import com.arrupeteca.persistence.repository.NacionalidadRepository;
import com.arrupeteca.service.NacionalidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NacionalidadServiceImpl implements NacionalidadService {

    private final NacionalidadRepository nacionalidadRepository;
    private final NacionalidadMapper nacionalidadMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<NacionalidadResumen> obtenerTodasActivas(Pageable pageable) {
        return nacionalidadRepository.findByBorradoLogicoFalse(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public NacionalidadResumen obtenerPorIdActivo(Long id) {
        return nacionalidadRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new ValidacionNegocioException("La nacionalidad con ID "
                        + id + " no existe, o no está activa"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NacionalidadResumen> obtenerTodas(Pageable pageable) {
        return nacionalidadRepository.findAllProjectedBy(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public NacionalidadResumen obtenerPorId(Long id) {
        return nacionalidadRepository.findProjectedById(id)
                .orElseThrow(() -> new ValidacionNegocioException("La nacionalidad con ID " + id + " no existe"));
    }

    @Override
    @Transactional
    public NacionalidadResumen crear(NacionalidadRequest request) {
        if (nacionalidadRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe una nacionalidad con el nombre: " + request.getNombre());
        }

        Nacionalidad nuevaNacionalidad = nacionalidadMapper.toEntity(request);
        Nacionalidad nacionalidadGuardada = nacionalidadRepository.save(nuevaNacionalidad);

        return obtenerPorId(nacionalidadGuardada.getId());
    }

    @Override
    @Transactional
    public NacionalidadResumen actualizar(Long id, NacionalidadRequest request) {
        Nacionalidad nacionalidadExistente = nacionalidadRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("La nacionalidad con ID " + id + " no existe"));

        if (!nacionalidadExistente.getNombre().equalsIgnoreCase(request.getNombre())
                && nacionalidadRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe una nacionalidad con el nombre: " + request.getNombre());
        }

        nacionalidadMapper.updateEntity(request, nacionalidadExistente);
        nacionalidadRepository.save(nacionalidadExistente);

        return obtenerPorId(nacionalidadExistente.getId());
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean activar) {
        if (!nacionalidadRepository.existsById(id)) {
            throw new ValidacionNegocioException("La nacionalidad con ID " + id + " no existe");
        }

        if (activar) {
            nacionalidadRepository.activar(id);
        } else {
            nacionalidadRepository.desactivar(id);
        }
    }
}
