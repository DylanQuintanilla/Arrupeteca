package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.TipoAutoriaRequest;
import com.arrupeteca.dto.response.TipoAutoriaResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.TipoAutoriaMapper;
import com.arrupeteca.persistence.entity.TipoAutoria;
import com.arrupeteca.persistence.repository.TipoAutoriaRepository;
import com.arrupeteca.service.TipoAutoriaService;
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
public class TipoAutoriaServiceImpl implements TipoAutoriaService {

    private final TipoAutoriaRepository tipoAutoriaRepository;
    private final TipoAutoriaMapper tipoAutoriaMapper;

    @Override
    @Cacheable(value = "tipos-autoria-activos")
    public Page<TipoAutoriaResponse> obtenerTodosActivos(Pageable pageable) {
        return tipoAutoriaRepository.findByBorradoLogicoFalse(pageable)
                .map(tipoAutoriaMapper::toResponse);
    }

    @Override
    public TipoAutoriaResponse obtenerPorIdActivo(Long id) {
        return tipoAutoriaRepository.findByIdAndBorradoLogicoFalse(id)
                .map(tipoAutoriaMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un tipo de autoría activo con ID: " + id));
    }

    @Override
    public Page<TipoAutoriaResponse> obtenerTodos(Pageable pageable) {
        return tipoAutoriaRepository.findAll(pageable)
                .map(tipoAutoriaMapper::toResponse);
    }

    @Override
    public TipoAutoriaResponse obtenerPorId(Long id) {
        return tipoAutoriaRepository.findById(id)
                .map(tipoAutoriaMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un tipo de autoría con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "tipos-autoria-activos", allEntries = true)
    public TipoAutoriaResponse crear(TipoAutoriaRequest request) {
        if (tipoAutoriaRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ValidacionNegocioException("Ya existe un tipo de autoría con el nombre: " + request.nombre());
        }
        TipoAutoria tipoAutoria = tipoAutoriaMapper.toEntity(request);
        return tipoAutoriaMapper.toResponse(tipoAutoriaRepository.save(tipoAutoria));
    }

    @Override
    @Transactional
    @CacheEvict(value = "tipos-autoria-activos", allEntries = true)
    public TipoAutoriaResponse actualizar(Long id, TipoAutoriaRequest request) {
        TipoAutoria tipoAutoriaExistente = tipoAutoriaRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un tipo de autoría con ID: " + id));

        if (!tipoAutoriaExistente.getNombre().equalsIgnoreCase(request.nombre())
                && tipoAutoriaRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ValidacionNegocioException("Ya existe otro tipo de autoría con el nombre: " + request.nombre());
        }

        tipoAutoriaMapper.updateEntity(request, tipoAutoriaExistente);
        return tipoAutoriaMapper.toResponse(tipoAutoriaRepository.save(tipoAutoriaExistente));
    }

    @Override
    @Transactional
    @CacheEvict(value = "tipos-autoria-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        if (tipoAutoriaRepository.countByIdIncludeDeleted(id) == 0) {
            throw new ValidacionNegocioException("No se encontró un tipo de autoría con ID: " + id);
        }
        if (estado) {
            tipoAutoriaRepository.activar(id);
        } else {
            tipoAutoriaRepository.desactivar(id);
        }
    }
}
