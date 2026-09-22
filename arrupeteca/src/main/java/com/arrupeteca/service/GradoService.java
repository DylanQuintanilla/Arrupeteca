package com.arrupeteca.service;

import com.arrupeteca.dto.request.GradoRequest;
import com.arrupeteca.dto.response.GradoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GradoService {

    List<GradoResponse> obtenerTodosActivos(String nombre);

    Page<GradoResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable);

    GradoResponse obtenerPorId(Long id);

    GradoResponse crear(GradoRequest request);

    GradoResponse actualizar(Long id, GradoRequest request);

    void cambiarEstado(Long id, boolean estado);}
