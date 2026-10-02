package com.arrupeteca.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.AssertTrue;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.openapitools.jackson.nullable.JsonNullable;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record EjemplarLibroRequest(
    JsonNullable<String> comentario,
    JsonNullable<Long> idEstadoFisico,
    JsonNullable<Long> idDisponibilidad,
    JsonNullable<Long> idLibro,
    JsonNullable<Long> idMueble,
    Long version
) {
    public EjemplarLibroRequest {}

    @AssertTrue(message = "El comentario no puede exceder los 255 caracteres")
    public boolean isComentarioValido() {
        if (comentario == null || !comentario.isPresent() || comentario.get() == null) {
            return true;
        }
        return comentario.get().length() <= 255;
    }

    @AssertTrue(message = "Los IDs relacionados al ejemplar deben ser números positivos")
    public boolean isIdsValidos() {
        return esPositivo(idEstadoFisico)
            && esPositivo(idDisponibilidad)
            && esPositivo(idLibro)
            && esPositivo(idMueble);
    }

    private boolean esPositivo(JsonNullable<Long> id) {
        if (id == null || !id.isPresent() || id.get() == null) {
            return true;
        }
        return id.get() > 0;
    }
}
