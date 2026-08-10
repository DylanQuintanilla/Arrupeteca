package com.arrupeteca.service;

import com.arrupeteca.dto.request.NacionalidadRequest;
import com.arrupeteca.dto.response.NacionalidadResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NacionalidadService {

    List<NacionalidadResponse> obtenerTodosActivos(String nombre);

    Page<NacionalidadResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable);

    NacionalidadResponse obtenerPorId(Long id);

    NacionalidadResponse crear(NacionalidadRequest request);

    NacionalidadResponse actualizar(Long id, NacionalidadRequest request);

    void cambiarEstado(Long id, boolean estado);}
