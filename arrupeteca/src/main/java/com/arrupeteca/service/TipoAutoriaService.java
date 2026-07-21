package com.arrupeteca.service;

import com.arrupeteca.dto.request.TipoAutoriaRequest;
import com.arrupeteca.dto.response.TipoAutoriaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TipoAutoriaService {

    Page<TipoAutoriaResponse> obtenerTodosActivos(Pageable pageable);

    TipoAutoriaResponse obtenerPorIdActivo(Long id);

    Page<TipoAutoriaResponse> obtenerTodos(Pageable pageable);

    TipoAutoriaResponse obtenerPorId(Long id);

    TipoAutoriaResponse crear(TipoAutoriaRequest request);

    TipoAutoriaResponse actualizar(Long id, TipoAutoriaRequest request);

    void cambiarEstado(Long id, boolean estado);
}
