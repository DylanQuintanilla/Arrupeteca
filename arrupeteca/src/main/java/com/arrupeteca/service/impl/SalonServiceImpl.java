package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.SalonRequest;
import com.arrupeteca.dto.response.detail.SalonDetalleResponse;
import com.arrupeteca.dto.response.summary.SalonResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.SalonMapper;
import com.arrupeteca.persistence.entity.Salon;
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

    private final SalonRepository salonRepository;
    private final SalonMapper salonMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<SalonResumen> obtenerTodosActivos(Pageable pageable) {
        return salonRepository.findByBorradoLogicoFalse(pageable)
                .map(salonMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public SalonDetalleResponse obtenerPorIdActivo(Long id) {
        return salonRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .map(salonMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No existe un salón activo con el ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SalonResumen> obtenerTodos(Pageable pageable) {
        return salonRepository.findAllProjectedBy(pageable)
                .map(salonMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public SalonDetalleResponse obtenerPorId(Long id) {
        return salonRepository.findProjectedById(id)
                .map(salonMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No existe un salón con el ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SalonResumen> buscarPorPlantaId(Long idPlanta, Pageable pageable) {
        return salonRepository.findByPlantaIdAndBorradoLogicoFalse(idPlanta, pageable)
                .map(salonMapper::toResumen);
    }

    @Override
    @Transactional
    public SalonDetalleResponse crear(SalonRequest request) {
        if (salonRepository.existsByNombreAndPlantaId(request.getNombre(), request.getIdPlanta())) {
            throw new ValidacionNegocioException("Ya existe un salón con ese nombre en la planta seleccionada");
        }
        Salon salon = salonMapper.toEntity(request);
        return salonMapper.toDetalleResponse(salonRepository.save(salon));
    }

    @Override
    @Transactional
    public SalonDetalleResponse actualizar(Long id, SalonRequest request) {
        Salon salonExistente = salonRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No existe un salón con el ID: " + id));

        if (!salonExistente.getNombre().equalsIgnoreCase(request.getNombre())
                && salonRepository.existsByNombreAndPlantaId(request.getNombre(), request.getIdPlanta())) {
            throw new ValidacionNegocioException("Ya existe otro salón con ese nombre en la planta seleccionada");
        }

        salonMapper.updateEntity(request, salonExistente);
        return salonMapper.toDetalleResponse(salonRepository.save(salonExistente));
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!salonRepository.existsById(id)) {
            throw new ValidacionNegocioException("No existe un salón con el ID: " + id);
        }
        if (estado) {
            salonRepository.activar(id);
        } else {
            salonRepository.desactivar(id);
        }
    }
}
