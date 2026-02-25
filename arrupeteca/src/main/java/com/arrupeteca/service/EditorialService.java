package com.arrupeteca.service;

import com.arrupeteca.dto.request.EditorialRequest;
import com.arrupeteca.persistence.projection.EditorialResumen;

import java.util.List;

public interface EditorialService {

    List<EditorialResumen> obtenerTodosActivos();

    EditorialResumen obtenerPorIdActivo(Long id);

    List<EditorialResumen> obtenerTodos();

    EditorialResumen obtenerPorId(Long id);

    EditorialResumen crear(EditorialRequest request);

    EditorialResumen actualizar(Long id, EditorialRequest request);

    void cambiarEstado(Long id, boolean estado);
    
}
