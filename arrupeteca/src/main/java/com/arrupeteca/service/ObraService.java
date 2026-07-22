package com.arrupeteca.service;

import com.arrupeteca.dto.request.ObraRequest;
import com.arrupeteca.dto.response.detail.ObraDetalleResponse;
import com.arrupeteca.dto.response.summary.ObraResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ObraService {

    Page<ObraResumen> obtenerTodosActivos(Pageable pageable);

    ObraDetalleResponse obtenerPorIdActivo(Long id);

    Page<ObraResumen> busquedaGlobal(String palabra, Pageable pageable);

    Page<ObraResumen> obtenerTodos(Pageable pageable);

    ObraDetalleResponse obtenerPorId(Long id);

    Page<ObraResumen> busquedaMaestraAdmin(
            Boolean esBorrado, Long idGenero, Long idCategoria, String termino, Integer anioInicio, Integer anioFin,
            Pageable pageable);

    ObraDetalleResponse crear(ObraRequest request);

    ObraDetalleResponse actualizar(Long id, ObraRequest request);

    void cambiarEstado(Long id, boolean estado);
}
