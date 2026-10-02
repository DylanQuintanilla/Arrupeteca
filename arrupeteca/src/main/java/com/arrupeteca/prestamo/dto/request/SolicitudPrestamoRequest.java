package com.arrupeteca.prestamo.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDateTime;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record SolicitudPrestamoRequest(
    JsonNullable<Long> idUsuario,
    JsonNullable<Long> idEstadoSolicitud,
    JsonNullable<LocalDateTime> fechaLimiteRetiro,
    @Size(max = 255, message = "El comentario de respuesta no puede exceder 255 caracteres")
    JsonNullable<String> comentarioRespuesta,
    JsonNullable<Boolean> creadoPorAdmin,
    Long version
) {
    public SolicitudPrestamoRequest {}

    @AssertTrue(message = "Los IDs del usuario y estado de solicitud deben ser números positivos")
    public boolean isIdsValidos() {
        return esPositivo(idUsuario) && esPositivo(idEstadoSolicitud);
    }

    private boolean esPositivo(JsonNullable<Long> id) {
        if (id == null || !id.isPresent() || id.get() == null) {
            return true;
        }
        return id.get() > 0;
    }
}
