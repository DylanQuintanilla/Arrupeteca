package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.CategoriaMapper;
import com.arrupeteca.persistence.entity.Categoria;
import com.arrupeteca.persistence.projection.CategoriaResumen;
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
        return categoriaRepository.findByBorradoLogicoFalse(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResumen obtenerPorIdActivo(Long id) {
        return categoriaRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró una categoría con ID: " + id + ", o no está activa"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoriaResumen> obtenerTodos(Pageable pageable) {
        return categoriaRepository.findAllProjectedBy(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResumen obtenerPorId(Long id) {
        return categoriaRepository.findProjectedById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró una categoría con ID: " + id));
    }

    @Override
    @Transactional
    public CategoriaResumen crear(CategoriaRequest request) {
        if (categoriaRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("El nombre: " + request.getNombre() + " ya ha sido utilizado");
        }
        Categoria categoriaMapeada = categoriaMapper.toEntity(request);
        Categoria categoriaGuardada = categoriaRepository.save(categoriaMapeada);
        return obtenerPorId(categoriaGuardada.getId());
    }

    @Override
    @Transactional
    public CategoriaResumen actualizar(Long id, CategoriaRequest request) {
        Categoria categoriaEncontrada = categoriaRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró categoría con ID: " + id));

        if (!categoriaEncontrada.getNombre().equalsIgnoreCase(request.getNombre())
                && categoriaRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe una categoría con el nombre: " + request.getNombre());
        }

        categoriaMapper.updateEntity(request, categoriaEncontrada);
        categoriaRepository.save(categoriaEncontrada);

        return obtenerPorId(categoriaEncontrada.getId());
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!categoriaRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró categoría con ID: " + id);
        }
        if (estado)
            categoriaRepository.activar(id);
        else
            categoriaRepository.desactivar(id);
    }
}
