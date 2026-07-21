package com.arrupeteca.service;

import com.arrupeteca.dto.request.MuebleRequest;
import com.arrupeteca.dto.response.MuebleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MuebleService {

    Page<MuebleResponse> obtenerTodosActivos(Pageable pageable);

    MuebleResponse obtenerPorIdActivo(Long id);

    Page<MuebleResponse> busquedaGlobal(String palabra, Pageable pageable);

    Page<MuebleResponse> obtenerTodos(Pageable pageable);

    MuebleResponse obtenerPorId(Long id);

    Page<MuebleResponse> busquedaMaestraAdmin(Boolean esBorrado, Long idSalon, String termino, Pageable pageable);

    MuebleResponse crear(MuebleRequest request);

    MuebleResponse actualizar(Long id, MuebleRequest request);

    void cambiarEstado(Long id, boolean estado);
}
