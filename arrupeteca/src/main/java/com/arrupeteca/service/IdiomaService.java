package com.arrupeteca.service;

import com.arrupeteca.dto.request.IdiomaRequest;
import com.arrupeteca.dto.response.IdiomaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IdiomaService {

    Page<IdiomaResponse> obtenerTodosActivos(Pageable pageable);

    IdiomaResponse obtenerPorIdActivo(Long id);

    Page<IdiomaResponse> obtenerTodos(Pageable pageable);

    IdiomaResponse obtenerPorId(Long id);

    IdiomaResponse crear(IdiomaRequest request);

    IdiomaResponse actualizar(Long id, IdiomaRequest request);

    void cambiarEstado(Long id, boolean estado);
}
