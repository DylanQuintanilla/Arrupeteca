package com.arrupeteca.service;

import com.arrupeteca.dto.request.EdificioRequest;
import com.arrupeteca.persistence.projection.EdificioResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EdificioService {

    Page<EdificioResumen> obtenerTodosActivos(Pageable pageable);

    EdificioResumen obtenerPorIdActivo(Long id);

    Page<EdificioResumen> obtenerTodos(Pageable pageable);

    EdificioResumen obtenerPorId(Long id);

    EdificioResumen crear(EdificioRequest request);

    EdificioResumen actualizar(Long id, EdificioRequest request);

    void cambiarEstado(Long id, boolean estado);
}
