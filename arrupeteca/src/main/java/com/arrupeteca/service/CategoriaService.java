package com.arrupeteca.service;

import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.dto.response.CategoriaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoriaService {

    List<CategoriaResponse> obtenerTodosActivos(String nombre);

    Page<CategoriaResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable);

    CategoriaResponse obtenerPorId(Long id);

    CategoriaResponse crear(CategoriaRequest request);

    CategoriaResponse actualizar(Long id, CategoriaRequest request);

    void cambiarEstado(Long id, boolean estado);
}
