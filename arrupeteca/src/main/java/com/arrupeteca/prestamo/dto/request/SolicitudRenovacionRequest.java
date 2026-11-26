package com.arrupeteca.prestamo.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.AssertTrue;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDateTime;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record SolicitudRenovacionRequest(
    JsonNullable<Long> idEjemplarPrestamo,
    JsonNullable<Long> idUsuario,
    JsonNullable<Long> idEstadoSolicitud,
    JsonNullable<Integer> diasSolicitados,
    JsonNullable<LocalDateTime> nuevaFechaLimitePropuesta,
    JsonNullable<String> motivoSolicitud,
    JsonNullable<String> motivoRespuesta,
    JsonNullable<Long> revisadoPor,
    Long version
) {
    public SolicitudRenovacionRequest {}

    @AssertTrue(message = "El motivo de la solicitud no puede exceder 255 caracteres")
    public boolean isMotivoSolicitudValido() {
        if (motivoSolicitud == null || !motivoSolicitud.isPresent() || motivoSolicitud.get() == null) {
            return true;
        }
        return motivoSolicitud.get().length() <= 255;
    }

    @AssertTrue(message = "El motivo de la respuesta no puede exceder 255 caracteres")
    public boolean isMotivoRespuestaValido() {
        if (motivoRespuesta == null || !motivoRespuesta.isPresent() || motivoRespuesta.get() == null) {
            return true;
        }
        return motivoRespuesta.get().length() <= 255;
    }

    @AssertTrue(message = "Los días de prórroga no pueden ser negativos ni cero (deben ser al menos 1 día)")
    public boolean isDiasSolicitadosValidos() {
        if (diasSolicitados == null || !diasSolicitados.isPresent() || diasSolicitados.get() == null) {
            return true;
        }
        return diasSolicitados.get() >= 1;
    }

    @AssertTrue(message = "Los IDs relacionados deben ser números positivos")
    public boolean isIdsValidos() {
        return esPositivo(idEjemplarPrestamo)
            && esPositivo(idUsuario)
            && esPositivo(idEstadoSolicitud)
            && esPositivo(revisadoPor);
    }

    private boolean esPositivo(JsonNullable<Long> id) {
        if (id == null || !id.isPresent() || id.get() == null) {
            return true;
        }
        return id.get() > 0;
    }
}
