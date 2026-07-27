package com.arrupeteca.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record TipoUsuarioRequest(
    @NotBlank(message = "El nombre del tipo de usuario no puede estar vacío")
    @Size(max = 50, message = "El nombre del tipo de usuario no puede exceder 50 caracteres")
    String nombre
) {
    public TipoUsuarioRequest { }
}
