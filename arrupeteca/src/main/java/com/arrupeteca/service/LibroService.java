package com.arrupeteca.service;

import com.arrupeteca.dto.request.LibroRequest;
import com.arrupeteca.dto.response.detail.LibroDetalleResponse;
import com.arrupeteca.dto.response.summary.LibroResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LibroService {

    Page<LibroResumen> obtenerTodosActivos(Pageable pageable);

    LibroDetalleResponse obtenerPorIdActivo(Long id);

    Page<LibroResumen> busquedaGlobal(String palabra, Long idGenero, Long idCategoria, Pageable pageable);

    Page<LibroResumen> obtenerTodos(Pageable pageable);

    LibroDetalleResponse obtenerPorId(Long id);

    Page<LibroResumen> busquedaMaestraAdmin(
            Boolean esBorrado, Long idObra, Long idGenero, Long idCategoria, Long idEtiqueta,
            Long idEditorial, Long idFormato, Long idGrado, Long idCiclo,
            String termino, Integer anioInicio, Integer anioFin,
            Pageable pageable);

    Page<LibroResumen> buscarPorObraId(Long idObra, Pageable pageable);

    LibroDetalleResponse crear(LibroRequest request);

    LibroDetalleResponse actualizar(Long id, LibroRequest request);

    void cambiarEstado(Long id, boolean estado);
}
