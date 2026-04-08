package com.arrupeteca.service;

import com.arrupeteca.dto.request.IdiomaRequest;
import com.arrupeteca.persistence.projection.IdiomaResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IdiomaService {

    Page<IdiomaResumen> obtenerTodosActivos(Pageable pageable);

    IdiomaResumen obtenerPorIdActivo(Long id);

    Page<IdiomaResumen> obtenerTodos(Pageable pageable);

    IdiomaResumen obtenerPorId(Long id);

    IdiomaResumen crear(IdiomaRequest request);

    IdiomaResumen actualizar(Long id, IdiomaRequest request);

    void cambiarEstado(Long id, boolean estado);
}
