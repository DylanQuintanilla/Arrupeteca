package com.arrupeteca.service;

import com.arrupeteca.dto.request.EditorialRequest;
import com.arrupeteca.persistence.projection.EditorialResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EditorialService {

    Page<EditorialResumen> obtenerTodosActivos(Pageable pageable);

    EditorialResumen obtenerPorIdActivo(Long id);

    Page<EditorialResumen> obtenerTodos(Pageable pageable);

    Page<EditorialResumen> busquedaMaestraAdmin(Boolean esBorrado, String termino, Pageable pageable);

    EditorialResumen obtenerPorId(Long id);

    EditorialResumen crear(EditorialRequest request);

    EditorialResumen actualizar(Long id, EditorialRequest request);

    void cambiarEstado(Long id, boolean estado);
}
