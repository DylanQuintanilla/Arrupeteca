package com.arrupeteca.service;

import com.arrupeteca.dto.request.CicloRequest;
import com.arrupeteca.dto.response.CicloResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CicloService {

    Page<CicloResponse> obtenerTodosActivos(Pageable pageable);

    CicloResponse obtenerPorIdActivo(Long id);

    Page<CicloResponse> obtenerTodos(Pageable pageable);

    CicloResponse obtenerPorId(Long id);

    CicloResponse crear(CicloRequest request);

    CicloResponse actualizar(Long id, CicloRequest request);

    void cambiarEstado(Long id, boolean estado);
}
