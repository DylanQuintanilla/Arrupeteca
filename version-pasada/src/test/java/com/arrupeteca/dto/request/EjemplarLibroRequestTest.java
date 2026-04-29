package com.arrupeteca.dto.request;

import com.arrupeteca.persistence.enums.Disponibilidad;
import com.arrupeteca.persistence.enums.EstadoFisico;
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
@DisplayName("EjemplarLibroRequest JSON Tests")
class EjemplarLibroRequestTest {

    @Autowired
    private JacksonTester<EjemplarLibroRequest> json;

    @Test
    @DisplayName("Should serialize EjemplarLibroRequest correctly")
    void shouldSerialize() throws IOException {
        EjemplarLibroRequest request = EjemplarLibroRequest.builder()
                .comentario("Nuevo ejemplar")
                .estadoFisico(EstadoFisico.NUEVO)
                .disponibilidad(Disponibilidad.DISPONIBLE)
                .idLibro(1L)
                .idMueble(2L)
                .build();
        assertThat(json.write(request)).hasJsonPathStringValue("@.comentario", "Nuevo ejemplar");
        assertThat(json.write(request)).hasJsonPathStringValue("@.estadoFisico", "NUEVO");
    }

    @Test
    @DisplayName("Should validate EjemplarLibroRequest correctly")
    void shouldValidate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        EjemplarLibroRequest invalid = EjemplarLibroRequest.builder().build();
        assertThat(validator.validate(invalid)).isNotEmpty();
    }
}
