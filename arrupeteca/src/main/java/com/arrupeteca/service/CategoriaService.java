package com.arrupeteca.service;

import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.persistence.projection.CategoriaResumen;

import java.util.List;

public interface CategoriaService {

    List<CategoriaResumen> obtenerTodosActivos();

    CategoriaResumen obtenerPorIdActivo(Long id);

    List<CategoriaResumen> obtenerTodos();

    CategoriaResumen obtenerPorId(Long id);

    CategoriaResumen crear(CategoriaRequest request);

    CategoriaResumen actualizar(Long id, CategoriaRequest request);

    void cambiarEstado(Long id, boolean estado);

}
