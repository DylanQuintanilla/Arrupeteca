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
@DisplayName("CategoriaRequest JSON Tests")
class CategoriaRequestTest {

    @Autowired
    private JacksonTester<CategoriaRequest> json;

    @Test
    @DisplayName("Should serialize CategoriaRequest correctly")
    void shouldSerialize() throws IOException {
        CategoriaRequest request = CategoriaRequest.builder().nombre("Ficción").build();
        assertThat(json.write(request)).hasJsonPathStringValue("@.nombre", "Ficción");
    }

    @Test
    @DisplayName("Should validate CategoriaRequest correctly")
    void shouldValidate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        CategoriaRequest invalid = CategoriaRequest.builder().nombre("").build();
        assertThat(validator.validate(invalid)).isNotEmpty();
    }
}
