package com.arrupeteca.dto.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("ObraRequest JSON Tests")
class ObraRequestTest {

    @Autowired
    private JacksonTester<ObraRequest> json;

    @Test
    @DisplayName("Should serialize ObraRequest correctly")
    void shouldSerialize() throws IOException {
        ObraRequest request = ObraRequest.builder()
                .titulo("Cien años de soledad")
                .anioCreacion(1967)
                .resumen("Historia de los Buendía...")
                .idGeneros(Set.of(1L))
                .idCategorias(Set.of(2L))
                .build();
        assertThat(json.write(request)).hasJsonPathStringValue("@.titulo", "Cien años de soledad");
        assertThat(json.write(request)).hasJsonPathNumberValue("@.anioCreacion", 1967);
    }

    @Test
    @DisplayName("Should validate ObraRequest correctly")
    void shouldValidate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        ObraRequest invalid = ObraRequest.builder().build();
        assertThat(validator.validate(invalid)).isNotEmpty();
    }
}
