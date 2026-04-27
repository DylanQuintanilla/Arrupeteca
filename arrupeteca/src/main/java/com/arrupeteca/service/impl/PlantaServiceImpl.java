package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.PlantaRequest;
import com.arrupeteca.dto.response.detail.PlantaDetalleResponse;
import com.arrupeteca.dto.response.summary.PlantaResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.PlantaMapper;
import com.arrupeteca.persistence.entity.Planta;
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

    private final PlantaRepository plantaRepository;
    private final PlantaMapper plantaMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<PlantaResumen> obtenerTodosActivos(Pageable pageable) {
        return plantaRepository.findByBorradoLogicoFalse(pageable)
                .map(plantaMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public PlantaDetalleResponse obtenerPorIdActivo(Long id) {
        return plantaRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .map(plantaMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No existe una planta activa con el ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlantaResumen> obtenerTodos(Pageable pageable) {
        return plantaRepository.findAllProjectedBy(pageable)
                .map(plantaMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public PlantaDetalleResponse obtenerPorId(Long id) {
        return plantaRepository.findProjectedById(id)
                .map(plantaMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No existe una planta con el ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlantaResumen> buscarPorEdificioId(Long idEdificio, Pageable pageable) {
        return plantaRepository.findByEdificioIdAndBorradoLogicoFalse(idEdificio, pageable)
                .map(plantaMapper::toResumen);
    }

    @Override
    @Transactional
    public PlantaDetalleResponse crear(PlantaRequest request) {
        if (plantaRepository.existsByNombreAndEdificioId(request.getNombre(), request.getIdEdificio())) {
            throw new ValidacionNegocioException("Ya existe una planta con ese nombre en el edificio seleccionado");
        }
        Planta planta = plantaMapper.toEntity(request);
        return plantaMapper.toDetalleResponse(plantaRepository.save(planta));
    }

    @Override
    @Transactional
    public PlantaDetalleResponse actualizar(Long id, PlantaRequest request) {
        Planta plantaExistente = plantaRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No existe una planta con el ID: " + id));

        if (!plantaExistente.getNombre().equalsIgnoreCase(request.getNombre())
                && plantaRepository.existsByNombreAndEdificioId(request.getNombre(), request.getIdEdificio())) {
            throw new ValidacionNegocioException("Ya existe otra planta con ese nombre en el edificio seleccionado");
        }

        plantaMapper.updateEntity(request, plantaExistente);
        return plantaMapper.toDetalleResponse(plantaRepository.save(plantaExistente));
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!plantaRepository.existsById(id)) {
            throw new ValidacionNegocioException("No existe una planta con el ID: " + id);
        }
        if (estado) {
            plantaRepository.activar(id);
        } else {
            plantaRepository.desactivar(id);
        }
    }
}
