package com.arrupeteca.service;

import com.arrupeteca.dto.request.PlantaRequest;
import com.arrupeteca.dto.response.detail.PlantaDetalleResponse;
import com.arrupeteca.dto.response.summary.PlantaResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlantaService {

    Page<PlantaResumen> obtenerTodosActivos(Pageable pageable);

    PlantaDetalleResponse obtenerPorIdActivo(Long id);

    Page<PlantaResumen> obtenerTodos(Pageable pageable);

    PlantaDetalleResponse obtenerPorId(Long id);

    Page<PlantaResumen> buscarPorEdificioId(Long idEdificio, Pageable pageable);

    PlantaDetalleResponse crear(PlantaRequest request);

    PlantaDetalleResponse actualizar(Long id, PlantaRequest request);

    void cambiarEstado(Long id, boolean estado);
}
