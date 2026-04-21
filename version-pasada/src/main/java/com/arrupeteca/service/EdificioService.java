package com.arrupeteca.service;

import com.arrupeteca.dto.request.EdificioRequest;
import com.arrupeteca.persistence.projection.EdificioResumen;

import java.util.List;

public interface EdificioService {

    List<EdificioResumen> obtenerTodosActivos();

    EdificioResumen obtenerPorIdActivo(Long id);

    List<EdificioResumen> obtenerTodos();

    EdificioResumen obtenerPorId(Long id);

    EdificioResumen crear(EdificioRequest request);

    EdificioResumen actualizar(Long id, EdificioRequest request);

    void cambiarEstado(Long id, boolean estado);

}