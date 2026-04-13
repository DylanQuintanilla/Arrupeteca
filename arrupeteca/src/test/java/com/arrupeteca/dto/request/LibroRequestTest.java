package com.arrupeteca.dto.request;

import com.arrupeteca.persistence.enums.Ciclo;
import com.arrupeteca.persistence.enums.Grado;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("LibroRequest JSON Tests")
class LibroRequestTest {

    @Autowired
    private JacksonTester<LibroRequest> json;

    @Test
    @DisplayName("Should serialize LibroRequest correctly")
    void shouldSerialize() throws IOException {
        LibroRequest request = LibroRequest.builder()
                .isbn("978-3-16-148410-0")
                .anioPublicacion(2021)
                .precio(new BigDecimal("29.99"))
                .grado(Grado.PRIMER_TO_NOVENO)
                .ciclo(Ciclo.PRIMER)
                .idObra(1L)
                .idFormato(2L)
                .idEditorial(3L)
                .idIdiomas(Set.of(4L))
                .build();
        assertThat(json.write(request)).hasJsonPathStringValue("@.isbn", "978-3-16-148410-0");
        assertThat(json.write(request)).hasJsonPathNumberValue("@.anioPublicacion", 2021);
    }

    @Test
    @DisplayName("Should validate LibroRequest correctly")
    void shouldValidate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        LibroRequest invalid = LibroRequest.builder().build();
        assertThat(validator.validate(invalid)).isNotEmpty();
    }
}
