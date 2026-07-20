package com.arrupeteca.service;

import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.dto.response.CategoriaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoriaService {

    Page<CategoriaResponse> obtenerTodosActivos(Pageable pageable);

    CategoriaResponse obtenerPorIdActivo(Long id);

    Page<CategoriaResponse> obtenerTodos(Pageable pageable);

    CategoriaResponse obtenerPorId(Long id);

    CategoriaResponse crear(CategoriaRequest request);

    CategoriaResponse actualizar(Long id, CategoriaRequest request);

    void cambiarEstado(Long id, boolean estado);
}
