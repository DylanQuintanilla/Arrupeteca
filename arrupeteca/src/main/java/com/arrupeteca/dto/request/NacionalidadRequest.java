package com.arrupeteca.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record NacionalidadRequest(
    @NotBlank(message = "El nombre no puede estar vacio")
    @Size(max = 50, message = "El nombreno puede exceder los 50 caracteres")
    String nombre
) {}
