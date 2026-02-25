package com.arrupeteca.service;

import com.arrupeteca.dto.request.AutorRequest;
import com.arrupeteca.persistence.projection.AutorResumen;

import java.util.List;

public interface AutorService {

    List<AutorResumen> obtenerTodosActivos();

    AutorResumen obtenerPorIdActivo(Long id);

    List<AutorResumen> obtenerTodos();

    AutorResumen obtenerPorId(Long id);

    List<AutorResumen> busquedaGlobalAvanzada(String palabra, Integer inicio, Integer fin, String ordenarPor, String direccion);

    List<AutorResumen> busquedaMaestraAdmin(Boolean esBorrado, Long idNacionalidad, Long idPais,
                                            Long idGenero, Long idObra, String terminoBusqueda,
                                            Integer anioInicio, Integer anioFin,
                                            String ordenarPor, String direccion);

    List<AutorResumen> buscarAutoresVivosEnRango(Integer anioInicio, Integer anioFin);

    AutorResumen crear(AutorRequest request);

    AutorResumen actualizar(Long id, AutorRequest request);

    void cambiarEstadoLogico(Long id, boolean estado);
}