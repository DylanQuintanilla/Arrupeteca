package com.arrupeteca.transferencia.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record SolicitudTransferenciaRequest(
    JsonNullable<Long> idUsuario,
    JsonNullable<Long> idSalonDestino,
    JsonNullable<Long> idEstadoSolicitudTransferencia,
    @Size(max = 255, message = "El motivo no puede exceder 255 caracteres")
    JsonNullable<String> motivo,
    @Size(max = 255, message = "El motivo de rechazo no puede exceder 255 caracteres")
    JsonNullable<String> motivoRechazo,
    JsonNullable<List<DetalleSolicitudTransferenciaRequest>> detalles,
    Long version
) {
    public SolicitudTransferenciaRequest {}

    @AssertTrue(message = "Los IDs del usuario, salón destino y estado de solicitud deben ser números positivos")
    public boolean isIdsValidos() {
        return esPositivo(idUsuario)
            && esPositivo(idSalonDestino)
            && esPositivo(idEstadoSolicitudTransferencia);
    }

    private boolean esPositivo(JsonNullable<Long> id) {
        if (id == null || !id.isPresent() || id.get() == null) {
            return true;
        }
        return id.get() > 0;
    }
}
