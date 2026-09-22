package com.arrupeteca.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record ParentescoRequest(
    @NotBlank(message = "El nombre del parentesco no puede estar vacío")
    @Size(max = 50, message = "El nombre del parentesco no puede exceder 50 caracteres")
    String nombre
) {
    public ParentescoRequest { }
}
