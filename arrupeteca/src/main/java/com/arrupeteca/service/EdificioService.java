package com.arrupeteca.service;

import com.arrupeteca.dto.request.EdificioRequest;
import com.arrupeteca.dto.response.EdificioResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EdificioService {

    List<EdificioResponse> obtenerTodosActivos(String nombre);

    Page<EdificioResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable);

    EdificioResponse obtenerPorId(Long id);

    EdificioResponse crear(EdificioRequest request);

    EdificioResponse actualizar(Long id, EdificioRequest request);

    void cambiarEstado(Long id, boolean estado);}
