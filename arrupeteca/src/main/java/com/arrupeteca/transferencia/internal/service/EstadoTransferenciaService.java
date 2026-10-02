package com.arrupeteca.transferencia.internal.service;

import com.arrupeteca.transferencia.dto.request.EstadoTransferenciaRequest;
import com.arrupeteca.transferencia.dto.response.EstadoTransferenciaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EstadoTransferenciaService {

    List<EstadoTransferenciaResponse> obtenerTodosActivos(String nombre);

    Page<EstadoTransferenciaResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable);

    EstadoTransferenciaResponse obtenerPorId(Long id);

    EstadoTransferenciaResponse crear(EstadoTransferenciaRequest request);

    EstadoTransferenciaResponse actualizar(Long id, EstadoTransferenciaRequest request);

    void cambiarEstado(Long id, boolean estado);
}
