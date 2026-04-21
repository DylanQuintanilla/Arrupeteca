package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.SalonRequest;
import com.arrupeteca.mapper.SalonMapper;
import com.arrupeteca.persistence.entity.Salon;
import com.arrupeteca.persistence.projection.SalonResumen;
import com.arrupeteca.persistence.repository.PlantaRepository;
import com.arrupeteca.persistence.repository.SalonRepository;
import com.arrupeteca.service.SalonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalonServiceImpl implements SalonService {

    private final SalonMapper salonMapper;
    private final SalonRepository salonRepository;
    private final PlantaRepository plantaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SalonResumen> obtenerTodosActivos() {
        return salonRepository.findByBorradoLogicoFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public SalonResumen obtenerPorIdActivo(Long id) {
        return salonRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new RuntimeException("No existe un salón activo con el ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalonResumen> obtenerTodos() {
        return salonRepository.findAllProjectedBy();
    }

    @Override
    @Transactional(readOnly = true)
    public SalonResumen obtenerPorId(Long id) {
        return salonRepository.findProjectedById(id)
                .orElseThrow(() -> new RuntimeException("No existe un salón con el ID: " + id));
    }

    @Override
    @Transactional
    public SalonResumen crear(SalonRequest request) {
        // Validación de unicidad dentro de la misma planta
        if (salonRepository.existsByNombreIgnoreCaseAndPlanta_Id(request.getNombre(), request.getIdPlanta())) {
            throw new RuntimeException("Ya existe el salón '" + request.getNombre() + "' en la planta indicada.");
        }

        Salon salonCreado = salonMapper.toEntity(request);
        asignarRelaciones(salonCreado, request);

        Salon salonGuardado = salonRepository.save(salonCreado);
        return obtenerPorId(salonGuardado.getId());
    }

    @Override
    @Transactional
    public SalonResumen actualizar(Long id, SalonRequest request) {
        Salon salonEncontrado = salonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró salón con ID: " + id));

        boolean cambioNombre = !salonEncontrado.getNombre().equalsIgnoreCase(request.getNombre());
        boolean cambioPlanta = !salonEncontrado.getPlanta().getId().equals(request.getIdPlanta());

        if ((cambioNombre || cambioPlanta) &&
                salonRepository.existsByNombreIgnoreCaseAndPlanta_Id(request.getNombre(), request.getIdPlanta())) {
            throw new RuntimeException("Ya existe un salón con el nombre '" + request.getNombre() + "' en esta planta.");
        }

        salonMapper.updateEntity(request, salonEncontrado);
        asignarRelaciones(salonEncontrado, request);

        Salon salonActualizado = salonRepository.save(salonEncontrado);
        return obtenerPorId(salonActualizado.getId());
    }

    @Override
    @Transactional
    public void cambiarBorradoLogico(Long id, Boolean estado) {
        if (!salonRepository.existsById(id)) {
            throw new RuntimeException("No se encontró salón con ID: " + id);
        }

        if (estado) {
            salonRepository.activarSalon(id);
        } else {
            salonRepository.desactivarSalon(id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalonResumen> obtenerPorIdPlanta(Long idPlanta) {
        if (!plantaRepository.existsById(idPlanta)) {
            throw new RuntimeException("No existe una planta con ID: " + idPlanta);
        }

        return salonRepository.findProjectedByPlanta_IdAndBorradoLogicoFalse(idPlanta);
    }

    //------------------

    private void asignarRelaciones(Salon salon, SalonRequest request) {
        if (request.getIdPlanta() != null) {
            salon.setPlanta(plantaRepository.findById(request.getIdPlanta())
                    .orElseThrow(() -> new RuntimeException("No existe una planta con ID: " + request.getIdPlanta())));
        }
    }
}