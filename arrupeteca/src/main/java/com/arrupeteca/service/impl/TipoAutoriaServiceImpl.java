package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.TipoAutoriaRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.TipoAutoriaMapper;
import com.arrupeteca.persistence.entity.TipoAutoria;
import com.arrupeteca.persistence.projection.TipoAutoriaResumen;
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
        return tipoAutoriaRepository.findByBorradoLogicoFalse(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public TipoAutoriaResumen obtenerPorIdActivo(Long id) {
        return tipoAutoriaRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un tipo de autoría con ID: " + id + ", o no está activo"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TipoAutoriaResumen> obtenerTodos(Pageable pageable) {
        return tipoAutoriaRepository.findAllProjectedBy(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public TipoAutoriaResumen obtenerPorId(Long id) {
        return tipoAutoriaRepository.findProjectedById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un tipo de autoría con ID: " + id));
    }

    @Override
    @Transactional
    public TipoAutoriaResumen crear(TipoAutoriaRequest request) {
        if (tipoAutoriaRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("El nombre: " + request.getNombre() + " ya ha sido utilizado");
        }
        TipoAutoria tipoAutoriaMapeado = tipoAutoriaMapper.toEntity(request);
        TipoAutoria tipoAutoriaGuardado = tipoAutoriaRepository.save(tipoAutoriaMapeado);
        return obtenerPorId(tipoAutoriaGuardado.getId());
    }

    @Override
    @Transactional
    public TipoAutoriaResumen actualizar(Long id, TipoAutoriaRequest request) {
        TipoAutoria tipoAutoriaEncontrado = tipoAutoriaRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un tipo de autoría con ID: " + id));

        if (!tipoAutoriaEncontrado.getNombre().equalsIgnoreCase(request.getNombre())
                && tipoAutoriaRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe una autoría con el nombre: " + request.getNombre());
        }

        tipoAutoriaMapper.updateEntity(request, tipoAutoriaEncontrado);
        tipoAutoriaRepository.save(tipoAutoriaEncontrado);

        return obtenerPorId(tipoAutoriaEncontrado.getId());
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!tipoAutoriaRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró un tipo de autoría con ID: " + id);
        }
        if (estado)
            tipoAutoriaRepository.activar(id);
        else
            tipoAutoriaRepository.desactivar(id);
    }
}
