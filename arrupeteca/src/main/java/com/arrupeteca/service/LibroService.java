package com.arrupeteca.service;

import com.arrupeteca.dto.request.LibroRequest;
import com.arrupeteca.persistence.projection.LibroResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LibroService {

    Page<LibroResumen> obtenerTodosActivos(Pageable pageable);

    LibroResumen obtenerPorIdActivo(Long id);

    Page<LibroResumen> busquedaGlobal(String palabra, Pageable pageable);

    Page<LibroResumen> obtenerTodos(Pageable pageable);

    LibroResumen obtenerPorId(Long id);

    Page<LibroResumen> busquedaMaestraAdmin(
            Boolean esBorrado, Long idObra, Long idEditorial, Long idFormato,
            String grado, String ciclo, String termino, Integer anioInicio, Integer anioFin,
            Pageable pageable);

    Page<LibroResumen> obtenerPorObra(Long idObra, Pageable pageable);

    LibroResumen crear(LibroRequest request);

    LibroResumen actualizar(Long id, LibroRequest request);

    void cambiarEstado(Long id, boolean estado);
}
