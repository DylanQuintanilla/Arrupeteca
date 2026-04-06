package com.arrupeteca.service;

import com.arrupeteca.dto.request.EditorialRequest;
import com.arrupeteca.persistence.projection.EditorialResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EditorialService {

    Page<EditorialResumen> obtenerTodosActivos(int page, int size, String ordenarPor, String direccion);

    EditorialResumen obtenerPorIdActivo(Long id);

    Page<EditorialResumen> obtenerTodos(int page, int size, String ordenarPor, String direccion);

    Page<EditorialResumen> busquedaMaestraAdmin(Boolean esBorrado, String termino, int page, int size, String ordenarPor, String direccion);

    EditorialResumen obtenerPorId(Long id);

    EditorialResumen crear(EditorialRequest request);

    EditorialResumen actualizar(Long id, EditorialRequest request);

    void cambiarEstado(Long id, boolean estado);
    
}
