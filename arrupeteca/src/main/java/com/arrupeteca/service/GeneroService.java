package com.arrupeteca.service;

import com.arrupeteca.dto.request.GeneroRequest;
import com.arrupeteca.persistence.projection.GeneroResumen;

import java.util.List;

public interface GeneroService {

    List<GeneroResumen> obtenerTodosActivos();

    GeneroResumen obtenerPorIdActivo(Long id);

    List<GeneroResumen> obtenerTodos();

    GeneroResumen obtenerPorId(Long id);

    GeneroResumen crear(GeneroRequest request);

    GeneroResumen actualizar(Long id, GeneroRequest request);

    void cambiarEstado(Long id, boolean estado);

}
