package com.arrupeteca.service;

import com.arrupeteca.dto.request.EstadoFisicoRequest;
import com.arrupeteca.dto.response.EstadoFisicoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EstadoFisicoService {

    List<EstadoFisicoResponse> obtenerTodosActivos();

    Page<EstadoFisicoResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable);

    EstadoFisicoResponse obtenerPorId(Long id);

    EstadoFisicoResponse crear(EstadoFisicoRequest request);

    EstadoFisicoResponse actualizar(Long id, EstadoFisicoRequest request);

    void cambiarEstado(Long id, boolean estado);
}
