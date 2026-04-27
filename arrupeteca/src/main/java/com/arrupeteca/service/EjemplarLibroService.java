package com.arrupeteca.service;

import com.arrupeteca.dto.request.EjemplarLibroRequest;
import com.arrupeteca.dto.response.detail.EjemplarLibroDetalleResponse;
import com.arrupeteca.dto.response.summary.EjemplarLibroResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EjemplarLibroService {

    Page<EjemplarLibroResumen> obtenerTodosActivos(Pageable pageable);

    EjemplarLibroDetalleResponse obtenerPorIdActivo(Long id);

    Page<EjemplarLibroResumen> busquedaGlobal(String palabra, Pageable pageable);

    Page<EjemplarLibroResumen> obtenerTodos(Pageable pageable);

    EjemplarLibroDetalleResponse obtenerPorId(Long id);

    Page<EjemplarLibroResumen> busquedaMaestraAdmin(
            Boolean esBorrado, Long idLibro, Long idMueble, Object estadoFisico,
            Object disponibilidad, String termino, Pageable pageable);

    EjemplarLibroDetalleResponse crear(EjemplarLibroRequest request);

    EjemplarLibroDetalleResponse actualizar(Long id, EjemplarLibroRequest request);

    void cambiarEstado(Long id, boolean estado);
}
