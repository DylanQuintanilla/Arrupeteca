package com.arrupeteca.service;

import com.arrupeteca.dto.request.AutorRequest;
import com.arrupeteca.persistence.projection.AutorResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AutorService {

    Page<AutorResumen> obtenerTodosActivos(Pageable pageable);

    AutorResumen obtenerPorIdActivo(Long id);

    // Admin
    Page<AutorResumen> obtenerTodos(Pageable pageable);

    AutorResumen obtenerPorId(Long id);

    Page<AutorResumen> busquedaGlobalAvanzada(String palabra, Integer inicio, Integer fin, Pageable pageable);

    Page<AutorResumen> busquedaMaestraAdmin(Boolean esBorrado, Long idNacionalidad, Long idPais,
                                            Long idGenero, Long idObra, String terminoBusqueda,
                                            Integer anioInicio, Integer anioFin,
                                            Pageable pageable);

    Page<AutorResumen> buscarAutoresVivosEnRango(Integer anioInicio, Integer anioFin, Pageable pageable);

    AutorResumen crear(AutorRequest request);

    AutorResumen actualizar(Long id, AutorRequest request);

    void cambiarEstado(Long id, boolean estado);

    List<AutorResumen> verificarDuplicados(String termino, Long idExcluir);
}
