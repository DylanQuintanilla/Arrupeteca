package com.arrupeteca.service;

import com.arrupeteca.dto.request.PaisRequest;
import com.arrupeteca.dto.response.PaisResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaisService {

    List<PaisResponse> obtenerTodosActivos(String nombre);

    Page<PaisResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable);

    PaisResponse obtenerPorId(Long id);

    PaisResponse crear(PaisRequest request);

    PaisResponse actualizar(Long id, PaisRequest request);

    void cambiarEstado(Long id, boolean estado);}
