package com.arrupeteca.service;

import com.arrupeteca.dto.request.ObraRequest;
import com.arrupeteca.persistence.projection.ObraResumen;
import org.springframework.data.domain.Page;

public interface ObraService {

    Page<ObraResumen> obtenerTodosActivos(int page, int size, String ordenarPor, String direccion);

    ObraResumen obtenerPorIdActivo(Long id);

    Page<ObraResumen> busquedaGlobal(String palabra, int page, int size, String ordenarPor, String direccion);

    Page<ObraResumen> obtenerTodos(int page, int size, String ordenarPor, String direccion);

    ObraResumen obtenerPorId(Long id);

    Page<ObraResumen> busquedaMaestraAdmin(
            Boolean esBorrado, String termino, Integer anioInicio, Integer anioFin,
            int page, int size, String ordenarPor, String direccion);

    ObraResumen crear(ObraRequest request);

    ObraResumen actualizar(Long id, ObraRequest request);

    void cambiarEstado(Long id, boolean estado);
}