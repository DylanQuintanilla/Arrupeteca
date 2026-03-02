package com.arrupeteca.service;

import com.arrupeteca.dto.request.AutorRequest;
import com.arrupeteca.persistence.projection.AutorResumen;
import org.springframework.data.domain.Page;

public interface AutorService {

    Page<AutorResumen> obtenerTodosActivos(int page, int size, String ordenarPor, String direccion);

    AutorResumen obtenerPorIdActivo(Long id);

    // Admin
    Page<AutorResumen> obtenerTodos(int page, int size, String ordenarPor, String direccion);

    AutorResumen obtenerPorId(Long id);


    Page<AutorResumen> busquedaGlobalAvanzada(String palabra, Integer inicio, Integer fin,
                                              int page, int size, String ordenarPor, String direccion);

    Page<AutorResumen> busquedaMaestraAdmin(Boolean esBorrado, Long idNacionalidad, Long idPais,
                                            Long idGenero, Long idObra, String terminoBusqueda,
                                            Integer anioInicio, Integer anioFin,
                                            int page, int size, String ordenarPor, String direccion);

    Page<AutorResumen> buscarAutoresVivosEnRango(Integer anioInicio, Integer anioFin,
                                                 int page, int size, String ordenarPor, String direccion);

    AutorResumen crear(AutorRequest request);

    AutorResumen actualizar(Long id, AutorRequest request);

    void cambiarEstadoLogico(Long id, boolean estado);
}