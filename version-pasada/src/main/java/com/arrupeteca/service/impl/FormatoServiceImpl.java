package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.FormatoRequest;
import com.arrupeteca.dto.response.detail.FormatoDetalleResponse;
import com.arrupeteca.dto.response.summary.FormatoResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.FormatoMapper;
import com.arrupeteca.persistence.entity.Formato;
import com.arrupeteca.persistence.repository.FormatoRepository;
import com.arrupeteca.service.FormatoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FormatoServiceImpl implements FormatoService {

    private final FormatoRepository formatoRepository;
    private final FormatoMapper formatoMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<FormatoResumen> obtenerTodosActivos(Pageable pageable) {
        return formatoRepository.findByBorradoLogicoFalse(pageable)
                .map(formatoMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public FormatoDetalleResponse obtenerPorIdActivo(Long id) {
        return formatoRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .map(formatoMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un Formato activo con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FormatoResumen> obtenerTodos(Pageable pageable) {
        return formatoRepository.findAllProjectedBy(pageable)
                .map(formatoMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public FormatoDetalleResponse obtenerPorId(Long id) {
        return formatoRepository.findProjectedById(id)
                .map(formatoMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un Formato con ID: " + id));
    }

    @Override
    @Transactional
    public FormatoDetalleResponse crear(FormatoRequest request) {
        if (formatoRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe un Formato con el nombre: " + request.getNombre());
        }
        Formato formato = formatoMapper.toEntity(request);
        return formatoMapper.toDetalleResponse(formatoRepository.save(formato));
    }

    @Override
    @Transactional
    public FormatoDetalleResponse actualizar(Long id, FormatoRequest request) {
        Formato formatoExistente = formatoRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un Formato con ID: " + id));

        if (!formatoExistente.getNombre().equalsIgnoreCase(request.getNombre())
                && formatoRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe otro Formato con el nombre: " + request.getNombre());
        }

        formatoMapper.updateEntity(request, formatoExistente);
        return formatoMapper.toDetalleResponse(formatoRepository.save(formatoExistente));
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!formatoRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró un Formato con ID: " + id);
        }
        if (estado) {
            formatoRepository.activar(id);
        } else {
            formatoRepository.desactivar(id);
        }
    }
}
