package com.arrupeteca.service;

import com.arrupeteca.dto.request.FormatoRequest;
import com.arrupeteca.persistence.projection.FormatoResumen;

import java.util.List;

public interface FormatoService {

    List<FormatoResumen> obtenerTodosActivos();

    FormatoResumen obtenerPorIdActivo(Long id);

    List<FormatoResumen> obtenerTodos();

    FormatoResumen obtenerPorId(Long id);

    FormatoResumen crear(FormatoRequest request);

    FormatoResumen actualizar(Long id, FormatoRequest request);

    void cambiarEstado(Long id, boolean estado);

}