package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.FormatoRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.FormatoMapper;
import com.arrupeteca.persistence.entity.Formato;
import com.arrupeteca.persistence.projection.FormatoResumen;
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
        return formatoRepository.findByBorradoLogicoFalse(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public FormatoResumen obtenerPorIdActivo(Long id) {
        return formatoRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un Formato con ID: " + id + ", o no está activo"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FormatoResumen> obtenerTodos(Pageable pageable) {
        return formatoRepository.findAllProjectedBy(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public FormatoResumen obtenerPorId(Long id) {
        return formatoRepository.findProjectedById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un Formato con ID: " + id));
    }

    @Override
    @Transactional
    public FormatoResumen crear(FormatoRequest request) {
        if (formatoRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("El nombre: " + request.getNombre() + " ya ha sido utilizado");
        }
        Formato formatoMapeado = formatoMapper.toEntity(request);
        Formato formatoGuardado = formatoRepository.save(formatoMapeado);
        return obtenerPorId(formatoGuardado.getId());
    }

    @Override
    @Transactional
    public FormatoResumen actualizar(Long id, FormatoRequest request) {
        Formato formatoEncontrado = formatoRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Formato con ID: " + id));

        if (!formatoEncontrado.getNombre().equalsIgnoreCase(request.getNombre())
                && formatoRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe un Formato con el nombre: " + request.getNombre());
        }

        formatoMapper.updateEntity(request, formatoEncontrado);
        formatoRepository.save(formatoEncontrado);

        return obtenerPorId(formatoEncontrado.getId());
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!formatoRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró Formato con ID: " + id);
        }
        if (estado) {
            formatoRepository.activar(id);
        } else {
            formatoRepository.desactivar(id);
        }
    }
}
