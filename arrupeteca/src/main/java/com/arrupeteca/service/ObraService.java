package com.arrupeteca.service;

import com.arrupeteca.dto.request.ObraRequest;
import com.arrupeteca.persistence.projection.ObraResumen;

import java.util.List;

public interface ObraService {

    List<ObraResumen> obtenerTodasActivas();

    ObraResumen obtenerPorIdActivo(Long id);

    List<ObraResumen> busquedaGlobalAvanzada(String palabra, String ordenarPor, String direccion);

    List<ObraResumen> obtenerTodas();

    ObraResumen obtenerPorId(Long id);

    List<ObraResumen> busquedaMaestraAdmin(Boolean esBorrado, Long idGenero, Long idCategoria, Long idAutor,
                                           String termino, Integer anioInicio, Integer anioFin, String ordenarPor, String direccion);

    ObraResumen crear(ObraRequest request);

    ObraResumen actualizar(Long id, ObraRequest request);

    void cambiarEstadoLogico(Long id, boolean estado);
}
