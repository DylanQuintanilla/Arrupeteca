package com.arrupeteca.service;

import com.arrupeteca.dto.request.MuebleRequest;
import com.arrupeteca.dto.response.detail.MuebleDetalleResponse;
import com.arrupeteca.dto.response.summary.MuebleResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MuebleService {

    Page<MuebleResumen> obtenerTodosActivos(Pageable pageable);

    MuebleDetalleResponse obtenerPorIdActivo(Long id);

    Page<MuebleResumen> busquedaGlobal(String palabra, Pageable pageable);

    Page<MuebleResumen> obtenerTodos(Pageable pageable);

    MuebleDetalleResponse obtenerPorId(Long id);

    Page<MuebleResumen> busquedaMaestraAdmin(Boolean esBorrado, Long idSalon, String termino, Pageable pageable);

    MuebleDetalleResponse crear(MuebleRequest request);

    MuebleDetalleResponse actualizar(Long id, MuebleRequest request);

    void cambiarEstado(Long id, boolean estado);
}
