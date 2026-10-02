package com.arrupeteca.transferencia.internal.service;

import com.arrupeteca.transferencia.dto.request.EstadoSolicitudTransferenciaRequest;
import com.arrupeteca.transferencia.dto.response.EstadoSolicitudTransferenciaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EstadoSolicitudTransferenciaService {

    List<EstadoSolicitudTransferenciaResponse> obtenerTodosActivos(String nombre);

    Page<EstadoSolicitudTransferenciaResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable);

    EstadoSolicitudTransferenciaResponse obtenerPorId(Long id);

    EstadoSolicitudTransferenciaResponse crear(EstadoSolicitudTransferenciaRequest request);

    EstadoSolicitudTransferenciaResponse actualizar(Long id, EstadoSolicitudTransferenciaRequest request);

    void cambiarEstado(Long id, boolean estado);
}
