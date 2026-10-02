package com.arrupeteca.transferencia.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.AssertTrue;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.openapitools.jackson.nullable.JsonNullable;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record DetalleSolicitudTransferenciaRequest(
    JsonNullable<Long> idLibro,
    JsonNullable<Integer> cantidad
) {
    public DetalleSolicitudTransferenciaRequest {}

    @AssertTrue(message = "La cantidad solicitada para transferencia no puede ser negativa ni cero (debe ser al menos 1)")
    public boolean isCantidadValida() {
        if (cantidad == null || !cantidad.isPresent() || cantidad.get() == null) {
            return true;
        }
        return cantidad.get() >= 1;
    }

    @AssertTrue(message = "El ID del libro debe ser un número positivo")
    public boolean isIdLibroValido() {
        if (idLibro == null || !idLibro.isPresent() || idLibro.get() == null) {
            return true;
        }
        return idLibro.get() > 0;
    }
}
