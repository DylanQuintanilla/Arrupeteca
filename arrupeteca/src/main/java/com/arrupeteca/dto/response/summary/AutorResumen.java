package com.arrupeteca.dto.response.summary;

import java.time.Year;
import lombok.Builder;

@Builder
public record AutorResumen(
    Long id,
    String nombre,
    String seudonimo,
    Year anioNacimiento,
    Year anioFallecimiento,
    boolean borradoLogico,
    String nombrePais,
    String nombreNacionalidad,
    String nombreGeneroPrincipal,
    String nombreObraCumbre,
    String biografiaResumida
) { }
