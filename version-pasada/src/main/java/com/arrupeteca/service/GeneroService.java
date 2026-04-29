package com.arrupeteca.service;

import com.arrupeteca.dto.request.GeneroRequest;
import com.arrupeteca.dto.response.detail.GeneroDetalleResponse;
import com.arrupeteca.dto.response.summary.GeneroResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GeneroService {

    Page<GeneroResumen> obtenerTodosActivos(Pageable pageable);

    GeneroDetalleResponse obtenerPorIdActivo(Long id);

    Page<GeneroResumen> obtenerTodos(Pageable pageable);

    GeneroDetalleResponse obtenerPorId(Long id);

    GeneroDetalleResponse crear(GeneroRequest request);

    GeneroDetalleResponse actualizar(Long id, GeneroRequest request);

    void cambiarEstado(Long id, boolean estado);
}
