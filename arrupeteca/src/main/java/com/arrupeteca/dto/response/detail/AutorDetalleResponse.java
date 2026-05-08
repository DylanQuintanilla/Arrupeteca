package com.arrupeteca.dto.response.detail;

import com.arrupeteca.dto.response.summary.GeneroResumen;
import com.arrupeteca.dto.response.summary.NacionalidadResumen;
import com.arrupeteca.dto.response.summary.ObraResumen;
import com.arrupeteca.dto.response.summary.PaisResumen;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.Year;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record AutorDetalleResponse(
    @NotNull
    Long id,
    
    @NotBlank
    String nombre,
    
    String seudonimo,
    Year anioNacimiento,
    Year anioFallecimiento,
    
    @NotBlank
    String biografia,
    
    Long version,
    
    boolean borradoLogico,
    
    @NotBlank
    String creadoPor,
    
    @NotNull
    LocalDateTime fechaCreacion,
    
    String actualizadoPor,
    LocalDateTime fechaActualizacion,
    
    PaisResumen paisNacimiento,
    NacionalidadResumen nacionalidad,
    GeneroResumen generoPrincipal,
    ObraResumen obraCumbre
) { }
