package com.arrupeteca.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.context.annotation.Import;
import com.arrupeteca.config.JacksonConfig;

import java.io.IOException;
import java.time.Year;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Import(JacksonConfig.class)
@DisplayName("AutorRequest JSON and Validation Tests")
class AutorRequestTest {

    @Autowired
    private JacksonTester<AutorRequest> json;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should serialize AutorRequest correctly")
    void shouldSerialize() throws IOException {
        AutorRequest request = AutorRequest.builder()
                .nombre(JsonNullable.of("Gabriel García Márquez"))
                .seudonimo(JsonNullable.of("Gabo"))
                .anioNacimiento(JsonNullable.of(Year.of(1927)))
                .anioFallecimiento(JsonNullable.of(Year.of(2014)))
                .biografia(JsonNullable.of("Escritor colombiano..."))
                .build();

        assertThat(json.write(request)).hasJsonPathStringValue("@.nombre", "Gabriel García Márquez");
        assertThat(json.write(request)).hasJsonPathStringValue("@.seudonimo", "Gabo");
        assertThat(json.write(request)).hasJsonPathStringValue("@.anioNacimiento", "1927");
        assertThat(json.write(request)).hasJsonPathStringValue("@.anioFallecimiento", "2014");
    }



    @Test
    @DisplayName("Should fail when birth year is greater than death year")
    void shouldFailWhenDatesAreInvalid() {
        AutorRequest request = AutorRequest.builder()
                .nombre(JsonNullable.of("Autor"))
                .anioNacimiento(JsonNullable.of(Year.of(2000)))
                .anioFallecimiento(JsonNullable.of(Year.of(1990)))
                .biografia(JsonNullable.of("Bio"))
                .build();

        Set<ConstraintViolation<AutorRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getMessage().contains("año de nacimiento no puede ser mayor"));
    }
}
