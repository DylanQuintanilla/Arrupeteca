package com.arrupeteca.service;

import com.arrupeteca.dto.request.SalonRequest;
import com.arrupeteca.dto.response.SalonResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SalonService {

    Page<SalonResponse> obtenerTodosActivos(Pageable pageable);

    SalonResponse obtenerPorIdActivo(Long id);

    Page<SalonResponse> obtenerTodos(Pageable pageable);

    SalonResponse obtenerPorId(Long id);

    Page<SalonResponse> buscarPorPlantaId(Long idPlanta, Pageable pageable);

    SalonResponse crear(SalonRequest request);

    SalonResponse actualizar(Long id, SalonRequest request);

    void cambiarEstado(Long id, boolean estado);
}
