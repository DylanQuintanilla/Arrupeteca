package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.NacionalidadRequest;
import com.arrupeteca.mapper.NacionalidadMapper;
import com.arrupeteca.persistence.entity.Nacionalidad;
import com.arrupeteca.persistence.projection.NacionalidadResumen;
import com.arrupeteca.persistence.repository.NacionalidadRepository;
import com.arrupeteca.service.NacionalidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor //Crea la inyeccion por constructor de manera automatica
public class NacionalidadServiceImpl implements NacionalidadService {

    //Obliga si o si poner final
    private final NacionalidadRepository nacionalidadRepository;
    private final NacionalidadMapper nacionalidadMapper;

    @Override
    @Transactional(readOnly = true)
    public List<NacionalidadResumen> obtenerTodasActivas() {
        return nacionalidadRepository.findByBorradoLogicoFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public NacionalidadResumen obtenerPorIdActiva(Long id) {
        return nacionalidadRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new RuntimeException("La nacionalidad con ID " + id + " no existe o está inactiva"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NacionalidadResumen> obtenerTodas() {
        return nacionalidadRepository.findAllProjectedBy();
    }

    @Override
    @Transactional(readOnly = true)
    public NacionalidadResumen obtenerPorId(Long id) {
        return nacionalidadRepository.findProjectedById(id)
                .orElseThrow(() -> new RuntimeException("La nacionalidad con ID " + id + " no existe"));
    }

    @Override
    @Transactional
    public NacionalidadResumen crear(NacionalidadRequest request) {
        if (nacionalidadRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new RuntimeException("Ya existe una nacionalidad con el nombre: " + request.getNombre());
        }

        Nacionalidad nuevaNacionalidad = nacionalidadMapper.toEntity(request);
        Nacionalidad nacionalidadGuardada = nacionalidadRepository.save(nuevaNacionalidad);

        return obtenerPorId(nacionalidadGuardada.getId());
    }

    @Override
    @Transactional
    public NacionalidadResumen actualizar(Long id, NacionalidadRequest request) {
        Nacionalidad nacionalidadExistente = nacionalidadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("La nacionalidad con ID " + id + " no existe"));

        if (!nacionalidadExistente.getNombre().equalsIgnoreCase(request.getNombre()) &&
                nacionalidadRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new RuntimeException("Ya existe otra nacionalidad con el nombre: " + request.getNombre());
        }

        nacionalidadMapper.updateEntity(request, nacionalidadExistente);
        Nacionalidad nacionalidadActualizada = nacionalidadRepository.save(nacionalidadExistente);

        return obtenerPorId(nacionalidadActualizada.getId());
    }

    @Override
    @Transactional
    public void cambiarEstadoLogico(Long id, boolean activar) {
        if (!nacionalidadRepository.existsById(id)) {
            throw new RuntimeException("La nacionalidad con ID " + id + " no existe");
        }

        if (activar) {
            nacionalidadRepository.activar(id);
        } else {
            nacionalidadRepository.desactivar(id);
        }
    }
}