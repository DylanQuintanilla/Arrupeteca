package com.arrupeteca.service;

import com.arrupeteca.dto.request.TipoUsuarioRequest;
import com.arrupeteca.dto.response.TipoUsuarioResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TipoUsuarioService {

    List<TipoUsuarioResponse> obtenerTodosActivos(String nombre);

    Page<TipoUsuarioResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable);

    TipoUsuarioResponse obtenerPorId(Long id);

    TipoUsuarioResponse crear(TipoUsuarioRequest request);

    TipoUsuarioResponse actualizar(Long id, TipoUsuarioRequest request);

    void cambiarEstado(Long id, boolean estado);}
