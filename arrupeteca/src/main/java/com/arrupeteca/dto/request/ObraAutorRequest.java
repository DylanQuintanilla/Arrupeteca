package com.arrupeteca.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record ObraAutorRequest(
    @NotNull(message = "El ID del autor es obligatorio")
    Long idAutor,

    @NotNull(message = "El ID del tipo de autoría es obligatorio")
    Long idTipoAutoria
) {}
