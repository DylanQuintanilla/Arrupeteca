package com.arrupeteca.service;

import com.arrupeteca.dto.request.DisponibilidadRequest;
import com.arrupeteca.dto.response.DisponibilidadResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DisponibilidadService {

    List<DisponibilidadResponse> obtenerTodosActivos();

    Page<DisponibilidadResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable);

    DisponibilidadResponse obtenerPorId(Long id);

    DisponibilidadResponse crear(DisponibilidadRequest request);

    DisponibilidadResponse actualizar(Long id, DisponibilidadRequest request);

    void cambiarEstado(Long id, boolean estado);
}
