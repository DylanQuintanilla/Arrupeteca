package com.arrupeteca.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record ResponsableResponse(
    @NotNull Long id,
    @NotNull Long idUsuario,
    String nombreUsuario,
    String nombres,
    String apellidos,
    String correo,
    String telefono,
    String dui,
    @NotNull Long idParentesco,
    String nombreParentesco,
    boolean borradoLogico
) { }
