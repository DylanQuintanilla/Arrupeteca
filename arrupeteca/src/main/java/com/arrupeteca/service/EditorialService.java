package com.arrupeteca.service;

import com.arrupeteca.dto.request.EditorialRequest;
import com.arrupeteca.dto.response.EditorialResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EditorialService {

    List<EditorialResponse> obtenerTodosActivos(String nombre);

    Page<EditorialResponse> obtenerTodos(String nombre, String correo, String personaContacto, Boolean borradoLogico, Pageable pageable);

    EditorialResponse obtenerPorId(Long id);

    EditorialResponse crear(EditorialRequest request);

    EditorialResponse actualizar(Long id, EditorialRequest request);

    void cambiarEstado(Long id, boolean estado);
}
