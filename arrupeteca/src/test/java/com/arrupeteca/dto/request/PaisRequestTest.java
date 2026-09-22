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
@DisplayName("PaisRequest JSON Tests")
class PaisRequestTest {

    @Autowired
    private JacksonTester<PaisRequest> json;

    @Test
    @DisplayName("Should serialize PaisRequest correctly")
    void shouldSerialize() throws IOException {
        PaisRequest request = PaisRequest.builder().nombre("El Salvador").build();
        assertThat(json.write(request)).hasJsonPathStringValue("@.nombre", "El Salvador");
    }

    @Test
    @DisplayName("Should validate PaisRequest correctly")
    void shouldValidate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        PaisRequest invalid = PaisRequest.builder().nombre("").build();
        assertThat(validator.validate(invalid)).isNotEmpty();
    }
}
