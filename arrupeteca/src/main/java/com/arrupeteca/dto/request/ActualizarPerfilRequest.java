package com.arrupeteca.dto.request;

import org.openapitools.jackson.nullable.JsonNullable;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record ActualizarPerfilRequest(
    JsonNullable<String> nombres,
    JsonNullable<String> apellidos,
    JsonNullable<String> carnet,
    JsonNullable<String> contrasenaActual,
    JsonNullable<String> nuevaContrasena
) { }
