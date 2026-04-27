package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.ObraRequest;
import com.arrupeteca.dto.response.detail.ObraDetalleResponse;
import com.arrupeteca.dto.response.summary.ObraResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.ObraMapper;
import com.arrupeteca.persistence.entity.Obra;
import com.arrupeteca.persistence.repository.ObraRepository;
import com.arrupeteca.service.ObraService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ObraServiceImpl implements ObraService {

    private final ObraRepository obraRepository;
    private final ObraMapper obraMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ObraResumen> obtenerTodosActivos(Pageable pageable) {
        return obraRepository.findByBorradoLogicoFalse(pageable)
                .map(obraMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public ObraDetalleResponse obtenerPorIdActivo(Long id) {
        return obraRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .map(obraMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Obra activa con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ObraResumen> busquedaGlobal(String palabra, Pageable pageable) {
        return obraRepository.busquedaGlobal(palabra, pageable)
                .map(obraMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ObraResumen> obtenerTodos(Pageable pageable) {
        return obraRepository.findAll(pageable)
                .map(obraMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public ObraDetalleResponse obtenerPorId(Long id) {
        return obraRepository.findById(id)
                .map(obraMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Obra con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ObraResumen> busquedaMaestraAdmin(
            Boolean esBorrado, String termino, Integer anioInicio, Integer anioFin,
            Pageable pageable) {
        return obraRepository.busquedaMaestraAdmin(esBorrado, termino, anioInicio, anioFin, pageable)
                .map(obraMapper::toResumen);
    }

    @Override
    @Transactional
    public ObraDetalleResponse crear(ObraRequest request) {
        if (obraRepository.existsByTituloIgnoreCase(request.getTitulo())) {
            throw new ValidacionNegocioException("Ya existe una obra con el título: " + request.getTitulo());
        }
        Obra obra = obraMapper.toEntity(request);
        return obraMapper.toDetalleResponse(obraRepository.save(obra));
    }

    @Override
    @Transactional
    public ObraDetalleResponse actualizar(Long id, ObraRequest request) {
        Obra obraExistente = obraRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Obra con ID: " + id));

        if (!obraExistente.getTitulo().equalsIgnoreCase(request.getTitulo())
                && obraRepository.existsByTituloIgnoreCase(request.getTitulo())) {
            throw new ValidacionNegocioException("Ya existe otra obra con el título: " + request.getTitulo());
        }

        obraMapper.updateEntity(request, obraExistente);
        return obraMapper.toDetalleResponse(obraRepository.save(obraExistente));
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!obraRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró Obra con ID: " + id);
        }
        if (estado) {
            obraRepository.activar(id);
        } else {
            obraRepository.desactivar(id);
        }
    }
}
