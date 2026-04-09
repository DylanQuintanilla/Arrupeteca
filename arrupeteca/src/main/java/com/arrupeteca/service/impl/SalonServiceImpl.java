package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.SalonRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.SalonMapper;
import com.arrupeteca.persistence.entity.Salon;
import com.arrupeteca.persistence.projection.SalonResumen;
import com.arrupeteca.persistence.repository.PlantaRepository;
import com.arrupeteca.persistence.repository.SalonRepository;
import com.arrupeteca.service.SalonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SalonServiceImpl implements SalonService {

    private final SalonMapper salonMapper;
    private final SalonRepository salonRepository;
    private final PlantaRepository plantaRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<SalonResumen> obtenerTodosActivos(Pageable pageable) {
        return salonRepository.findByBorradoLogicoFalse(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public SalonResumen obtenerPorIdActivo(Long id) {
        return salonRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new ValidacionNegocioException("No existe un salón activo con el ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SalonResumen> obtenerTodos(Pageable pageable) {
        return salonRepository.findAllProjectedBy(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public SalonResumen obtenerPorId(Long id) {
        return salonRepository.findProjectedById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No existe un salón con el ID: " + id));
    }

    @Override
    @Transactional
    public SalonResumen crear(SalonRequest request) {
        if (salonRepository.qExistePorNombreYPlantaId(request.getNombre(), request.getIdPlanta())) {
            throw new ValidacionNegocioException("Ya existe el salón '" + request.getNombre() + "' en la planta indicada.");
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
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró salón con ID: " + id));

        boolean cambioNombre = !salonEncontrado.getNombre().equalsIgnoreCase(request.getNombre());
        boolean cambioPlanta = salonEncontrado.getPlanta() == null || !salonEncontrado.getPlanta().getId().equals(request.getIdPlanta());

        if ((cambioNombre || cambioPlanta) &&
                salonRepository.qExistePorNombreYPlantaId(request.getNombre(), request.getIdPlanta())) {
            throw new ValidacionNegocioException("Ya existe un salón con el nombre '" + request.getNombre() + "' en esta planta.");
        }

        salonMapper.updateEntity(request, salonEncontrado);
        asignarRelaciones(salonEncontrado, request);

        salonRepository.save(salonEncontrado);
        return obtenerPorId(salonEncontrado.getId());
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!salonRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró salón con ID: " + id);
        }

        if (estado) {
            salonRepository.activarSalon(id);
        } else {
            salonRepository.desactivarSalon(id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SalonResumen> obtenerPorIdPlanta(Long idPlanta, Pageable pageable) {
        if (!plantaRepository.existsById(idPlanta)) {
            throw new ValidacionNegocioException("No existe una planta con ID: " + idPlanta);
        }

        return salonRepository.qBuscarPorPlantaIdYActivo(idPlanta, pageable);
    }

    private void asignarRelaciones(Salon salon, SalonRequest request) {
        if (request.getIdPlanta() != null) {
            salon.setPlanta(plantaRepository.findById(request.getIdPlanta())
                    .orElseThrow(() -> new ValidacionNegocioException("No existe una planta con ID: " + request.getIdPlanta())));
        }
    }
}
