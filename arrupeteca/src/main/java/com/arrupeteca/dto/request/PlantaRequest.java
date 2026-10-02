package com.arrupeteca.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record PlantaRequest(
    @NotBlank(message = "El nombre de la planta no puede estar vacío")
    @Size(max = 30, message = "El nombre no puede exceder 30 caracteres")
    String nombre,

    @NotNull(message = "Se debe especificar el edificio")
    @jakarta.validation.constraints.Positive(message = "El ID del edificio debe ser un número positivo")
    Long idEdificio
) {}
