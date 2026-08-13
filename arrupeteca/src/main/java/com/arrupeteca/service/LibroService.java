package com.arrupeteca.service;

import com.arrupeteca.dto.request.LibroRequest;
import com.arrupeteca.dto.response.detail.LibroDetalleResponse;
import com.arrupeteca.dto.response.summary.LibroResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LibroService {

    Page<LibroResumen> busquedaGlobal(
            String termino, Long idAutor, Long idGenero, Long idCategoria,
            Long idEditorial, Long idIdioma, Long idCiclo, Long idEtiqueta,
            Integer anioInicio, Integer anioFin, Pageable pageable);

    LibroDetalleResponse busquedaGlobalPorId(Long id);

    Page<LibroResumen> busquedaAdmin(
            Boolean borradoLogico, Long idObra, Long idAutor, Long idGenero, Long idCategoria, Long idEtiqueta,
            Long idEditorial, Long idFormato, Long idGrado, Long idCiclo, Long idIdioma,
            String termino, Integer anioInicio, Integer anioFin,
            Pageable pageable);

    LibroDetalleResponse busquedaAdminPorId(Long id);

    Page<LibroResumen> buscarPorObraId(Long idObra, Pageable pageable);

    LibroDetalleResponse crear(LibroRequest request);

    LibroDetalleResponse actualizar(Long id, LibroRequest request);

    void cambiarEstado(Long id, boolean estado);
}

