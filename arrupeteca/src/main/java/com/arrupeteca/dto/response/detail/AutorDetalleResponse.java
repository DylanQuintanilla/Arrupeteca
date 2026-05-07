package com.arrupeteca.dto.response.detail;

import com.arrupeteca.dto.response.summary.GeneroResumen;
import com.arrupeteca.dto.response.summary.NacionalidadResumen;
import com.arrupeteca.dto.response.summary.ObraResumen;
import com.arrupeteca.dto.response.summary.PaisResumen;
import java.time.LocalDateTime;
import java.time.Year;

public record AutorDetalleResponse(
    Long id,
    String nombre,
    String seudonimo,
    Year anioNacimiento,
    Year anioFallecimiento,
    String biografia,
    boolean borradoLogico,
    String creadoPor,
    LocalDateTime fechaCreacion,
    String actualizadoPor,
    LocalDateTime fechaActualizacion,
    PaisResumen paisNacimiento,
    NacionalidadResumen nacionalidad,
    GeneroResumen generoPrincipal,
    ObraResumen obraCumbre
) { }
