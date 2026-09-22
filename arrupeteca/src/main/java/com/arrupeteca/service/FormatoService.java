package com.arrupeteca.service;

import com.arrupeteca.dto.request.FormatoRequest;
import com.arrupeteca.dto.response.FormatoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FormatoService {

    List<FormatoResponse> obtenerTodosActivos(String nombre);

    Page<FormatoResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable);

    FormatoResponse obtenerPorId(Long id);

    FormatoResponse crear(FormatoRequest request);

    FormatoResponse actualizar(Long id, FormatoRequest request);

    void cambiarEstado(Long id, boolean estado);}
