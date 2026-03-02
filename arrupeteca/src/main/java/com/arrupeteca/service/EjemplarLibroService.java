package com.arrupeteca.service;

import com.arrupeteca.dto.request.EjemplarLibroRequest;
import com.arrupeteca.persistence.projection.EjemplarLibroResumen;
import org.springframework.data.domain.Page;

public interface EjemplarLibroService {

    Page<EjemplarLibroResumen> obtenerTodosActivos(int page, int size, String ordenarPor, String direccion);

    EjemplarLibroResumen obtenerPorIdActivo(Long id);

    Page<EjemplarLibroResumen> busquedaGlobal(String palabra, int page, int size, String ordenarPor, String direccion);

    Page<EjemplarLibroResumen> obtenerTodos(int page, int size, String ordenarPor, String direccion);

    EjemplarLibroResumen obtenerPorId(Long id);

    Page<EjemplarLibroResumen> busquedaMaestraAdmin(
            Boolean esBorrado, Long idLibro, Long idMueble, String estadoFisico, String disponibilidad,
            String termino, int page, int size, String ordenarPor, String direccion);

    EjemplarLibroResumen crear(EjemplarLibroRequest request);

    EjemplarLibroResumen actualizar(Long id, EjemplarLibroRequest request);

    void cambiarEstado(Long id, boolean estado);
}
