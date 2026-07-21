package com.arrupeteca.service;

import com.arrupeteca.dto.request.EditorialRequest;
import com.arrupeteca.dto.response.EditorialResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EditorialService {

    Page<EditorialResponse> obtenerTodosActivos(Pageable pageable);

    EditorialResponse obtenerPorIdActivo(Long id);

    Page<EditorialResponse> obtenerTodos(Pageable pageable);

    EditorialResponse obtenerPorId(Long id);

    Page<EditorialResponse> busquedaMaestraAdmin(Boolean esBorrado, String termino, Pageable pageable);

    EditorialResponse crear(EditorialRequest request);

    EditorialResponse actualizar(Long id, EditorialRequest request);

    void cambiarEstado(Long id, boolean estado);
}
