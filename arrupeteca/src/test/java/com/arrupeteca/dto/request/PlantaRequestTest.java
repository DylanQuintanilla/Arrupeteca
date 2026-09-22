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
@DisplayName("PlantaRequest JSON Tests")
class PlantaRequestTest {

    @Autowired
    private JacksonTester<PlantaRequest> json;

    @Test
    @DisplayName("Should serialize PlantaRequest correctly")
    void shouldSerialize() throws IOException {
        PlantaRequest request = PlantaRequest.builder()
                .nombre("Planta Baja")
                .idEdificio(1L)
                .build();
        assertThat(json.write(request)).hasJsonPathStringValue("@.nombre", "Planta Baja");
        assertThat(json.write(request)).hasJsonPathNumberValue("@.idEdificio", 1);
    }

    @Test
    @DisplayName("Should validate PlantaRequest correctly")
    void shouldValidate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        PlantaRequest invalid = PlantaRequest.builder().build();
        assertThat(validator.validate(invalid)).isNotEmpty();
    }
}
