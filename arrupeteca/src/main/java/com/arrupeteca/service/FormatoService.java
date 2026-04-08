package com.arrupeteca.service;

import com.arrupeteca.dto.request.FormatoRequest;
import com.arrupeteca.persistence.projection.FormatoResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FormatoService {

    Page<FormatoResumen> obtenerTodosActivos(Pageable pageable);

    FormatoResumen obtenerPorIdActivo(Long id);

    Page<FormatoResumen> obtenerTodos(Pageable pageable);

    FormatoResumen obtenerPorId(Long id);

    FormatoResumen crear(FormatoRequest request);

    FormatoResumen actualizar(Long id, FormatoRequest request);

    void cambiarEstado(Long id, boolean estado);
}
