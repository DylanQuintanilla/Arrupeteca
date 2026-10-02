package com.arrupeteca.transferencia.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record EstadoSolicitudTransferenciaRequest(
    @NotBlank(message = "El nombre del estado de solicitud de transferencia no puede estar vacio")
    @Size(max = 50, message = "El nombre del estado de solicitud de transferencia no puede exceder 50 caracteres")
    String nombre
) {
    public EstadoSolicitudTransferenciaRequest {}
}
