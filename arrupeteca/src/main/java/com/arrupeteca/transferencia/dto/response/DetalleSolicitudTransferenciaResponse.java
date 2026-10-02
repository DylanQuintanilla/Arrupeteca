package com.arrupeteca.transferencia.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record DetalleSolicitudTransferenciaResponse(
    @NotNull Long id,
    @NotNull Long idLibro,
    String tituloLibro,
    @NotNull Integer cantidad
) { }
