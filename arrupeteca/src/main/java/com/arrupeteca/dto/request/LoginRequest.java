package com.arrupeteca.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record LoginRequest(
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El formato del correo es inválido")
    String correo,

    @NotBlank(message = "La contraseña es obligatoria")
    String contrasena
) {
    public LoginRequest { }
}
