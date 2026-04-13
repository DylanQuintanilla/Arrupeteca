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
@DisplayName("NacionalidadRequest JSON Tests")
class NacionalidadRequestTest {

    @Autowired
    private JacksonTester<NacionalidadRequest> json;

    @Test
    @DisplayName("Should serialize NacionalidadRequest correctly")
    void shouldSerialize() throws IOException {
        NacionalidadRequest request = NacionalidadRequest.builder().nombre("Salvadoreña").build();
        assertThat(json.write(request)).hasJsonPathStringValue("@.nombre", "Salvadoreña");
    }

    @Test
    @DisplayName("Should validate NacionalidadRequest correctly")
    void shouldValidate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        NacionalidadRequest invalid = NacionalidadRequest.builder().nombre("").build();
        assertThat(validator.validate(invalid)).isNotEmpty();
    }
}
