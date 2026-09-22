package com.arrupeteca.service;

import com.arrupeteca.dto.request.TipoAutoriaRequest;
import com.arrupeteca.dto.response.TipoAutoriaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TipoAutoriaService {

    List<TipoAutoriaResponse> obtenerTodosActivos(String nombre);

    Page<TipoAutoriaResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable);

    TipoAutoriaResponse obtenerPorId(Long id);

    TipoAutoriaResponse crear(TipoAutoriaRequest request);

    TipoAutoriaResponse actualizar(Long id, TipoAutoriaRequest request);

    void cambiarEstado(Long id, boolean estado);}
