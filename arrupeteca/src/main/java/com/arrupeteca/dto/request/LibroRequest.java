package com.arrupeteca.dto.request;

import java.math.BigDecimal;
import java.util.Set;
import com.arrupeteca.persistence.enums.Ciclo;
import com.arrupeteca.persistence.enums.Grado;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.AssertTrue;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.openapitools.jackson.nullable.JsonNullable;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record LibroRequest(
    JsonNullable<String> isbn,
    JsonNullable<Integer> anioPublicacion,
    JsonNullable<BigDecimal> precio,
    JsonNullable<String> urlImagen,
    JsonNullable<BigDecimal> mora,
    JsonNullable<Grado> grado,
    JsonNullable<Ciclo> ciclo,
    JsonNullable<Long> idObra,
    JsonNullable<Long> idFormato,
    JsonNullable<Long> idEditorial,
    JsonNullable<Set<Long>> idIdiomas,
    Long version
) {
    public LibroRequest {}

    @AssertTrue(message = "El ISBN no es válido")
    public boolean isIsbnValido() {
        if (isbn == null || !isbn.isPresent() || isbn.get() == null || isbn.get().isBlank()) {
            return true;
        }
        String val = isbn.get().replaceAll("[-\\s]", "");
        if (val.length() != 10 && val.length() != 13) {
            return false;
        }
        return val.matches("^[0-9xX]+$");
    }

    @AssertTrue(message = "El ISBN no puede contener más de 20 caracteres")
    public boolean isIsbnLongitudValida() {
        if (isbn == null || !isbn.isPresent() || isbn.get() == null) {
            return true;
        }
        return isbn.get().length() <= 20;
    }

    @AssertTrue(message = "El año de publicación no puede tener más de 4 dígitos")
    public boolean isAnioPublicacionValido() {
        if (anioPublicacion == null || !anioPublicacion.isPresent() || anioPublicacion.get() == null) {
            return true;
        }
        return anioPublicacion.get() <= 9999;
    }

    @AssertTrue(message = "El precio no puede ser negativo")
    public boolean isPrecioValido() {
        if (precio == null || !precio.isPresent() || precio.get() == null) {
            return true;
        }
        return precio.get().compareTo(BigDecimal.ZERO) >= 0;
    }

    @AssertTrue(message = "La mora no puede ser negativa")
    public boolean isMoraValida() {
        if (mora == null || !mora.isPresent() || mora.get() == null) {
            return true;
        }
        return mora.get().compareTo(BigDecimal.ZERO) >= 0;
    }
}
