package com.arrupeteca.service;

import com.arrupeteca.dto.request.SalonRequest;
import com.arrupeteca.persistence.projection.SalonResumen;

import java.util.List;

public interface SalonService {

    List<SalonResumen> obtenerTodosActivos();

    SalonResumen obtenerPorIdActivo(Long id);

    List<SalonResumen> obtenerTodos();

    SalonResumen obtenerPorId(Long id);

    SalonResumen crear(SalonRequest request);

    SalonResumen actualizar(Long id, SalonRequest request);

    void cambiarBorradoLogico(Long id, Boolean estado);

    List<SalonResumen> obtenerPorIdPlanta(Long idPlanta);

}
