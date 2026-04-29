package com.arrupeteca.service;

import com.arrupeteca.dto.request.AutorRequest;
import com.arrupeteca.dto.response.detail.AutorDetalleResponse;
import com.arrupeteca.dto.response.summary.AutorResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AutorService {

    Page<AutorResumen> obtenerTodosActivos(Pageable pageable);

    AutorDetalleResponse obtenerPorIdActivo(Long id);

    Page<AutorResumen> obtenerTodos(Pageable pageable);

    AutorDetalleResponse obtenerPorId(Long id);

    Page<AutorResumen> busquedaGlobal(String palabra, Integer anioInicio, Integer anioFin, Pageable pageable);

    Page<AutorResumen> busquedaMaestraAdmin(
            Boolean esBorrado, Long idNacionalidad, Long idPais, Long idGenero,
            String termino, Pageable pageable);

    AutorDetalleResponse crear(AutorRequest request);

    AutorDetalleResponse actualizar(Long id, AutorRequest request);

    void cambiarEstado(Long id, boolean estado);

    List<AutorResumen> buscarDuplicados(String termino, Long idExcluir);
}
