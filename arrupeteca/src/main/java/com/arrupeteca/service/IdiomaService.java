package com.arrupeteca.service;

import com.arrupeteca.dto.request.IdiomaRequest;
import com.arrupeteca.dto.response.detail.IdiomaDetalleResponse;
import com.arrupeteca.dto.response.summary.IdiomaResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IdiomaService {

    Page<IdiomaResumen> obtenerTodosActivos(Pageable pageable);

    IdiomaDetalleResponse obtenerPorIdActivo(Long id);

    Page<IdiomaResumen> obtenerTodos(Pageable pageable);

    IdiomaDetalleResponse obtenerPorId(Long id);

    IdiomaDetalleResponse crear(IdiomaRequest request);

    IdiomaDetalleResponse actualizar(Long id, IdiomaRequest request);

    void cambiarEstado(Long id, boolean estado);
}
