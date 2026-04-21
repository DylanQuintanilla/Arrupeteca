package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.mapper.CategoriaMapper;
import com.arrupeteca.mapper.GeneroMapper;
import com.arrupeteca.persistence.entity.Categoria;
import com.arrupeteca.persistence.projection.CategoriaResumen;
import com.arrupeteca.persistence.repository.CategoriaRepository;
import com.arrupeteca.persistence.repository.GeneroRepository;
import com.arrupeteca.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    private final CategoriaMapper categoriaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResumen> obtenerTodosActivos() {

        return categoriaRepository.findByBorradoLogicoFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResumen obtenerPorIdActivo(Long id) {
        return categoriaRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new RuntimeException("No se encontro una categoria con ID: "+id+", o no esta activo"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResumen> obtenerTodos() {
        return categoriaRepository.findAllProjectedBy();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResumen obtenerPorId(Long id) {
        return categoriaRepository.findProjectedById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro una categoria con ID: "+id));

    }

    @Override
    @Transactional
    public CategoriaResumen crear(CategoriaRequest request) {
        if(categoriaRepository.existsByNombreIgnoreCase(request.getNombre())){
            throw new RuntimeException("El nombre: "+request.getNombre()+" ya ha sido utilizado");
        }
        Categoria categoriaMapeada = categoriaMapper.toEntity(request);
        Categoria categoriaGuarda = categoriaRepository.save(categoriaMapeada);
        return obtenerPorId(categoriaGuarda.getId());
    }

    @Override
    @Transactional
    public CategoriaResumen actualizar(Long id, CategoriaRequest request) {

        Categoria categoriaEncontrada = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro categoria con ID: "+id));

        if (!categoriaEncontrada.getNombre().equalsIgnoreCase(request.getNombre())
                && categoriaRepository.existsByNombreIgnoreCase(request.getNombre())){
            throw new RuntimeException("Ya existe un genero con el nombre: "+request.getNombre());
        }

        categoriaMapper.updateEntity(request, categoriaEncontrada);
        Categoria categoriaActualizada = categoriaRepository.save(categoriaEncontrada);

        return obtenerPorId(categoriaEncontrada.getId());
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!categoriaRepository.existsById(id)){
            throw new RuntimeException("No se encontro categoria con ID: "+id);
        }
        if (estado)
            categoriaRepository.activar(id);
        else
            categoriaRepository.desactivar(id);
    }
}
