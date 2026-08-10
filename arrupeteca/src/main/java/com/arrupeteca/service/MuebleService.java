package com.arrupeteca.service;

import com.arrupeteca.dto.request.MuebleRequest;
import com.arrupeteca.dto.response.MuebleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MuebleService {

    List<MuebleResponse> obtenerTodosActivos(String nombre);

    Page<MuebleResponse> obtenerTodos(Long idSalon, String nombre, Boolean borradoLogico, Pageable pageable);

    MuebleResponse obtenerPorId(Long id);

    MuebleResponse crear(MuebleRequest request);

    MuebleResponse actualizar(Long id, MuebleRequest request);

    void cambiarEstado(Long id, boolean estado);
}
