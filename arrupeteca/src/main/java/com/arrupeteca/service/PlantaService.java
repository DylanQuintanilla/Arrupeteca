package com.arrupeteca.service;

import com.arrupeteca.dto.request.PlantaRequest;
import com.arrupeteca.dto.response.PlantaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlantaService {

    List<PlantaResponse> obtenerTodosActivos(String nombre);

    Page<PlantaResponse> obtenerTodos(Long idEdificio, String nombre, Boolean borradoLogico, Pageable pageable);

    PlantaResponse obtenerPorId(Long id);

    PlantaResponse crear(PlantaRequest request);

    PlantaResponse actualizar(Long id, PlantaRequest request);

    void cambiarEstado(Long id, boolean estado);
}
