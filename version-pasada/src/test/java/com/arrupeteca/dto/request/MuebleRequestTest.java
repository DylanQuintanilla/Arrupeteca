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
@DisplayName("MuebleRequest JSON Tests")
class MuebleRequestTest {

    @Autowired
    private JacksonTester<MuebleRequest> json;

    @Test
    @DisplayName("Should serialize MuebleRequest correctly")
    void shouldSerialize() throws IOException {
        MuebleRequest request = MuebleRequest.builder()
                .nombre("Estante A")
                .idSalon(1L)
                .build();
        assertThat(json.write(request)).hasJsonPathStringValue("@.nombre", "Estante A");
        assertThat(json.write(request)).hasJsonPathNumberValue("@.idSalon", 1);
    }

    @Test
    @DisplayName("Should validate MuebleRequest correctly")
    void shouldValidate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        MuebleRequest invalid = MuebleRequest.builder().build();
        assertThat(validator.validate(invalid)).isNotEmpty();
    }
}
