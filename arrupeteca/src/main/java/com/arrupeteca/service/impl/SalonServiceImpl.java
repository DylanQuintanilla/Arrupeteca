package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.SalonRequest;
import com.arrupeteca.dto.response.SalonResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.SalonMapper;
import com.arrupeteca.persistence.entity.Salon;
import com.arrupeteca.persistence.repository.SalonRepository;
import com.arrupeteca.service.SalonService;
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
public class SalonServiceImpl implements SalonService {

    private final SalonRepository salonRepository;
    private final SalonMapper salonMapper;

    @Override
    @Cacheable(value = "salones-activos")
    public Page<SalonResponse> obtenerTodosActivos(Pageable pageable) {
        return salonRepository.findByBorradoLogicoFalse(pageable)
                .map(salonMapper::toResponse);
    }

    @Override
    public SalonResponse obtenerPorIdActivo(Long id) {
        return salonRepository.findByIdAndBorradoLogicoFalse(id)
                .map(salonMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No existe un salón activo con el ID: " + id));
    }

    @Override
    public Page<SalonResponse> obtenerTodos(Pageable pageable) {
        return salonRepository.findAll(pageable)
                .map(salonMapper::toResponse);
    }

    @Override
    public SalonResponse obtenerPorId(Long id) {
        return salonRepository.findById(id)
                .map(salonMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No existe un salón con el ID: " + id));
    }

    @Override
    public Page<SalonResponse> buscarPorPlantaId(Long idPlanta, Pageable pageable) {
        return salonRepository.findByPlantaIdAndBorradoLogicoFalse(idPlanta, pageable)
                .map(salonMapper::toResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = "salones-activos", allEntries = true)
    public SalonResponse crear(SalonRequest request) {
        if (salonRepository.existsByNombreIgnoreCaseAndPlantaId(request.nombre(), request.idPlanta())) {
            throw new ValidacionNegocioException("Ya existe un salón con ese nombre en la planta seleccionada");
        }
        Salon salon = salonMapper.toEntity(request);
        return salonMapper.toResponse(salonRepository.save(salon));
    }

    @Override
    @Transactional
    @CacheEvict(value = "salones-activos", allEntries = true)
    public SalonResponse actualizar(Long id, SalonRequest request) {
        Salon salonExistente = salonRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No existe un salón con el ID: " + id));

        if (!salonExistente.getNombre().equalsIgnoreCase(request.nombre())
                && salonRepository.existsByNombreIgnoreCaseAndPlantaId(request.nombre(), request.idPlanta())) {
            throw new ValidacionNegocioException("Ya existe otro salón con ese nombre en la planta seleccionada");
        }

        salonMapper.updateEntity(request, salonExistente);
        return salonMapper.toResponse(salonRepository.save(salonExistente));
    }

    @Override
    @Transactional
    @CacheEvict(value = "salones-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        if (salonRepository.countByIdIncludeDeleted(id) == 0) {
            throw new ValidacionNegocioException("No existe un salón con el ID: " + id);
        }
        if (estado) {
            salonRepository.activar(id);
        } else {
            salonRepository.desactivar(id);
        }
    }
}
