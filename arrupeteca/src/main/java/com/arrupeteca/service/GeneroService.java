package com.arrupeteca.service;

import com.arrupeteca.dto.request.GeneroRequest;
import com.arrupeteca.dto.response.GeneroResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GeneroService {

    List<GeneroResponse> obtenerTodosActivos(String nombre);

    Page<GeneroResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable);

    GeneroResponse obtenerPorId(Long id);

    GeneroResponse crear(GeneroRequest request);

    GeneroResponse actualizar(Long id, GeneroRequest request);

    void cambiarEstado(Long id, boolean estado);}
