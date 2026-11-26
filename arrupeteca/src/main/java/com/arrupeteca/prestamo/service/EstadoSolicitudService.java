package com.arrupeteca.prestamo.service;

import com.arrupeteca.prestamo.dto.request.EstadoSolicitudRequest;
import com.arrupeteca.prestamo.dto.response.EstadoSolicitudResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EstadoSolicitudService {

    List<EstadoSolicitudResponse> obtenerTodosActivos(String nombre);

    Page<EstadoSolicitudResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable);

    EstadoSolicitudResponse obtenerPorId(Long id);

    EstadoSolicitudResponse crear(EstadoSolicitudRequest request);

    EstadoSolicitudResponse actualizar(Long id, EstadoSolicitudRequest request);

    void cambiarEstado(Long id, boolean estado);
}

