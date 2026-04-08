package com.arrupeteca.service;

import com.arrupeteca.dto.request.TipoAutoriaRequest;
import com.arrupeteca.persistence.projection.TipoAutoriaResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TipoAutoriaService {

    Page<TipoAutoriaResumen> obtenerTodosActivos(Pageable pageable);

    TipoAutoriaResumen obtenerPorIdActivo(Long id);

    Page<TipoAutoriaResumen> obtenerTodos(Pageable pageable);

    TipoAutoriaResumen obtenerPorId(Long id);

    TipoAutoriaResumen crear(TipoAutoriaRequest request);

    TipoAutoriaResumen actualizar(Long id, TipoAutoriaRequest request);

    void cambiarEstado(Long id, boolean estado);
}
