package com.arrupeteca.service;

import com.arrupeteca.dto.request.SalonRequest;
import com.arrupeteca.dto.response.SalonResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SalonService {

    List<SalonResponse> obtenerTodosActivos(String nombre);

    Page<SalonResponse> obtenerTodos(Long idPlanta, String nombre, Boolean borradoLogico, Pageable pageable);

    SalonResponse obtenerPorId(Long id);

    SalonResponse crear(SalonRequest request);

    SalonResponse actualizar(Long id, SalonRequest request);

    void cambiarEstado(Long id, boolean estado);
}
