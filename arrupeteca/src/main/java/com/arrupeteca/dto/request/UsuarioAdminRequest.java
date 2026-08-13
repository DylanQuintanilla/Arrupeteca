package com.arrupeteca.dto.request;

import org.openapitools.jackson.nullable.JsonNullable;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record UsuarioAdminRequest(
    JsonNullable<String> nombres,
    JsonNullable<String> apellidos,
    JsonNullable<String> correo,
    JsonNullable<String> contrasena,
    JsonNullable<String> carnet,
    JsonNullable<Long> idTipoUsuario,
    JsonNullable<Boolean> bloqueadoPorMora
) { }
