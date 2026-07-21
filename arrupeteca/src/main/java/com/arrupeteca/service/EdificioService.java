package com.arrupeteca.service;

import com.arrupeteca.dto.request.EdificioRequest;
import com.arrupeteca.dto.response.EdificioResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EdificioService {

    Page<EdificioResponse> obtenerTodosActivos(Pageable pageable);

    EdificioResponse obtenerPorIdActivo(Long id);

    Page<EdificioResponse> obtenerTodos(Pageable pageable);

    EdificioResponse obtenerPorId(Long id);

    EdificioResponse crear(EdificioRequest request);

    EdificioResponse actualizar(Long id, EdificioRequest request);

    void cambiarEstado(Long id, boolean estado);
}
