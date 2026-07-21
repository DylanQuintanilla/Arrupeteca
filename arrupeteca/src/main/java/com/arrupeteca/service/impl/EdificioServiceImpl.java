package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EdificioRequest;
import com.arrupeteca.dto.response.EdificioResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EdificioMapper;
import com.arrupeteca.persistence.entity.Edificio;
import com.arrupeteca.persistence.repository.EdificioRepository;
import com.arrupeteca.service.EdificioService;
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
public class EdificioServiceImpl implements EdificioService {

    private final EdificioRepository edificioRepository;
    private final EdificioMapper edificioMapper;

    @Override
    @Cacheable(value = "edificios-activos")
    public Page<EdificioResponse> obtenerTodosActivos(Pageable pageable) {
        return edificioRepository.findByBorradoLogicoFalse(pageable)
                .map(edificioMapper::toResponse);
    }

    @Override
    public EdificioResponse obtenerPorIdActivo(Long id) {
        return edificioRepository.findByIdAndBorradoLogicoFalse(id)
                .map(edificioMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un edificio activo con ID: " + id));
    }

    @Override
    public Page<EdificioResponse> obtenerTodos(Pageable pageable) {
        return edificioRepository.findAll(pageable)
                .map(edificioMapper::toResponse);
    }

    @Override
    public EdificioResponse obtenerPorId(Long id) {
        return edificioRepository.findById(id)
                .map(edificioMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un edificio con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "edificios-activos", allEntries = true)
    public EdificioResponse crear(EdificioRequest request) {
        if (edificioRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ValidacionNegocioException("Ya existe un edificio con el nombre: " + request.nombre());
        }
        Edificio edificio = edificioMapper.toEntity(request);
        return edificioMapper.toResponse(edificioRepository.save(edificio));
    }

    @Override
    @Transactional
    @CacheEvict(value = "edificios-activos", allEntries = true)
    public EdificioResponse actualizar(Long id, EdificioRequest request) {
        Edificio edificioExistente = edificioRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un edificio con ID: " + id));

        if (!edificioExistente.getNombre().equalsIgnoreCase(request.nombre())
                && edificioRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ValidacionNegocioException("Ya existe otro edificio con el nombre: " + request.nombre());
        }

        edificioMapper.updateEntity(request, edificioExistente);
        return edificioMapper.toResponse(edificioRepository.save(edificioExistente));
    }

    @Override
    @Transactional
    @CacheEvict(value = "edificios-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        if (edificioRepository.countByIdIncludeDeleted(id) == 0) {
            throw new ValidacionNegocioException("No se encontró un edificio con ID: " + id);
        }
        if (estado) {
            edificioRepository.activar(id);
        } else {
            edificioRepository.desactivar(id);
        }
    }
}
