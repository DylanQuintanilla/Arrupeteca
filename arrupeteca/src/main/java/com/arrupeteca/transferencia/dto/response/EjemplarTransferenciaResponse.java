package com.arrupeteca.transferencia.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record EjemplarTransferenciaResponse(
    @NotNull Long id,
    @NotNull Long idEjemplarLibro,
    String tituloObra,
    @NotNull Long idEstadoFisicoAlMomento,
    String estadoFisicoAlMomento,
    boolean recibido
) { }
