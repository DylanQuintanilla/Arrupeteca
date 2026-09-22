package com.arrupeteca.dto.response.detail;

import com.arrupeteca.dto.response.TipoUsuarioResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record UsuarioDetalleResponse(
    @NotNull
    Long id,

    @NotBlank
    String nombres,

    @NotBlank
    String apellidos,

    @NotBlank
    String correo,

    String carnet,
    boolean bloqueadoPorMora,
    LocalDateTime fechaRegistro,
    LocalDateTime ultimaFechaActividad,
    boolean borradoLogico,

    @NotNull
    TipoUsuarioResponse tipoUsuario
) { }
