package com.arrupeteca.service;

import com.arrupeteca.dto.request.PlantaRequest;
import com.arrupeteca.dto.response.PlantaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlantaService {

    Page<PlantaResponse> obtenerTodosActivos(Pageable pageable);

    PlantaResponse obtenerPorIdActivo(Long id);

    Page<PlantaResponse> obtenerTodos(Pageable pageable);

    PlantaResponse obtenerPorId(Long id);

    Page<PlantaResponse> buscarPorEdificioId(Long idEdificio, Pageable pageable);

    PlantaResponse crear(PlantaRequest request);

    PlantaResponse actualizar(Long id, PlantaRequest request);

    void cambiarEstado(Long id, boolean estado);
}
