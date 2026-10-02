package com.arrupeteca.transferencia.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.AssertTrue;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.openapitools.jackson.nullable.JsonNullable;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record EjemplarTransferenciaRequest(
    JsonNullable<Long> idEjemplarLibro,
    JsonNullable<Long> idEstadoFisicoAlMomento,
    JsonNullable<Boolean> recibido
) {
    public EjemplarTransferenciaRequest {}

    @AssertTrue(message = "Los IDs del ejemplar y estado físico deben ser números positivos")
    public boolean isIdsValidos() {
        return esPositivo(idEjemplarLibro) && esPositivo(idEstadoFisicoAlMomento);
    }

    private boolean esPositivo(JsonNullable<Long> id) {
        if (id == null || !id.isPresent() || id.get() == null) {
            return true;
        }
        return id.get() > 0;
    }
}
