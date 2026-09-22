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
    @DisplayName("Should pass when all fields are valid")
    void shouldPassWhenValid() {
        AutorRequest request = AutorRequest.builder()
                .nombre(JsonNullable.of("Nombre Valido"))
                .seudonimo(JsonNullable.of("Gabo"))
                .anioNacimiento(JsonNullable.of(Year.of(1927)))
                .anioFallecimiento(JsonNullable.of(Year.of(2014)))
                .biografia(JsonNullable.of("Biografia corta"))
                .idPaisNacimiento(JsonNullable.of(1L))
                .idNacionalidad(JsonNullable.of(2L))
                .idGeneroPrincipal(JsonNullable.of(3L))
                .idObraCumbre(JsonNullable.of(4L))
                .build();

        Set<ConstraintViolation<AutorRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail when name exceeds 100 characters")
    void shouldFailWhenNameTooLong() {
        AutorRequest request = AutorRequest.builder()
                .nombre(JsonNullable.of("A".repeat(101)))
                .build();

        Set<ConstraintViolation<AutorRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getMessage().contains("nombre no puede exceder 100"));
    }

    @Test
    @DisplayName("Should fail when pseudonym exceeds 50 characters")
    void shouldFailWhenPseudonymTooLong() {
        AutorRequest request = AutorRequest.builder()
                .seudonimo(JsonNullable.of("A".repeat(51)))
                .build();

        Set<ConstraintViolation<AutorRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getMessage().contains("seudonimo no puede exceder 50"));
    }

    @Test
    @DisplayName("Should fail when biography exceeds 5000 characters")
    void shouldFailWhenBiographyTooLong() {
        AutorRequest request = AutorRequest.builder()
                .biografia(JsonNullable.of("A".repeat(5001)))
                .build();

        Set<ConstraintViolation<AutorRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getMessage().contains("biografia es demasiado larga"));
    }

    @Test
    @DisplayName("Should fail when birth year is in the future")
    void shouldFailWhenBirthYearInFuture() {
        AutorRequest request = AutorRequest.builder()
                .anioNacimiento(JsonNullable.of(Year.now().plusYears(1)))
                .build();

        Set<ConstraintViolation<AutorRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getMessage().contains("nacimiento no puede ser una fecha futura"));
    }

    @Test
    @DisplayName("Should fail when death year is in the future")
    void shouldFailWhenDeathYearInFuture() {
        AutorRequest request = AutorRequest.builder()
                .anioFallecimiento(JsonNullable.of(Year.now().plusYears(1)))
                .build();

        Set<ConstraintViolation<AutorRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getMessage().contains("fallecimiento no puede ser una fecha futura"));
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

    @Test
    @DisplayName("Should fail when relation IDs are negative or zero")
    void shouldFailWhenIdsAreNegativeOrZero() {
        AutorRequest requestZero = AutorRequest.builder()
                .idPaisNacimiento(JsonNullable.of(0L))
                .build();
        Set<ConstraintViolation<AutorRequest>> violationsZero = validator.validate(requestZero);
        assertThat(violationsZero).anyMatch(v -> v.getMessage().contains("IDs de las relaciones deben ser positivos"));

        AutorRequest requestNegative = AutorRequest.builder()
                .idNacionalidad(JsonNullable.of(-1L))
                .build();
        Set<ConstraintViolation<AutorRequest>> violationsNegative = validator.validate(requestNegative);
        assertThat(violationsNegative).anyMatch(v -> v.getMessage().contains("IDs de las relaciones deben ser positivos"));
    }
}
