package com.arrupeteca.service;

import com.arrupeteca.dto.request.EtiquetaRequest;
import com.arrupeteca.dto.response.EtiquetaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EtiquetaService {

    List<EtiquetaResponse> obtenerTodosActivos(String nombre);

    Page<EtiquetaResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable);

    EtiquetaResponse obtenerPorId(Long id);

    EtiquetaResponse crear(EtiquetaRequest request);

    EtiquetaResponse actualizar(Long id, EtiquetaRequest request);

    void cambiarEstado(Long id, boolean estado);}
