package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EdificioRequest;
import com.arrupeteca.dto.response.detail.EdificioDetalleResponse;
import com.arrupeteca.dto.response.summary.EdificioResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EdificioMapper;
import com.arrupeteca.persistence.entity.Edificio;
import com.arrupeteca.persistence.repository.EdificioRepository;
import com.arrupeteca.service.EdificioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EdificioServiceImpl implements EdificioService {

    private final EdificioRepository edificioRepository;
    private final EdificioMapper edificioMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<EdificioResumen> obtenerTodosActivos(Pageable pageable) {
        return edificioRepository.findByBorradoLogicoFalse(pageable)
                .map(edificioMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public EdificioDetalleResponse obtenerPorIdActivo(Long id) {
        return edificioRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .map(edificioMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un edificio activo con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EdificioResumen> obtenerTodos(Pageable pageable) {
        return edificioRepository.findAllProjectedBy(pageable)
                .map(edificioMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public EdificioDetalleResponse obtenerPorId(Long id) {
        return edificioRepository.findProjectedById(id)
                .map(edificioMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un edificio con ID: " + id));
    }

    @Override
    @Transactional
    public EdificioDetalleResponse crear(EdificioRequest request) {
        if (edificioRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe un edificio con el nombre: " + request.getNombre());
        }
        Edificio edificio = edificioMapper.toEntity(request);
        return edificioMapper.toDetalleResponse(edificioRepository.save(edificio));
    }

    @Override
    @Transactional
    public EdificioDetalleResponse actualizar(Long id, EdificioRequest request) {
        Edificio edificioExistente = edificioRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un edificio con ID: " + id));

        if (!edificioExistente.getNombre().equalsIgnoreCase(request.getNombre())
                && edificioRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe otro edificio con el nombre: " + request.getNombre());
        }

        edificioMapper.updateEntity(request, edificioExistente);
        return edificioMapper.toDetalleResponse(edificioRepository.save(edificioExistente));
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!edificioRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró un edificio con ID: " + id);
        }
        if (estado) {
            edificioRepository.activar(id);
        } else {
            edificioRepository.desactivar(id);
        }
    }
}
