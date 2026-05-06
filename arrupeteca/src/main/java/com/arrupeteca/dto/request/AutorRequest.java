package com.arrupeteca.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.Year;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record AutorRequest(
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    String nombre,

    @Size(max = 50, message = "El seudonimo no puede exceder 50 caracteres")
    String seudonimo,

    @PastOrPresent(message = "El año de nacimiento no puede ser una fecha futura")
    Year anioNacimiento,

    @PastOrPresent(message = "El año de fallecimiento no puede ser una fecha futura")
    Year anioFallecimiento,

    @NotBlank(message = "La biografia no puede estar vacía")
    @Size(max = 5000, message = "La biografia es demasiado larga")
    String biografia,

    @Positive(message = "ID de país inválido")
    Long idPaisNacimiento,

    @Positive(message = "ID de nacionalidad inválido")
    Long idNacionalidad,

    @Positive(message = "ID de género inválido")
    Long idGeneroPrincipal,

    @Positive(message = "ID de obra inválido")
    Long idObraCumbre
) {

    public AutorRequest {

    }

    @AssertTrue(message = "El autor debe tener al menos un Nombre o un Seudonimo")
    public boolean isIdentidadValida() {
        boolean tieneNombre = (nombre != null && !nombre.isBlank());
        boolean tieneSeudonimo = (seudonimo != null && !seudonimo.isBlank());
        return tieneNombre || tieneSeudonimo;
    }

    @AssertTrue(message = "El año de nacimiento no puede ser mayor al año de fallecimiento")
    public boolean isFechasValidas() {
        if (anioNacimiento == null || anioFallecimiento == null) {
            return true;
        }
        return !anioNacimiento.isAfter(anioFallecimiento);
    }

}
