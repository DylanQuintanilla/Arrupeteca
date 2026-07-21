package com.arrupeteca.dto.response;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record IdiomaResponse(
    Long id,
    String nombre,
    boolean borradoLogico
) {}
