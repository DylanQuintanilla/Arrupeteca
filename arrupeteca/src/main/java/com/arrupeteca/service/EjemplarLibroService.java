package com.arrupeteca.service;

import com.arrupeteca.dto.request.EjemplarLibroRequest;
import com.arrupeteca.dto.response.detail.EjemplarLibroDetalleResponse;
import com.arrupeteca.dto.response.summary.EjemplarLibroResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EjemplarLibroService {

    Page<EjemplarLibroResumen> busquedaAdmin(
            Boolean borradoLogico, Long idLibro, Long idGenero, Long idCategoria, Long idEtiqueta,
            Long idMueble, Long idEstadoFisico, Long idDisponibilidad,
            String termino, Pageable pageable);

    EjemplarLibroDetalleResponse busquedaAdminPorId(Long id);

    EjemplarLibroDetalleResponse crear(EjemplarLibroRequest request);

    EjemplarLibroDetalleResponse actualizar(Long id, EjemplarLibroRequest request);

    void cambiarEstado(Long id, boolean estado);
}

