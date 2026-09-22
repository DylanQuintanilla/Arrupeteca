package com.arrupeteca.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record ParentescoResponse(
    @NotNull Long id,
    @NotBlank String nombre,
    boolean borradoLogico
) { }
