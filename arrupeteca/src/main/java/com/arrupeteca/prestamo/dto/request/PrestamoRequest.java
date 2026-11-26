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
public record PrestamoRequest(
    JsonNullable<Long> idSolicitud,
    JsonNullable<Long> idUsuario,
    JsonNullable<Long> idEstadoPrestamo,
    JsonNullable<LocalDateTime> fechaPrestamo,
    JsonNullable<LocalDateTime> fechaLimiteDevolucion,
    JsonNullable<Long> entregadoPor,
    JsonNullable<String> comentarios,
    Long version
) {
    public PrestamoRequest {}

    @AssertTrue(message = "El comentario no puede exceder 255 caracteres")
    public boolean isComentariosValido() {
        if (comentarios == null || !comentarios.isPresent() || comentarios.get() == null) {
            return true;
        }
        return comentarios.get().length() <= 255;
    }

    @AssertTrue(message = "Los IDs relacionados al préstamo deben ser números positivos")
    public boolean isIdsValidos() {
        return esPositivo(idSolicitud)
            && esPositivo(idUsuario)
            && esPositivo(idEstadoPrestamo)
            && esPositivo(entregadoPor);
    }

    @AssertTrue(message = "La fecha límite de devolución debe ser estrictamente posterior a la fecha del préstamo")
    public boolean isFechasValidas() {
        if (fechaPrestamo == null || !fechaPrestamo.isPresent() || fechaPrestamo.get() == null ||
            fechaLimiteDevolucion == null || !fechaLimiteDevolucion.isPresent() || fechaLimiteDevolucion.get() == null) {
            return true;
        }
        return fechaLimiteDevolucion.get().isAfter(fechaPrestamo.get());
    }

    private boolean esPositivo(JsonNullable<Long> id) {
        if (id == null || !id.isPresent() || id.get() == null) {
            return true;
        }
        return id.get() > 0;
    }
}
