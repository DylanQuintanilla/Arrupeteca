package com.arrupeteca.service;

import com.arrupeteca.dto.request.SalonRequest;
import com.arrupeteca.dto.response.detail.SalonDetalleResponse;
import com.arrupeteca.dto.response.summary.SalonResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SalonService {

    Page<SalonResumen> obtenerTodosActivos(Pageable pageable);

    SalonDetalleResponse obtenerPorIdActivo(Long id);

    Page<SalonResumen> obtenerTodos(Pageable pageable);

    SalonDetalleResponse obtenerPorId(Long id);

    Page<SalonResumen> buscarPorPlantaId(Long idPlanta, Pageable pageable);

    SalonDetalleResponse crear(SalonRequest request);

    SalonDetalleResponse actualizar(Long id, SalonRequest request);

    void cambiarEstado(Long id, boolean estado);
}
