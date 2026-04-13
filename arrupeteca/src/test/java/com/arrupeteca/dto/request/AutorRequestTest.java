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

import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
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
                .nombre("Gabriel García Márquez")
                .seudonimo("Gabo")
                .anioNacimiento(1927)
                .anioFallecimiento(2014)
                .biografia("Escritor colombiano...")
                .build();

        assertThat(json.write(request)).hasJsonPathStringValue("@.nombre", "Gabriel García Márquez");
        assertThat(json.write(request)).hasJsonPathStringValue("@.seudonimo", "Gabo");
        assertThat(json.write(request)).hasJsonPathNumberValue("@.anioNacimiento", 1927);
        assertThat(json.write(request)).hasJsonPathNumberValue("@.anioFallecimiento", 2014);
    }

    @Test
    @DisplayName("Should fail when both nombre and seudonimo are blank")
    void shouldFailWhenIdentityIsInvalid() {
        AutorRequest request = AutorRequest.builder()
                .nombre("")
                .seudonimo("")
                .biografia("Bio")
                .build();

        Set<ConstraintViolation<AutorRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Nombre o un Seudonimo"));
    }

    @Test
    @DisplayName("Should fail when birth year is greater than death year")
    void shouldFailWhenDatesAreInvalid() {
        AutorRequest request = AutorRequest.builder()
                .nombre("Autor")
                .anioNacimiento(2000)
                .anioFallecimiento(1990)
                .biografia("Bio")
                .build();

        Set<ConstraintViolation<AutorRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getMessage().contains("año de nacimiento no puede ser mayor"));
    }
}
