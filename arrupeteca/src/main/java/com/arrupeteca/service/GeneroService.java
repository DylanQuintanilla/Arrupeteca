package com.arrupeteca.service;

import com.arrupeteca.dto.request.GeneroRequest;
import com.arrupeteca.dto.response.GeneroResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GeneroService {

    Page<GeneroResponse> obtenerTodosActivos(Pageable pageable);

    GeneroResponse obtenerPorIdActivo(Long id);

    Page<GeneroResponse> obtenerTodos(Pageable pageable);

    GeneroResponse obtenerPorId(Long id);

    GeneroResponse crear(GeneroRequest request);

    GeneroResponse actualizar(Long id, GeneroRequest request);

    void cambiarEstado(Long id, boolean estado);
}
