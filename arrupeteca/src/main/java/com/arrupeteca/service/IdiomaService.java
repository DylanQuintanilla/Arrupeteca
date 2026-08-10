package com.arrupeteca.service;

import com.arrupeteca.dto.request.IdiomaRequest;
import com.arrupeteca.dto.response.IdiomaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IdiomaService {

    List<IdiomaResponse> obtenerTodosActivos(String nombre);

    Page<IdiomaResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable);

    IdiomaResponse obtenerPorId(Long id);

    IdiomaResponse crear(IdiomaRequest request);

    IdiomaResponse actualizar(Long id, IdiomaRequest request);

    void cambiarEstado(Long id, boolean estado);}
