package com.arrupeteca.service;

import com.arrupeteca.dto.request.EstadoFisicoRequest;
import com.arrupeteca.dto.response.EstadoFisicoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EstadoFisicoService {

    Page<EstadoFisicoResponse> obtenerTodosActivos(Pageable pageable);

    EstadoFisicoResponse obtenerPorIdActivo(Long id);

    Page<EstadoFisicoResponse> obtenerTodos(Pageable pageable);

    EstadoFisicoResponse obtenerPorId(Long id);

    EstadoFisicoResponse crear(EstadoFisicoRequest request);

    EstadoFisicoResponse actualizar(Long id, EstadoFisicoRequest request);

    void cambiarEstado(Long id, boolean estado);
}
