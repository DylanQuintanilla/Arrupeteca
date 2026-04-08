package com.arrupeteca.service;

import com.arrupeteca.dto.request.PlantaRequest;
import com.arrupeteca.persistence.projection.PlantaResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlantaService {

    Page<PlantaResumen> obtenerTodosActivos(Pageable pageable);

    PlantaResumen obtenerPorIdActivo(Long id);

    Page<PlantaResumen> obtenerTodos(Pageable pageable);

    PlantaResumen obtenerPorId(Long id);

    Page<PlantaResumen> obtenerPorIdEdificio(Long idEdificio, Pageable pageable);

    PlantaResumen crear(PlantaRequest request);

    PlantaResumen actualizar(Long id, PlantaRequest request);

    void cambiarEstado(Long id, boolean estado);
}
