package com.arrupeteca.service;

import com.arrupeteca.dto.request.PaisRequest;
import com.arrupeteca.dto.response.summary.PaisResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaisService {

    Page<PaisResumen> obtenerTodosActivos(Pageable pageable);

    PaisResumen obtenerPorIdActivo(Long id);

    Page<PaisResumen> obtenerTodos(Pageable pageable);

    PaisResumen obtenerPorId(Long id);

    PaisResumen crear(PaisRequest request);

    PaisResumen actualizar(Long id, PaisRequest request);

    void cambiarEstado(Long id, boolean estado);
}
