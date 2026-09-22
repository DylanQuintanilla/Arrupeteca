package com.arrupeteca.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record TipoAutoriaRequest(
    @NotBlank(message = "El nombre del tipo de autoría no puede estar vacío")
    @Size(max = 25, message = "El nombre no puede exceder 25 caracteres")
    String nombre
) {}
