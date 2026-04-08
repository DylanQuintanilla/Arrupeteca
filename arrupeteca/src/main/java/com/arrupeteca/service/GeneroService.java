package com.arrupeteca.service;

import com.arrupeteca.dto.request.GeneroRequest;
import com.arrupeteca.persistence.projection.GeneroResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GeneroService {

    Page<GeneroResumen> obtenerTodosActivos(Pageable pageable);

    GeneroResumen obtenerPorIdActivo(Long id);

    Page<GeneroResumen> obtenerTodos(Pageable pageable);

    GeneroResumen obtenerPorId(Long id);

    GeneroResumen crear(GeneroRequest request);

    GeneroResumen actualizar(Long id, GeneroRequest request);

    void cambiarEstado(Long id, boolean estado);
}
