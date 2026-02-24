package com.arrupeteca.service;

import com.arrupeteca.dto.request.PaisRequest;
import com.arrupeteca.persistence.projection.PaisResumen;

import java.util.List;

public interface PaisService {

    List<PaisResumen> obtenerTodosActivos();

    PaisResumen obtenerPorIdActivo(Long id);

    List<PaisResumen> obtenerTodos();

    PaisResumen obtenerPorId(Long id);

    PaisResumen crear(PaisRequest paisRequest);

    PaisResumen actualizar(Long id, PaisRequest request);

    void cambiarEstadoLogico(Long id, boolean estado);



}
