package com.arrupeteca.service;

import com.arrupeteca.dto.request.TipoAutoriaRequest;
import com.arrupeteca.persistence.projection.TipoAutoriaResumen;

import java.util.List;

public interface TipoAutoriaService {

    List<TipoAutoriaResumen> obtenerTodosActivos();

    TipoAutoriaResumen obtenerPorIdActivo(Long id);

    List<TipoAutoriaResumen> obtenerTodos();

    TipoAutoriaResumen obtenerPorId(Long id);

    TipoAutoriaResumen crear(TipoAutoriaRequest request);

    TipoAutoriaResumen actualizar(Long id, TipoAutoriaRequest request);

    void cambiarEstado(Long id, boolean estado);

}