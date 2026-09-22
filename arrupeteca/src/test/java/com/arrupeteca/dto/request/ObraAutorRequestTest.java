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
@DisplayName("ObraAutorRequest JSON Tests")
class ObraAutorRequestTest {

    @Autowired
    private JacksonTester<ObraAutorRequest> json;

    @Test
    @DisplayName("Should serialize ObraAutorRequest correctly")
    void shouldSerialize() throws IOException {
        ObraAutorRequest request = ObraAutorRequest.builder()
                .idAutor(1L)
                .idTipoAutoria(2L)
                .build();
        assertThat(json.write(request)).hasJsonPathNumberValue("@.idAutor", 1);
        assertThat(json.write(request)).hasJsonPathNumberValue("@.idTipoAutoria", 2);
    }

    @Test
    @DisplayName("Should validate ObraAutorRequest correctly")
    void shouldValidate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        ObraAutorRequest invalid = ObraAutorRequest.builder().build();
        assertThat(validator.validate(invalid)).isNotEmpty();
    }
}
