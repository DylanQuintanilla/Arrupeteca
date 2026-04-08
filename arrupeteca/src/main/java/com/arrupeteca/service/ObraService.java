package com.arrupeteca.service;

import com.arrupeteca.dto.request.ObraRequest;
import com.arrupeteca.persistence.projection.ObraResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ObraService {

    Page<ObraResumen> obtenerTodosActivos(Pageable pageable);

    ObraResumen obtenerPorIdActivo(Long id);

    Page<ObraResumen> busquedaGlobal(String palabra, Pageable pageable);

    Page<ObraResumen> obtenerTodos(Pageable pageable);

    ObraResumen obtenerPorId(Long id);

    Page<ObraResumen> busquedaMaestraAdmin(
            Boolean esBorrado, String termino, Integer anioInicio, Integer anioFin,
            Pageable pageable);

    ObraResumen crear(ObraRequest request);

    ObraResumen actualizar(Long id, ObraRequest request);

    void cambiarEstado(Long id, boolean estado);
}
