package com.arrupeteca.service;

import com.arrupeteca.dto.request.NacionalidadRequest;
import com.arrupeteca.dto.response.NacionalidadResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NacionalidadService {

    Page<NacionalidadResponse> obtenerTodosActivos(Pageable pageable);

    NacionalidadResponse obtenerPorIdActivo(Long id);

    Page<NacionalidadResponse> obtenerTodos(Pageable pageable);

    NacionalidadResponse obtenerPorId(Long id);

    NacionalidadResponse crear(NacionalidadRequest request);

    NacionalidadResponse actualizar(Long id, NacionalidadRequest request);

    void cambiarEstado(Long id, boolean estado);
}
