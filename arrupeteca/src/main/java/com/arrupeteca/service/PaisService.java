package com.arrupeteca.service;

import com.arrupeteca.dto.request.PaisRequest;
import com.arrupeteca.dto.response.PaisResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaisService {

    Page<PaisResponse> obtenerTodosActivos(Pageable pageable);

    PaisResponse obtenerPorIdActivo(Long id);

    Page<PaisResponse> obtenerTodos(Pageable pageable);

    PaisResponse obtenerPorId(Long id);

    PaisResponse crear(PaisRequest request);

    PaisResponse actualizar(Long id, PaisRequest request);

    void cambiarEstado(Long id, boolean estado);
}
