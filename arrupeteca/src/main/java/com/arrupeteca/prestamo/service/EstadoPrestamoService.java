package com.arrupeteca.prestamo.service;

import com.arrupeteca.prestamo.dto.request.EstadoPrestamoRequest;
import com.arrupeteca.prestamo.dto.response.EstadoPrestamoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EstadoPrestamoService {

    List<EstadoPrestamoResponse> obtenerTodosActivos(String nombre);

    Page<EstadoPrestamoResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable);

    EstadoPrestamoResponse obtenerPorId(Long id);

    EstadoPrestamoResponse crear(EstadoPrestamoRequest request);

    EstadoPrestamoResponse actualizar(Long id, EstadoPrestamoRequest request);

    void cambiarEstado(Long id, boolean estado);
}

