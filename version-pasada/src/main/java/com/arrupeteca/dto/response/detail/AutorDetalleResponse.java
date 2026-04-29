package com.arrupeteca.dto.response.detail;

import com.arrupeteca.dto.response.summary.GeneroResumen;
import com.arrupeteca.dto.response.summary.NacionalidadResumen;
import com.arrupeteca.dto.response.summary.ObraResumen;
import com.arrupeteca.dto.response.summary.PaisResumen;
import java.time.LocalDateTime;

public record AutorDetalleResponse(
    Long id,
    String nombre,
    String seudonimo,
    Integer anioNacimiento,
    Integer anioFallecimiento,
    String biografia,
    boolean borradoLogico,
    String creadoPor,
    LocalDateTime fechaCreacion,
    String modificadoPor,
    LocalDateTime fechaModificacion,
    PaisResumen paisNacimiento,
    NacionalidadResumen nacionalidad,
    GeneroResumen generoPrincipal,
    ObraResumen obraCumbre
) { }
