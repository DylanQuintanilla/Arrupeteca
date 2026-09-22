package com.arrupeteca.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record FormatoRequest(
    @NotBlank(message = "El nombre del formato no puede ser vacio")
    @Size(max = 50, message = "El nombre del formato no debe exceder los 50 caracteres")
    String nombre
) {}
