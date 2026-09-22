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
@DisplayName("FormatoRequest JSON Tests")
class FormatoRequestTest {

    @Autowired
    private JacksonTester<FormatoRequest> json;

    @Test
    @DisplayName("Should serialize FormatoRequest correctly")
    void shouldSerialize() throws IOException {
        FormatoRequest request = FormatoRequest.builder().nombre("Digital").build();
        assertThat(json.write(request)).hasJsonPathStringValue("@.nombre", "Digital");
    }

    @Test
    @DisplayName("Should validate FormatoRequest correctly")
    void shouldValidate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        FormatoRequest invalid = FormatoRequest.builder().nombre("").build();
        assertThat(validator.validate(invalid)).isNotEmpty();
    }
}
