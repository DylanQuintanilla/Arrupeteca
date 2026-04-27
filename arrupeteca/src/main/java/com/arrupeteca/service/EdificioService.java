package com.arrupeteca.service;

import com.arrupeteca.dto.request.EdificioRequest;
import com.arrupeteca.dto.response.detail.EdificioDetalleResponse;
import com.arrupeteca.dto.response.summary.EdificioResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EdificioService {

    Page<EdificioResumen> obtenerTodosActivos(Pageable pageable);

    EdificioDetalleResponse obtenerPorIdActivo(Long id);

    Page<EdificioResumen> obtenerTodos(Pageable pageable);

    EdificioDetalleResponse obtenerPorId(Long id);

    EdificioDetalleResponse crear(EdificioRequest request);

    EdificioDetalleResponse actualizar(Long id, EdificioRequest request);

    void cambiarEstado(Long id, boolean estado);
}
