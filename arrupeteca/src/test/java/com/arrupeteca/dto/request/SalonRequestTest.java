package com.arrupeteca.dto.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("SalonRequest JSON Tests")
class SalonRequestTest {

    @Autowired
    private JacksonTester<SalonRequest> json;

    @Test
    @DisplayName("Should serialize SalonRequest correctly")
    void shouldSerialize() throws IOException {
        SalonRequest request = SalonRequest.builder()
                .nombre("Salon A")
                .idPlanta(1L)
                .build();
        assertThat(json.write(request)).hasJsonPathStringValue("@.nombre", "Salon A");
        assertThat(json.write(request)).hasJsonPathNumberValue("@.idPlanta", 1);
    }

    @Test
    @DisplayName("Should validate SalonRequest correctly")
    void shouldValidate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        SalonRequest invalid = SalonRequest.builder().build();
        assertThat(validator.validate(invalid)).isNotEmpty();
    }
}
