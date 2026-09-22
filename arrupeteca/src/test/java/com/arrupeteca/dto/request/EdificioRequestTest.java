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
@DisplayName("EdificioRequest JSON Tests")
class EdificioRequestTest {

    @Autowired
    private JacksonTester<EdificioRequest> json;

    @Test
    @DisplayName("Should serialize EdificioRequest correctly")
    void shouldSerialize() throws IOException {
        EdificioRequest request = EdificioRequest.builder().nombre("Edificio Central").build();
        assertThat(json.write(request)).hasJsonPathStringValue("@.nombre", "Edificio Central");
    }

    @Test
    @DisplayName("Should validate EdificioRequest correctly")
    void shouldValidate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        EdificioRequest invalid = EdificioRequest.builder().nombre("").build();
        assertThat(validator.validate(invalid)).isNotEmpty();
    }
}
