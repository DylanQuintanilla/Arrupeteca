package com.arrupeteca.service;

import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.persistence.projection.CategoriaResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoriaService {

    Page<CategoriaResumen> obtenerTodosActivos(Pageable pageable);

    CategoriaResumen obtenerPorIdActivo(Long id);

    Page<CategoriaResumen> obtenerTodos(Pageable pageable);

    CategoriaResumen obtenerPorId(Long id);

    CategoriaResumen crear(CategoriaRequest request);

    CategoriaResumen actualizar(Long id, CategoriaRequest request);

    void cambiarEstado(Long id, boolean estado);
}
