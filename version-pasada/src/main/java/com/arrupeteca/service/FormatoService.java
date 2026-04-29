package com.arrupeteca.service;

import com.arrupeteca.dto.request.FormatoRequest;
import com.arrupeteca.dto.response.detail.FormatoDetalleResponse;
import com.arrupeteca.dto.response.summary.FormatoResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FormatoService {

    Page<FormatoResumen> obtenerTodosActivos(Pageable pageable);

    FormatoDetalleResponse obtenerPorIdActivo(Long id);

    Page<FormatoResumen> obtenerTodos(Pageable pageable);

    FormatoDetalleResponse obtenerPorId(Long id);

    FormatoDetalleResponse crear(FormatoRequest request);

    FormatoDetalleResponse actualizar(Long id, FormatoRequest request);

    void cambiarEstado(Long id, boolean estado);
}
