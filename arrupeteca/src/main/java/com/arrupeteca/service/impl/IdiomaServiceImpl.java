package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.IdiomaRequest;
import com.arrupeteca.dto.response.IdiomaResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.IdiomaMapper;
import com.arrupeteca.persistence.entity.Idioma;
import com.arrupeteca.persistence.repository.IdiomaRepository;
import com.arrupeteca.service.IdiomaService;
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
public class IdiomaServiceImpl implements IdiomaService {

    private final IdiomaRepository idiomaRepository;
    private final IdiomaMapper idiomaMapper;

    @Override
    @Cacheable(value = "idiomas-activos")
    public Page<IdiomaResponse> obtenerTodosActivos(Pageable pageable) {
        return idiomaRepository.findByBorradoLogicoFalse(pageable)
                .map(idiomaMapper::toResponse);
    }

    @Override
    public IdiomaResponse obtenerPorIdActivo(Long id) {
        return idiomaRepository.findByIdAndBorradoLogicoFalse(id)
                .map(idiomaMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un idioma activo con ID: " + id));
    }

    @Override
    public Page<IdiomaResponse> obtenerTodos(Pageable pageable) {
        return idiomaRepository.findAll(pageable)
                .map(idiomaMapper::toResponse);
    }

    @Override
    public IdiomaResponse obtenerPorId(Long id) {
        return idiomaRepository.findById(id)
                .map(idiomaMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un idioma con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "idiomas-activos", allEntries = true)
    public IdiomaResponse crear(IdiomaRequest request) {
        if (idiomaRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ValidacionNegocioException("Ya existe un idioma con el nombre: " + request.nombre());
        }
        Idioma idioma = idiomaMapper.toEntity(request);
        return idiomaMapper.toResponse(idiomaRepository.save(idioma));
    }

    @Override
    @Transactional
    @CacheEvict(value = "idiomas-activos", allEntries = true)
    public IdiomaResponse actualizar(Long id, IdiomaRequest request) {
        Idioma idiomaExistente = idiomaRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un idioma con ID: " + id));

        if (!idiomaExistente.getNombre().equalsIgnoreCase(request.nombre())
                && idiomaRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ValidacionNegocioException("Ya existe otro idioma con el nombre: " + request.nombre());
        }

        idiomaMapper.updateEntity(request, idiomaExistente);
        return idiomaMapper.toResponse(idiomaRepository.save(idiomaExistente));
    }

    @Override
    @Transactional
    @CacheEvict(value = "idiomas-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        if (idiomaRepository.countByIdIncludeDeleted(id) == 0) {
            throw new ValidacionNegocioException("No se encontró un idioma con ID: " + id);
        }
        if (estado) {
            idiomaRepository.activar(id);
        } else {
            idiomaRepository.desactivar(id);
        }
    }
}
