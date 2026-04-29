package com.arrupeteca.service;

import com.arrupeteca.dto.request.EditorialRequest;
import com.arrupeteca.dto.response.detail.EditorialDetalleResponse;
import com.arrupeteca.dto.response.summary.EditorialResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EditorialService {

    Page<EditorialResumen> obtenerTodosActivos(Pageable pageable);

    EditorialDetalleResponse obtenerPorIdActivo(Long id);

    Page<EditorialResumen> obtenerTodos(Pageable pageable);

    EditorialDetalleResponse obtenerPorId(Long id);

    Page<EditorialResumen> busquedaMaestraAdmin(Boolean esBorrado, String termino, Pageable pageable);

    EditorialDetalleResponse crear(EditorialRequest request);

    EditorialDetalleResponse actualizar(Long id, EditorialRequest request);

    void cambiarEstado(Long id, boolean estado);
}
