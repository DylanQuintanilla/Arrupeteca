package com.arrupeteca.service;

import com.arrupeteca.dto.request.PlantaRequest;
import com.arrupeteca.persistence.entity.Planta;
import com.arrupeteca.persistence.enums.Disponibilidad;
import com.arrupeteca.persistence.projection.PlantaResumen;
import com.arrupeteca.persistence.projection.SalonResumen;

import java.util.List;

public interface PlantaService {

    List<PlantaResumen> obtenerTodosActivos();

    PlantaResumen obtenerPorIdActivo(Long id);

    List<PlantaResumen> obtenerTodos();

    PlantaResumen obtenerPorId(Long id);

    PlantaResumen crear(PlantaRequest request);

    PlantaResumen actualizar(Long id, PlantaRequest request);

    void cambiarBorradoLogico(Long id, Boolean estado);

    List<PlantaResumen> obtenerPorIdEdificio(Long idEdificio);

}
