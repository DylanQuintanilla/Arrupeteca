package com.arrupeteca.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalonRequest {

    @NotBlank(message = "El nombre del salon no puede estar vacío")
    @Size(max = 30, message = "El nombre no puede exceder 30 caracteres")
    private String nombre;

    @NotNull(message = "Se debe especificar la planta")
    private Long idPlanta;

}
