package com.arrupeteca.service;

import com.arrupeteca.dto.request.NacionalidadRequest;
import com.arrupeteca.persistence.projection.NacionalidadResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NacionalidadService {

    Page<NacionalidadResumen> obtenerTodasActivas(Pageable pageable);

    NacionalidadResumen obtenerPorIdActiva(Long id);

    Page<NacionalidadResumen> obtenerTodas(Pageable pageable);

    NacionalidadResumen obtenerPorId(Long id);

    NacionalidadResumen crear(NacionalidadRequest request);

    NacionalidadResumen actualizar(Long id, NacionalidadRequest request);

    void cambiarEstado(Long id, boolean estado);
}
