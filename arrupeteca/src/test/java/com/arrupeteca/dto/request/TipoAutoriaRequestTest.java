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
@DisplayName("TipoAutoriaRequest JSON Tests")
class TipoAutoriaRequestTest {

    @Autowired
    private JacksonTester<TipoAutoriaRequest> json;

    @Test
    @DisplayName("Should serialize TipoAutoriaRequest correctly")
    void shouldSerialize() throws IOException {
        TipoAutoriaRequest request = TipoAutoriaRequest.builder().nombre("Autor Principal").build();
        assertThat(json.write(request)).hasJsonPathStringValue("@.nombre", "Autor Principal");
    }

    @Test
    @DisplayName("Should validate TipoAutoriaRequest correctly")
    void shouldValidate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        TipoAutoriaRequest invalid = TipoAutoriaRequest.builder().nombre("").build();
        assertThat(validator.validate(invalid)).isNotEmpty();
    }
}
