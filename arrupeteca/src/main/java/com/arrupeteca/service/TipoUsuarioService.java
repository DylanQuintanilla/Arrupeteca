package com.arrupeteca.service;

import com.arrupeteca.dto.request.TipoUsuarioRequest;
import com.arrupeteca.dto.response.TipoUsuarioResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TipoUsuarioService {

    Page<TipoUsuarioResponse> obtenerTodosActivos(Pageable pageable);

    TipoUsuarioResponse obtenerPorIdActivo(Long id);

    Page<TipoUsuarioResponse> obtenerTodos(Pageable pageable);

    TipoUsuarioResponse obtenerPorId(Long id);

    TipoUsuarioResponse crear(TipoUsuarioRequest request);

    TipoUsuarioResponse actualizar(Long id, TipoUsuarioRequest request);

    void cambiarEstado(Long id, boolean estado);

}
