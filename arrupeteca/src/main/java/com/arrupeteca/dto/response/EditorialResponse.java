package com.arrupeteca.dto.response;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record EditorialResponse(
    Long id,
    String nombre,
    String direccion,
    String correo,
    String telefono,
    String personaContacto,
    boolean borradoLogico
) {}
