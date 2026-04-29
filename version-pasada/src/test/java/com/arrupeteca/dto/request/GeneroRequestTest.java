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
@DisplayName("GeneroRequest JSON Tests")
class GeneroRequestTest {

    @Autowired
    private JacksonTester<GeneroRequest> json;

    @Test
    @DisplayName("Should serialize GeneroRequest correctly")
    void shouldSerialize() throws IOException {
        GeneroRequest request = GeneroRequest.builder().nombre("Drama").build();
        assertThat(json.write(request)).hasJsonPathStringValue("@.nombre", "Drama");
    }

    @Test
    @DisplayName("Should validate GeneroRequest correctly")
    void shouldValidate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        GeneroRequest invalid = GeneroRequest.builder().nombre("").build();
        assertThat(validator.validate(invalid)).isNotEmpty();
    }
}
