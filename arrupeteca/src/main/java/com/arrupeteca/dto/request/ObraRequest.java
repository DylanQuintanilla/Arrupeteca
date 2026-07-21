package com.arrupeteca.dto.request;

import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.AssertTrue;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.openapitools.jackson.nullable.JsonNullable;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record ObraRequest(
    JsonNullable<String> titulo,
    JsonNullable<Integer> anioCreacion,
    JsonNullable<String> resumen,
    JsonNullable<Set<Long>> idGeneros,
    JsonNullable<Set<Long>> idCategorias,
    JsonNullable<Set<ObraAutorRequest>> autores,
    Long version
) {
    public ObraRequest {}

    @AssertTrue(message = "El título debe tener un máximo de 100 caracteres")
    public boolean isTituloValido() {
        if (titulo == null || !titulo.isPresent() || titulo.get() == null) {
            return true;
        }
        return titulo.get().length() <= 100;
    }

    @AssertTrue(message = "El año de creación no puede tener más de 4 dígitos")
    public boolean isAnioCreacionValido() {
        if (anioCreacion == null || !anioCreacion.isPresent() || anioCreacion.get() == null) {
            return true;
        }
        return anioCreacion.get() <= 9999;
    }
}
