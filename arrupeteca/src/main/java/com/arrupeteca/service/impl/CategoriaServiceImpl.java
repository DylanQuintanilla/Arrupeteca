package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.dto.response.detail.CategoriaDetalleResponse;
import com.arrupeteca.dto.response.summary.CategoriaResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.CategoriaMapper;
import com.arrupeteca.persistence.entity.Categoria;
import com.arrupeteca.persistence.repository.CategoriaRepository;
import com.arrupeteca.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<CategoriaResumen> obtenerTodosActivos(Pageable pageable) {
        return categoriaRepository.findByBorradoLogicoFalse(pageable)
                .map(categoriaMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaDetalleResponse obtenerPorIdActivo(Long id) {
        return categoriaRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .map(categoriaMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró una categoría activa con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoriaResumen> obtenerTodos(Pageable pageable) {
        return categoriaRepository.findAllProjectedBy(pageable)
                .map(categoriaMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaDetalleResponse obtenerPorId(Long id) {
        return categoriaRepository.findProjectedById(id)
                .map(categoriaMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró una categoría con ID: " + id));
    }

    @Override
    @Transactional
    public CategoriaDetalleResponse crear(CategoriaRequest request) {
        if (categoriaRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe una categoría con el nombre: " + request.getNombre());
        }
        Categoria categoria = categoriaMapper.toEntity(request);
        return categoriaMapper.toDetalleResponse(categoriaRepository.save(categoria));
    }

    @Override
    @Transactional
    public CategoriaDetalleResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró una categoría con ID: " + id));

        if (!categoriaExistente.getNombre().equalsIgnoreCase(request.getNombre())
                && categoriaRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe otra categoría con el nombre: " + request.getNombre());
        }

        categoriaMapper.updateEntity(request, categoriaExistente);
        return categoriaMapper.toDetalleResponse(categoriaRepository.save(categoriaExistente));
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!categoriaRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró una categoría con ID: " + id);
        }
        if (estado) {
            categoriaRepository.activar(id);
        } else {
            categoriaRepository.desactivar(id);
        }
    }
}
