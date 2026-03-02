package com.arrupeteca.service;

import com.arrupeteca.dto.request.LibroRequest;
import com.arrupeteca.persistence.projection.LibroResumen;
import org.springframework.data.domain.Page;

public interface LibroService {

    Page<LibroResumen> obtenerTodosActivos(int page, int size, String ordenarPor, String direccion);

    LibroResumen obtenerPorIdActivo(Long id);

    Page<LibroResumen> busquedaGlobal(String palabra, int page, int size, String ordenarPor, String direccion);

    Page<LibroResumen> obtenerTodos(int page, int size, String ordenarPor, String direccion);

    LibroResumen obtenerPorId(Long id);

    Page<LibroResumen> busquedaMaestraAdmin(
            Boolean esBorrado, Long idObra, Long idEditorial, Long idFormato,
            String grado, String ciclo, String termino, Integer anioInicio, Integer anioFin,
            int page, int size, String ordenarPor, String direccion);

    LibroResumen crear(LibroRequest request);

    LibroResumen actualizar(Long id, LibroRequest request);

    void cambiarEstado(Long id, boolean estado);
}