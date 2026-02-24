package com.arrupeteca.service;

import com.arrupeteca.dto.request.NacionalidadRequest;
import com.arrupeteca.persistence.projection.NacionalidadResumen;

import java.util.List;

public interface NacionalidadService {

    List<NacionalidadResumen> obtenerTodasActivas();

    NacionalidadResumen obtenerPorIdActiva(Long id);

    List<NacionalidadResumen> obtenerTodas();

    NacionalidadResumen obtenerPorId(Long id);

    NacionalidadResumen crearNacionalidad(NacionalidadRequest request);

    NacionalidadResumen actualizarNacionalidad(Long id, NacionalidadRequest request);

    void cambiarEstadoLogico(Long id, boolean activar);

}
