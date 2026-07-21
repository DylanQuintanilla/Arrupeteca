package com.arrupeteca.service;

import com.arrupeteca.dto.request.FormatoRequest;
import com.arrupeteca.dto.response.FormatoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FormatoService {

    Page<FormatoResponse> obtenerTodosActivos(Pageable pageable);

    FormatoResponse obtenerPorIdActivo(Long id);

    Page<FormatoResponse> obtenerTodos(Pageable pageable);

    FormatoResponse obtenerPorId(Long id);

    FormatoResponse crear(FormatoRequest request);

    FormatoResponse actualizar(Long id, FormatoRequest request);

    void cambiarEstado(Long id, boolean estado);
}
