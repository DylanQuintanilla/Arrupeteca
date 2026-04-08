package com.arrupeteca.service;

import com.arrupeteca.dto.request.EjemplarLibroRequest;
import com.arrupeteca.persistence.projection.EjemplarLibroResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EjemplarLibroService {

    Page<EjemplarLibroResumen> obtenerTodosActivos(Pageable pageable);

    EjemplarLibroResumen obtenerPorIdActivo(Long id);

    Page<EjemplarLibroResumen> busquedaGlobal(String palabra, Pageable pageable);

    Page<EjemplarLibroResumen> obtenerTodos(Pageable pageable);

    EjemplarLibroResumen obtenerPorId(Long id);

    Page<EjemplarLibroResumen> busquedaMaestraAdmin(
            Boolean esBorrado, Long idLibro, Long idMueble, String estadoFisico, String disponibilidad,
            String termino, Pageable pageable);

    EjemplarLibroResumen crear(EjemplarLibroRequest request);

    EjemplarLibroResumen actualizar(Long id, EjemplarLibroRequest request);

    void cambiarEstado(Long id, boolean estado);
}
