package com.arrupeteca.service;

import com.arrupeteca.dto.request.AutorRequest;
import com.arrupeteca.dto.response.detail.AutorDetalleResponse;
import com.arrupeteca.dto.response.summary.AutorResumenResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Year;
import java.util.List;

public interface AutorService {

    Page<AutorResumenResponse> busquedaGlobal(
            Long idNacionalidad, Long idPais, Long idGenero, Long idObraCumbre,
            Year anioInicio, Year anioFin, String termino, Pageable pageable);

    AutorDetalleResponse busquedaGlobalPorId(Long id);

    Page<AutorResumenResponse> busquedaAdmin(
            Boolean borradoLogico, Long idNacionalidad, Long idPais, Long idGenero, Long idObraCumbre,
            Year anioInicio, Year anioFin, String termino, Pageable pageable);

    AutorDetalleResponse busquedaAdminPorId(Long id);

    AutorDetalleResponse crear(AutorRequest request);

    AutorDetalleResponse actualizar(Long id, AutorRequest request);

    void cambiarEstado(Long id, boolean estado);

    List<AutorResumenResponse> buscarDuplicados(String termino, Long idExcluir);
}
