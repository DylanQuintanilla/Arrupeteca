package com.arrupeteca.service;

import com.arrupeteca.dto.request.NacionalidadRequest;
import com.arrupeteca.dto.response.summary.NacionalidadResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NacionalidadService {

    Page<NacionalidadResumen> obtenerTodosActivos(Pageable pageable);

    NacionalidadResumen obtenerPorIdActivo(Long id);

    Page<NacionalidadResumen> obtenerTodos(Pageable pageable);

    NacionalidadResumen obtenerPorId(Long id);

    NacionalidadResumen crear(NacionalidadRequest request);

    NacionalidadResumen actualizar(Long id, NacionalidadRequest request);

    void cambiarEstado(Long id, boolean estado);
}
