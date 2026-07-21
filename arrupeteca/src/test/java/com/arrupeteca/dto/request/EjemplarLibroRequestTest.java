package com.arrupeteca.dto.request;

import com.arrupeteca.persistence.enums.Disponibilidad;
import com.arrupeteca.persistence.enums.EstadoFisico;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;
import com.arrupeteca.config.JacksonConfig;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Import(JacksonConfig.class)
@DisplayName("EjemplarLibroRequest JSON Tests")
class EjemplarLibroRequestTest {

    @Autowired
    private JacksonTester<EjemplarLibroRequest> json;

    @Test
    @DisplayName("Should serialize EjemplarLibroRequest correctly")
    void shouldSerialize() throws IOException {
        EjemplarLibroRequest request = EjemplarLibroRequest.builder()
                .comentario(JsonNullable.of("Nuevo ejemplar"))
                .estadoFisico(JsonNullable.of(EstadoFisico.NUEVO))
                .disponibilidad(JsonNullable.of(Disponibilidad.DISPONIBLE))
                .idLibro(JsonNullable.of(1L))
                .idMueble(JsonNullable.of(2L))
                .build();
        assertThat(json.write(request)).hasJsonPathStringValue("@.comentario", "Nuevo ejemplar");
        assertThat(json.write(request)).hasJsonPathStringValue("@.estadoFisico", "NUEVO");
    }

    @Test
    @DisplayName("Should validate EjemplarLibroRequest correctly")
    void shouldValidate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        // Valid
        EjemplarLibroRequest valid = EjemplarLibroRequest.builder()
                .comentario(JsonNullable.of("Comentario válido"))
                .build();
        assertThat(validator.validate(valid)).isEmpty();

        // Too long comentario
        EjemplarLibroRequest invalidComentario = EjemplarLibroRequest.builder()
                .comentario(JsonNullable.of("a".repeat(256)))
                .build();
        assertThat(validator.validate(invalidComentario)).isNotEmpty();
    }
}
