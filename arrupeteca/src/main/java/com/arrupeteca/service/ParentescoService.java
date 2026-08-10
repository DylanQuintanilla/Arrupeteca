package com.arrupeteca.service;

import com.arrupeteca.dto.request.ParentescoRequest;
import com.arrupeteca.dto.response.ParentescoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ParentescoService {

    List<ParentescoResponse> obtenerTodosActivos(String nombre);

    Page<ParentescoResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable);

    ParentescoResponse obtenerPorId(Long id);

    ParentescoResponse crear(ParentescoRequest request);

    ParentescoResponse actualizar(Long id, ParentescoRequest request);

    void cambiarEstado(Long id, boolean estado);}
