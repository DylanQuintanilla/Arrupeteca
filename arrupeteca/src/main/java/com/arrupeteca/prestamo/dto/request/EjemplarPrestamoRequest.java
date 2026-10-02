package com.arrupeteca.prestamo.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.AssertTrue;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.openapitools.jackson.nullable.JsonNullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record EjemplarPrestamoRequest(
    JsonNullable<Long> idPrestamo,
    JsonNullable<Long> idEjemplar,
    JsonNullable<Long> idEstadoEjemplarPrestamo,
    JsonNullable<Long> idEstadoFisicoEntrega,
    JsonNullable<Long> idEstadoFisicoDevolucion,
    JsonNullable<LocalDateTime> fechaDevolucionReal,
    JsonNullable<BigDecimal> moraCalculada,
    JsonNullable<Boolean> moraPagada,
    JsonNullable<Boolean> exentoMora,
    JsonNullable<Long> recibidoPor,
    Long version
) {
    public EjemplarPrestamoRequest {}

    @AssertTrue(message = "El monto de la mora calculada no puede ser negativo")
    public boolean isMoraValida() {
        if (moraCalculada == null || !moraCalculada.isPresent() || moraCalculada.get() == null) {
            return true;
        }
        return moraCalculada.get().compareTo(BigDecimal.ZERO) >= 0;
    }

    @AssertTrue(message = "Los IDs del préstamo y ejemplares deben ser números positivos")
    public boolean isIdsValidos() {
        return esPositivo(idPrestamo)
            && esPositivo(idEjemplar)
            && esPositivo(idEstadoEjemplarPrestamo)
            && esPositivo(idEstadoFisicoEntrega)
            && esPositivo(idEstadoFisicoDevolucion)
            && esPositivo(recibidoPor);
    }

    private boolean esPositivo(JsonNullable<Long> id) {
        if (id == null || !id.isPresent() || id.get() == null) {
            return true;
        }
        return id.get() > 0;
    }
}
