package com.arrupeteca.transferencia.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record TransferenciaRequest(
    JsonNullable<Long> idSolicitudTransferencia,
    JsonNullable<Long> idUsuarioEnvia,
    JsonNullable<Long> idUsuarioRecibe,
    JsonNullable<Long> idMuebleOrigen,
    JsonNullable<Long> idMuebleDestino,
    JsonNullable<Long> idEstadoTransferencia,
    JsonNullable<LocalDateTime> fechaEnvio,
    JsonNullable<LocalDateTime> fechaRecepcion,
    @Size(max = 255, message = "La observación de recepción no puede exceder 255 caracteres")
    JsonNullable<String> observacionRecepcion,
    JsonNullable<Boolean> confirmada,
    @Size(max = 255, message = "El motivo no puede exceder 255 caracteres")
    JsonNullable<String> motivo,
    JsonNullable<List<EjemplarTransferenciaRequest>> ejemplares,
    Long version
) {
    public TransferenciaRequest {}

    @AssertTrue(message = "Los IDs relacionados a la transferencia deben ser números positivos")
    public boolean isIdsValidos() {
        return esPositivo(idSolicitudTransferencia)
            && esPositivo(idUsuarioEnvia)
            && esPositivo(idUsuarioRecibe)
            && esPositivo(idMuebleOrigen)
            && esPositivo(idMuebleDestino)
            && esPositivo(idEstadoTransferencia);
    }

    @AssertTrue(message = "El mueble origen y el mueble destino no pueden ser el mismo")
    public boolean isMueblesDiferentes() {
        if (idMuebleOrigen == null || !idMuebleOrigen.isPresent() || idMuebleOrigen.get() == null ||
            idMuebleDestino == null || !idMuebleDestino.isPresent() || idMuebleDestino.get() == null) {
            return true;
        }
        return !idMuebleOrigen.get().equals(idMuebleDestino.get());
    }

    private boolean esPositivo(JsonNullable<Long> id) {
        if (id == null || !id.isPresent() || id.get() == null) {
            return true;
        }
        return id.get() > 0;
    }
}
