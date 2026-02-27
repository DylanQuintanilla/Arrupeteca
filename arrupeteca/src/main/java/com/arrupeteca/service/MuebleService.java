package com.arrupeteca.service;

import com.arrupeteca.dto.request.MuebleRequest;
import com.arrupeteca.persistence.projection.MuebleResumen;

import java.util.List;

public interface MuebleService {

    List<MuebleResumen> obtenerTodosActivos();

    MuebleResumen obtenerPorIdActivo(Long id);

    List<MuebleResumen> obtenerTodos();

    MuebleResumen obtenerPorId(Long id);

    MuebleResumen crear(MuebleRequest request);

    MuebleResumen actualizar(Long id, MuebleRequest request);

    void cambiarBorradoLogico(Long id, Boolean estado);

    List<MuebleResumen> obtenerPorIdSalon(Long idSalon);

}
