package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.PlantaRequest;
import com.arrupeteca.dto.response.PlantaResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.PlantaMapper;
import com.arrupeteca.persistence.entity.Planta;
import com.arrupeteca.persistence.repository.PlantaRepository;
import com.arrupeteca.service.PlantaService;
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
public class PlantaServiceImpl implements PlantaService {

    private final PlantaRepository plantaRepository;
    private final PlantaMapper plantaMapper;

    @Override
    @Cacheable(value = "plantas-activas")
    public Page<PlantaResponse> obtenerTodosActivos(Pageable pageable) {
        return plantaRepository.findByBorradoLogicoFalse(pageable)
                .map(plantaMapper::toResponse);
    }

    @Override
    public PlantaResponse obtenerPorIdActivo(Long id) {
        return plantaRepository.findByIdAndBorradoLogicoFalse(id)
                .map(plantaMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No existe una planta activa con el ID: " + id));
    }

    @Override
    public Page<PlantaResponse> obtenerTodos(Pageable pageable) {
        return plantaRepository.findAll(pageable)
                .map(plantaMapper::toResponse);
    }

    @Override
    public PlantaResponse obtenerPorId(Long id) {
        return plantaRepository.findById(id)
                .map(plantaMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No existe una planta con el ID: " + id));
    }

    @Override
    public Page<PlantaResponse> buscarPorEdificioId(Long idEdificio, Pageable pageable) {
        return plantaRepository.findByEdificioIdAndBorradoLogicoFalse(idEdificio, pageable)
                .map(plantaMapper::toResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = "plantas-activas", allEntries = true)
    public PlantaResponse crear(PlantaRequest request) {
        if (plantaRepository.existsByNombreIgnoreCaseAndEdificioId(request.nombre(), request.idEdificio())) {
            throw new ValidacionNegocioException("Ya existe una planta con ese nombre en el edificio seleccionado");
        }
        Planta planta = plantaMapper.toEntity(request);
        return plantaMapper.toResponse(plantaRepository.save(planta));
    }

    @Override
    @Transactional
    @CacheEvict(value = "plantas-activas", allEntries = true)
    public PlantaResponse actualizar(Long id, PlantaRequest request) {
        Planta plantaExistente = plantaRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No existe una planta con el ID: " + id));

        if (!plantaExistente.getNombre().equalsIgnoreCase(request.nombre())
                && plantaRepository.existsByNombreIgnoreCaseAndEdificioId(request.nombre(), request.idEdificio())) {
            throw new ValidacionNegocioException("Ya existe otra planta con ese nombre en el edificio seleccionado");
        }

        plantaMapper.updateEntity(request, plantaExistente);
        return plantaMapper.toResponse(plantaRepository.save(plantaExistente));
    }

    @Override
    @Transactional
    @CacheEvict(value = "plantas-activas", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        if (plantaRepository.countByIdIncludeDeleted(id) == 0) {
            throw new ValidacionNegocioException("No existe una planta con el ID: " + id);
        }
        if (estado) {
            plantaRepository.activar(id);
        } else {
            plantaRepository.desactivar(id);
        }
    }
}
