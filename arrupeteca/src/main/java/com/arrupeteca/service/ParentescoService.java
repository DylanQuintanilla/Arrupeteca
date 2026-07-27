package com.arrupeteca.service;

import com.arrupeteca.dto.request.ParentescoRequest;
import com.arrupeteca.dto.response.ParentescoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ParentescoService {

    Page<ParentescoResponse> obtenerTodosActivos(Pageable pageable);

    ParentescoResponse obtenerPorIdActivo(Long id);

    Page<ParentescoResponse> obtenerTodos(Pageable pageable);

    ParentescoResponse obtenerPorId(Long id);

    ParentescoResponse crear(ParentescoRequest request);

    ParentescoResponse actualizar(Long id, ParentescoRequest request);

    void cambiarEstado(Long id, boolean estado);

}
