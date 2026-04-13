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
@DisplayName("IdiomaRequest JSON Tests")
class IdiomaRequestTest {

    @Autowired
    private JacksonTester<IdiomaRequest> json;

    @Test
    @DisplayName("Should serialize IdiomaRequest correctly")
    void shouldSerialize() throws IOException {
        IdiomaRequest request = IdiomaRequest.builder().nombre("Español").build();
        assertThat(json.write(request)).hasJsonPathStringValue("@.nombre", "Español");
    }

    @Test
    @DisplayName("Should validate IdiomaRequest correctly")
    void shouldValidate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        IdiomaRequest invalid = IdiomaRequest.builder().nombre("").build();
        assertThat(validator.validate(invalid)).isNotEmpty();
    }
}
