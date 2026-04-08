package com.arrupeteca.service;

import com.arrupeteca.dto.request.SalonRequest;
import com.arrupeteca.persistence.projection.SalonResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SalonService {

    Page<SalonResumen> obtenerTodosActivos(Pageable pageable);

    SalonResumen obtenerPorIdActivo(Long id);

    Page<SalonResumen> obtenerTodos(Pageable pageable);

    SalonResumen obtenerPorId(Long id);

    Page<SalonResumen> obtenerPorIdPlanta(Long idPlanta, Pageable pageable);

    SalonResumen crear(SalonRequest request);

    SalonResumen actualizar(Long id, SalonRequest request);

    void cambiarEstado(Long id, boolean estado);
}
