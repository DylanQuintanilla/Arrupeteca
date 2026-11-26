package com.arrupeteca.prestamo.service;

import com.arrupeteca.prestamo.dto.request.EstadoEjemplarPrestamoRequest;
import com.arrupeteca.prestamo.dto.response.EstadoEjemplarPrestamoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EstadoEjemplarPrestamoService {

    List<EstadoEjemplarPrestamoResponse> obtenerTodosActivos(String nombre);

    Page<EstadoEjemplarPrestamoResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable);

    EstadoEjemplarPrestamoResponse obtenerPorId(Long id);

    EstadoEjemplarPrestamoResponse crear(EstadoEjemplarPrestamoRequest request);

    EstadoEjemplarPrestamoResponse actualizar(Long id, EstadoEjemplarPrestamoRequest request);

    void cambiarEstado(Long id, boolean estado);
}

