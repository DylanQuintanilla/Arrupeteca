package com.arrupeteca.service;

import com.arrupeteca.dto.request.ObraRequest;
import com.arrupeteca.dto.response.detail.ObraDetalleResponse;
import com.arrupeteca.dto.response.summary.ObraResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ObraService {

    Page<ObraResumen> busquedaAdmin(
            Boolean borradoLogico, Long idGenero, Long idCategoria, Long idEtiqueta,
            String termino, Integer anioInicio, Integer anioFin,
            Pageable pageable);

    ObraDetalleResponse busquedaAdminPorId(Long id);

    ObraDetalleResponse crear(ObraRequest request);

    ObraDetalleResponse actualizar(Long id, ObraRequest request);

    void cambiarEstado(Long id, boolean estado);

    java.util.List<ObraResumen> buscarDuplicados(String termino, Long idExcluir);
}

