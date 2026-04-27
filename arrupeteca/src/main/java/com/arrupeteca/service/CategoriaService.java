package com.arrupeteca.service;

import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.dto.response.detail.CategoriaDetalleResponse;
import com.arrupeteca.dto.response.summary.CategoriaResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoriaService {

    Page<CategoriaResumen> obtenerTodosActivos(Pageable pageable);

    CategoriaDetalleResponse obtenerPorIdActivo(Long id);

    Page<CategoriaResumen> obtenerTodos(Pageable pageable);

    CategoriaDetalleResponse obtenerPorId(Long id);

    CategoriaDetalleResponse crear(CategoriaRequest request);

    CategoriaDetalleResponse actualizar(Long id, CategoriaRequest request);

    void cambiarEstado(Long id, boolean estado);
}
