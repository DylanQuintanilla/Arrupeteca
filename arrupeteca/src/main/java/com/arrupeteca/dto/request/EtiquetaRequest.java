package com.arrupeteca.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record EtiquetaRequest(
        @NotBlank(message = "El nombre de la etiqueta es obligatorio")
        @Size(max = 50, message = "El nombre de la etiqueta no debe exceder los 50 caracteres")
        String nombre
) {}
