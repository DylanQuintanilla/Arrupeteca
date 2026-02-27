package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.PlantaRequest;
import com.arrupeteca.mapper.PlantaMapper;
import com.arrupeteca.persistence.entity.Planta;
import com.arrupeteca.persistence.projection.PlantaResumen;
import com.arrupeteca.persistence.repository.EdificioRepository;
import com.arrupeteca.persistence.repository.PlantaRepository;
import com.arrupeteca.service.PlantaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlantaServiceImpl implements PlantaService {

    private final PlantaMapper plantaMapper;
    private final PlantaRepository plantaRepository;
    private final EdificioRepository edificioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PlantaResumen> obtenerTodosActivos() {
        return plantaRepository.findByBorradoLogicoFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public PlantaResumen obtenerPorIdActivo(Long id) {
        return plantaRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new RuntimeException("No existe una planta activa con el ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlantaResumen> obtenerTodos() {
        return plantaRepository.findAllProjectedBy();
    }

    @Override
    @Transactional(readOnly = true)
    public PlantaResumen obtenerPorId(Long id) {
        return plantaRepository.findProjectedById(id)
                .orElseThrow(() -> new RuntimeException("No existe una planta con el ID: " + id));
    }

    @Override
    @Transactional
    public PlantaResumen crear(PlantaRequest request) {
        if (plantaRepository.existsByNombreIgnoreCaseAndEdificio_Id(request.getNombre(), request.getIdEdificio())) {
            throw new RuntimeException("Ya existe la planta '" + request.getNombre() + "' en el edificio indicado.");
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
                .orElseThrow(() -> new RuntimeException("No se encontró planta con ID: " + id));

        boolean cambioNombre = !plantaEncontrada.getNombre().equalsIgnoreCase(request.getNombre());
        boolean cambioEdificio = !plantaEncontrada.getEdificio().getId().equals(request.getIdEdificio());

        if ((cambioNombre || cambioEdificio) &&
                plantaRepository.existsByNombreIgnoreCaseAndEdificio_Id(request.getNombre(), request.getIdEdificio())) {
            throw new RuntimeException("Ya existe una planta con el nombre '" + request.getNombre() + "' en este edificio.");
        }

        plantaMapper.updateEntity(request, plantaEncontrada);
        asignarRelaciones(plantaEncontrada, request);

        Planta plantaActualizada = plantaRepository.save(plantaEncontrada);
        return obtenerPorId(plantaActualizada.getId());
    }

    @Override
    @Transactional
    public void cambiarBorradoLogico(Long id, Boolean estado) {
        if (!plantaRepository.existsById(id)) {
            throw new RuntimeException("No se encontró planta con ID: " + id);
        }

        if (estado) {
            plantaRepository.activarPlanta(id);
        } else {
            plantaRepository.desactivarPlanta(id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlantaResumen> obtenerPorIdEdificio(Long idEdificio) {

        if(!edificioRepository.existsById(idEdificio)){
            throw new RuntimeException("No existe un edificio con ID: "+idEdificio);
        }

        return plantaRepository.findProjectedByEdificio_IdAndBorradoLogicoFalse(idEdificio);
    }

    //------------------

    private void asignarRelaciones(Planta planta, PlantaRequest request) {
        if (request.getIdEdificio() != null) {
            planta.setEdificio(edificioRepository.findById(request.getIdEdificio())
                    .orElseThrow(() -> new RuntimeException("No existe un edificio con ID: " + request.getIdEdificio())));
        }
    }
}