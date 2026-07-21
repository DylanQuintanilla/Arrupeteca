package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.FormatoRequest;
import com.arrupeteca.dto.response.FormatoResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.FormatoMapper;
import com.arrupeteca.persistence.entity.Formato;
import com.arrupeteca.persistence.repository.FormatoRepository;
import com.arrupeteca.service.FormatoService;
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
public class FormatoServiceImpl implements FormatoService {

    private final FormatoRepository formatoRepository;
    private final FormatoMapper formatoMapper;

    @Override
    @Cacheable(value = "formatos-activos")
    public Page<FormatoResponse> obtenerTodosActivos(Pageable pageable) {
        return formatoRepository.findByBorradoLogicoFalse(pageable)
                .map(formatoMapper::toResponse);
    }

    @Override
    public FormatoResponse obtenerPorIdActivo(Long id) {
        return formatoRepository.findByIdAndBorradoLogicoFalse(id)
                .map(formatoMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un formato activo con ID: " + id));
    }

    @Override
    public Page<FormatoResponse> obtenerTodos(Pageable pageable) {
        return formatoRepository.findAll(pageable)
                .map(formatoMapper::toResponse);
    }

    @Override
    public FormatoResponse obtenerPorId(Long id) {
        return formatoRepository.findById(id)
                .map(formatoMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un formato con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "formatos-activos", allEntries = true)
    public FormatoResponse crear(FormatoRequest request) {
        if (formatoRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ValidacionNegocioException("Ya existe un formato con el nombre: " + request.nombre());
        }
        Formato formato = formatoMapper.toEntity(request);
        return formatoMapper.toResponse(formatoRepository.save(formato));
    }

    @Override
    @Transactional
    @CacheEvict(value = "formatos-activos", allEntries = true)
    public FormatoResponse actualizar(Long id, FormatoRequest request) {
        Formato formatoExistente = formatoRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un formato con ID: " + id));

        if (!formatoExistente.getNombre().equalsIgnoreCase(request.nombre())
                && formatoRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ValidacionNegocioException("Ya existe otro formato con el nombre: " + request.nombre());
        }

        formatoMapper.updateEntity(request, formatoExistente);
        return formatoMapper.toResponse(formatoRepository.save(formatoExistente));
    }

    @Override
    @Transactional
    @CacheEvict(value = "formatos-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        if (formatoRepository.countByIdIncludeDeleted(id) == 0) {
            throw new ValidacionNegocioException("No se encontró un formato con ID: " + id);
        }
        if (estado) {
            formatoRepository.activar(id);
        } else {
            formatoRepository.desactivar(id);
        }
    }
}
