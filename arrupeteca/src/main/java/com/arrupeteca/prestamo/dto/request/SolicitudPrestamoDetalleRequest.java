package com.arrupeteca.prestamo.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.AssertTrue;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.openapitools.jackson.nullable.JsonNullable;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record SolicitudPrestamoDetalleRequest(
    JsonNullable<Long> idSolicitud,
    JsonNullable<Long> idLibro,
    JsonNullable<Integer> cantidadSolicitada,
    Long version
) {
    public SolicitudPrestamoDetalleRequest {}

    @AssertTrue(message = "La cantidad solicitada no puede ser negativa ni cero (debe ser al menos 1)")
    public boolean isCantidadSolicitadaValida() {
        if (cantidadSolicitada == null || !cantidadSolicitada.isPresent() || cantidadSolicitada.get() == null) {
            return true;
        }
        return cantidadSolicitada.get() >= 1;
    }

    @AssertTrue(message = "Los IDs de la solicitud y el libro deben ser números positivos")
    public boolean isIdsValidos() {
        return esPositivo(idSolicitud) && esPositivo(idLibro);
    }

    private boolean esPositivo(JsonNullable<Long> id) {
        if (id == null || !id.isPresent() || id.get() == null) {
            return true;
        }
        return id.get() > 0;
    }
}
