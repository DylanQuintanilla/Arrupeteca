package com.arrupeteca.service;

import com.arrupeteca.dto.request.AutorRequest;
import com.arrupeteca.dto.response.detail.AutorDetalleResponse;
import com.arrupeteca.dto.response.summary.AutorResumenResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Year;
import java.util.List;

public interface AutorService {

    Page<AutorResumenResponse> obtenerTodosActivos(Pageable pageable);

    AutorDetalleResponse obtenerPorIdActivo(Long id);

    Page<AutorResumenResponse> obtenerTodos(Pageable pageable);

    AutorDetalleResponse obtenerPorId(Long id);

    Page<AutorResumenResponse> busquedaGlobal(String palabra, Year anioInicio, Year anioFin, Pageable pageable);

    Page<AutorResumenResponse> busquedaMaestraAdmin(
            Boolean esBorrado, Long idNacionalidad, Long idPais, Long idGenero,
            String termino, Pageable pageable);

    AutorDetalleResponse crear(AutorRequest request);

    AutorDetalleResponse actualizar(Long id, AutorRequest request);

    void cambiarEstado(Long id, boolean estado);

    List<AutorResumenResponse> buscarDuplicados(String termino, Long idExcluir);
}
