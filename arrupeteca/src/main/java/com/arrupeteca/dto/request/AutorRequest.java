package com.arrupeteca.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Year;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.openapitools.jackson.nullable.JsonNullable;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record AutorRequest(
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    String nombre,

    JsonNullable<String> seudonimo,

    JsonNullable<Year> anioNacimiento,

    JsonNullable<Year> anioFallecimiento,

    @NotBlank(message = "La biografia no puede estar vacía")
    @Size(max = 5000, message = "La biografia es demasiado larga")
    String biografia,

    Long version,

    JsonNullable<Long> idPaisNacimiento,

    JsonNullable<Long> idNacionalidad,

    JsonNullable<Long> idGeneroPrincipal,

    JsonNullable<Long> idObraCumbre
) {

    public AutorRequest {

    }

    @AssertTrue(message = "El autor debe tener al menos un Nombre o un Seudonimo")
    public boolean isIdentidadValida() {
        boolean tieneNombre = (nombre != null && !nombre.isBlank());
        boolean tieneSeudonimo = (seudonimo != null && seudonimo.isPresent() && seudonimo.get() != null && !seudonimo.get().isBlank());
        return tieneNombre || tieneSeudonimo;
    }

    @AssertTrue(message = "El seudonimo no puede exceder 50 caracteres")
    public boolean isSeudonimoValido() {
        if (seudonimo == null || !seudonimo.isPresent() || seudonimo.get() == null) {
            return true;
        }
        return seudonimo.get().length() <= 50;
    }

    @AssertTrue(message = "El año de nacimiento no puede ser una fecha futura")
    public boolean isAnioNacimientoValido() {
        if (anioNacimiento == null || !anioNacimiento.isPresent() || anioNacimiento.get() == null) {
            return true;
        }
        return !anioNacimiento.get().isAfter(Year.now());
    }

    @AssertTrue(message = "El año de fallecimiento no puede ser una fecha futura")
    public boolean isAnioFallecimientoValido() {
        if (anioFallecimiento == null || !anioFallecimiento.isPresent() || anioFallecimiento.get() == null) {
            return true;
        }
        return !anioFallecimiento.get().isAfter(Year.now());
    }

    @AssertTrue(message = "El año de nacimiento no puede ser mayor al año de fallecimiento")
    public boolean isFechasValidas() {
        Year nacimiento = (anioNacimiento != null && anioNacimiento.isPresent()) ? anioNacimiento.get() : null;
        Year fallecimiento = (anioFallecimiento != null && anioFallecimiento.isPresent()) ? anioFallecimiento.get() : null;
        if (nacimiento == null || fallecimiento == null) {
            return true;
        }
        return !nacimiento.isAfter(fallecimiento);
    }

    @AssertTrue(message = "Los IDs de las relaciones deben ser positivos")
    public boolean isIdsValidos() {
        return esPositivo(idPaisNacimiento)
            && esPositivo(idNacionalidad)
            && esPositivo(idGeneroPrincipal)
            && esPositivo(idObraCumbre);
    }

    private boolean esPositivo(JsonNullable<Long> id) {
        if (id == null || !id.isPresent() || id.get() == null) {
            return true;
        }
        return id.get() > 0;
    }
}
