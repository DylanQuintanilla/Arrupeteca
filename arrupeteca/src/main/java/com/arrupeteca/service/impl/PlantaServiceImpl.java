package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.PlantaRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.PlantaMapper;
import com.arrupeteca.persistence.entity.Planta;
import com.arrupeteca.persistence.projection.PlantaResumen;
import com.arrupeteca.persistence.repository.EdificioRepository;
import com.arrupeteca.persistence.repository.PlantaRepository;
import com.arrupeteca.service.PlantaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlantaServiceImpl implements PlantaService {

    private final PlantaMapper plantaMapper;
    private final PlantaRepository plantaRepository;
    private final EdificioRepository edificioRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<PlantaResumen> obtenerTodosActivos(Pageable pageable) {
        return plantaRepository.findByBorradoLogicoFalse(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public PlantaResumen obtenerPorIdActivo(Long id) {
        return plantaRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new ValidacionNegocioException("No existe una planta activa con el ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlantaResumen> obtenerTodos(Pageable pageable) {
        return plantaRepository.findAllProjectedBy(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public PlantaResumen obtenerPorId(Long id) {
        return plantaRepository.findProjectedById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No existe una planta con el ID: " + id));
    }

    @Override
    @Transactional
    public PlantaResumen crear(PlantaRequest request) {
        if (plantaRepository.qExistePorNombreYEdificioId(request.getNombre(), request.getIdEdificio())) {
            throw new ValidacionNegocioException("Ya existe la planta '" + request.getNombre() + "' en el edificio indicado.");
        }

        Planta plantaCreada = plantaMapper.toEntity(request);
        asignarRelaciones(plantaCreada, request);

        Planta plantaGuardada = plantaRepository.save(plantaCreada);
        return obtenerPorId(plantaGuardada.getId());
    }

    @Override
    @Transactional
    public PlantaResumen actualizar(Long id, PlantaRequest request) {
        Planta plantaEncontrada = plantaRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró planta con ID: " + id));

        boolean cambioNombre = !plantaEncontrada.getNombre().equalsIgnoreCase(request.getNombre());
        boolean cambioEdificio = plantaEncontrada.getEdificio() == null || !plantaEncontrada.getEdificio().getId().equals(request.getIdEdificio());

        if ((cambioNombre || cambioEdificio) &&
                plantaRepository.qExistePorNombreYEdificioId(request.getNombre(), request.getIdEdificio())) {
            throw new ValidacionNegocioException("Ya existe una planta con el nombre '" + request.getNombre() + "' en este edificio.");
        }

        plantaMapper.updateEntity(request, plantaEncontrada);
        asignarRelaciones(plantaEncontrada, request);

        plantaRepository.save(plantaEncontrada);
        return obtenerPorId(plantaEncontrada.getId());
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!plantaRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró planta con ID: " + id);
        }

        if (estado) {
            plantaRepository.activarPlanta(id);
        } else {
            plantaRepository.desactivarPlanta(id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlantaResumen> obtenerPorIdEdificio(Long idEdificio, Pageable pageable) {
        if (!edificioRepository.existsById(idEdificio)) {
            throw new ValidacionNegocioException("No existe un edificio con ID: " + idEdificio);
        }

        return plantaRepository.qBuscarPorEdificioIdYActivo(idEdificio, pageable);
    }

    private void asignarRelaciones(Planta planta, PlantaRequest request) {
        if (request.getIdEdificio() != null) {
            planta.setEdificio(edificioRepository.findById(request.getIdEdificio())
                    .orElseThrow(() -> new ValidacionNegocioException("No existe un edificio con ID: " + request.getIdEdificio())));
        }
    }
}
