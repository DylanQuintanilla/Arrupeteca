package com.arrupeteca.dto.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;
import com.arrupeteca.config.JacksonConfig;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Import(JacksonConfig.class)
@DisplayName("LibroRequest JSON Tests")
class LibroRequestTest {

    @Autowired
    private JacksonTester<LibroRequest> json;

    @Test
    @DisplayName("Should serialize LibroRequest correctly")
    void shouldSerialize() throws IOException {
        LibroRequest request = LibroRequest.builder()
                .isbn(JsonNullable.of("978-3-16-148410-0"))
                .anioPublicacion(JsonNullable.of(2021))
                .precio(JsonNullable.of(new BigDecimal("29.99")))
                .idGrado(JsonNullable.of(1L))
                .idCiclo(JsonNullable.of(1L))
                .idObra(JsonNullable.of(1L))
                .idFormato(JsonNullable.of(2L))
                .idEditorial(JsonNullable.of(3L))
                .idIdiomas(JsonNullable.of(Set.of(4L)))
                .build();
        assertThat(json.write(request)).hasJsonPathStringValue("@.isbn", "978-3-16-148410-0");
        assertThat(json.write(request)).hasJsonPathNumberValue("@.anioPublicacion", 2021);
    }

    @Test
    @DisplayName("Should validate LibroRequest correctly")
    void shouldValidate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        // Valid
        LibroRequest valid = LibroRequest.builder()
                .isbn(JsonNullable.of("9780596520687"))
                .anioPublicacion(JsonNullable.of(2021))
                .precio(JsonNullable.of(BigDecimal.TEN))
                .mora(JsonNullable.of(BigDecimal.ONE))
                .build();
        assertThat(validator.validate(valid)).isEmpty();

        // Invalid ISBN format
        LibroRequest invalidIsbn = LibroRequest.builder()
                .isbn(JsonNullable.of("invalid-isbn"))
                .build();
        assertThat(validator.validate(invalidIsbn)).isNotEmpty();

        // Invalid negative price
        LibroRequest negativePrice = LibroRequest.builder()
                .precio(JsonNullable.of(new BigDecimal("-10.00")))
                .build();
        assertThat(validator.validate(negativePrice)).isNotEmpty();
    }
}
