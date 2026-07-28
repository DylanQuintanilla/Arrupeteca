package com.arrupeteca.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record GradoRequest(
    @NotBlank(message = "El nombre del grado no puede estar vacio")
    @Size(max = 80, message = "El nombre del grado no puede exceder 80 caracteres")
    String nombre
) {
    public GradoRequest {}
}
