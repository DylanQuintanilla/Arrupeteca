package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.TipoAutoriaRequest;
import com.arrupeteca.dto.response.summary.TipoAutoriaResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.TipoAutoriaMapper;
import com.arrupeteca.persistence.entity.TipoAutoria;
import com.arrupeteca.persistence.repository.TipoAutoriaRepository;
import com.arrupeteca.service.TipoAutoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TipoAutoriaServiceImpl implements TipoAutoriaService {

    private final TipoAutoriaRepository tipoAutoriaRepository;
    private final TipoAutoriaMapper tipoAutoriaMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<TipoAutoriaResumen> obtenerTodosActivos(Pageable pageable) {
        return tipoAutoriaRepository.findByBorradoLogicoFalse(pageable)
                .map(tipoAutoriaMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public TipoAutoriaResumen obtenerPorIdActivo(Long id) {
        return tipoAutoriaRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .map(tipoAutoriaMapper::toResumen)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un tipo de autoría activo con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TipoAutoriaResumen> obtenerTodos(Pageable pageable) {
        return tipoAutoriaRepository.findAllProjectedBy(pageable)
                .map(tipoAutoriaMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public TipoAutoriaResumen obtenerPorId(Long id) {
        return tipoAutoriaRepository.findProjectedById(id)
                .map(tipoAutoriaMapper::toResumen)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un tipo de autoría con ID: " + id));
    }

    @Override
    @Transactional
    public TipoAutoriaResumen crear(TipoAutoriaRequest request) {
        if (tipoAutoriaRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe un tipo de autoría con el nombre: " + request.getNombre());
        }
        TipoAutoria tipoAutoria = tipoAutoriaMapper.toEntity(request);
        return tipoAutoriaMapper.toResumen(tipoAutoriaRepository.save(tipoAutoria));
    }

    @Override
    @Transactional
    public TipoAutoriaResumen actualizar(Long id, TipoAutoriaRequest request) {
        TipoAutoria tipoAutoriaExistente = tipoAutoriaRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un tipo de autoría con ID: " + id));

        if (!tipoAutoriaExistente.getNombre().equalsIgnoreCase(request.getNombre())
                && tipoAutoriaRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe otro tipo de autoría con el nombre: " + request.getNombre());
        }

        tipoAutoriaMapper.updateEntity(request, tipoAutoriaExistente);
        return tipoAutoriaMapper.toResumen(tipoAutoriaRepository.save(tipoAutoriaExistente));
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!tipoAutoriaRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró un tipo de autoría con ID: " + id);
        }
        if (estado) {
            tipoAutoriaRepository.activar(id);
        } else {
            tipoAutoriaRepository.desactivar(id);
        }
    }
}
