package com.arrupeteca.dto.response;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record PlantaResponse(
    Long id,
    String nombre,
    boolean borradoLogico,
    EdificioResponse edificio
) {}
