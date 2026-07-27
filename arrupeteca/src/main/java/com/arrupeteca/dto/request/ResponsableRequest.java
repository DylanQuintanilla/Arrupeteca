package com.arrupeteca.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record ResponsableRequest(
    @NotNull(message = "El ID del usuario es obligatorio")
    Long idUsuario,

    @NotBlank(message = "Los nombres del responsable no pueden estar vacíos")
    @Size(max = 100, message = "Los nombres no pueden exceder 100 caracteres")
    String nombres,

    @NotBlank(message = "Los apellidos del responsable no pueden estar vacíos")
    @Size(max = 100, message = "Los apellidos no pueden exceder 100 caracteres")
    String apellidos,

    @NotBlank(message = "El correo no puede estar vacío")
    @Email(message = "El formato del correo es inválido")
    @Size(max = 150, message = "El correo no puede exceder 150 caracteres")
    String correo,

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    String telefono,

    @Size(max = 20, message = "El DUI no puede exceder 20 caracteres")
    String dui,

    @NotNull(message = "El ID del parentesco es obligatorio")
    Long idParentesco
) {
    public ResponsableRequest { }
}
