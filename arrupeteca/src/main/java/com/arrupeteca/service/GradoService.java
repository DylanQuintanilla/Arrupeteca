package com.arrupeteca.service;

import com.arrupeteca.dto.request.GradoRequest;
import com.arrupeteca.dto.response.GradoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GradoService {

    Page<GradoResponse> obtenerTodosActivos(Pageable pageable);

    GradoResponse obtenerPorIdActivo(Long id);

    Page<GradoResponse> obtenerTodos(Pageable pageable);

    GradoResponse obtenerPorId(Long id);

    GradoResponse crear(GradoRequest request);

    GradoResponse actualizar(Long id, GradoRequest request);

    void cambiarEstado(Long id, boolean estado);
}
