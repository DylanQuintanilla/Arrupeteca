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
@DisplayName("EditorialRequest JSON Tests")
class EditorialRequestTest {

    @Autowired
    private JacksonTester<EditorialRequest> json;

    @Test
    @DisplayName("Should serialize EditorialRequest correctly")
    void shouldSerialize() throws IOException {
        EditorialRequest request = EditorialRequest.builder()
                .nombre("Norma")
                .correo("contacto@norma.com")
                .telefono("2222-3333")
                .personaContacto("Juan Perez")
                .build();
        assertThat(json.write(request)).hasJsonPathStringValue("@.nombre", "Norma");
        assertThat(json.write(request)).hasJsonPathStringValue("@.correo", "contacto@norma.com");
    }

    @Test
    @DisplayName("Should validate EditorialRequest correctly")
    void shouldValidate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        EditorialRequest invalid = EditorialRequest.builder().nombre("").build();
        assertThat(validator.validate(invalid)).isNotEmpty();
    }
}
