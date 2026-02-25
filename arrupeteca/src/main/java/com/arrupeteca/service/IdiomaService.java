package com.arrupeteca.service;

import com.arrupeteca.dto.request.IdiomaRequest;
import com.arrupeteca.persistence.projection.IdiomaResumen;

import java.util.List;

public interface IdiomaService {

    List<IdiomaResumen> obtenerTodosActivos();

    IdiomaResumen obtenerPorIdActivo(Long id);

    List<IdiomaResumen> obtenerTodos();

    IdiomaResumen obtenerPorId(Long id);

    IdiomaResumen crear(IdiomaRequest request);

    IdiomaResumen actualizar(Long id, IdiomaRequest request);

    void cambiarEstado(Long id, boolean estado);

}