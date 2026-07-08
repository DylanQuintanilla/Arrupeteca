package com.arrupeteca.dto.response.summary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Year;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record AutorResumenResponse(
    @NotNull
    Long id,
    
    @NotBlank
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
