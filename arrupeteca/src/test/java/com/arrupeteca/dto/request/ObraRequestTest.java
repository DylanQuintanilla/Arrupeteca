package com.arrupeteca.dto.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@org.springframework.context.annotation.Import(com.arrupeteca.config.JacksonConfig.class)
@DisplayName("ObraRequest JSON Tests")
class ObraRequestTest {

    @Autowired
    private JacksonTester<ObraRequest> json;

    @Test
    @DisplayName("Should serialize ObraRequest correctly")
    void shouldSerialize() throws IOException {
        ObraRequest request = ObraRequest.builder()
                .titulo(JsonNullable.of("Cien años de soledad"))
                .anioCreacion(JsonNullable.of(1967))
                .resumen(JsonNullable.of("Historia de los Buendía..."))
                .idGeneros(JsonNullable.of(Set.of(1L)))
                .idCategorias(JsonNullable.of(Set.of(2L)))
                .build();
        assertThat(json.write(request)).hasJsonPathStringValue("@.titulo", "Cien años de soledad");
        assertThat(json.write(request)).hasJsonPathNumberValue("@.anioCreacion", 1967);
    }

    @Test
    @DisplayName("Should validate ObraRequest correctly")
    void shouldValidate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        // Valid
        ObraRequest valid = ObraRequest.builder()
                .titulo(JsonNullable.of("Cien años de soledad"))
                .anioCreacion(JsonNullable.of(1967))
                .build();
        assertThat(validator.validate(valid)).isEmpty();

        // Invalid title (too long)
        ObraRequest invalidTitle = ObraRequest.builder()
                .titulo(JsonNullable.of("a".repeat(101)))
                .build();
        assertThat(validator.validate(invalidTitle)).isNotEmpty();

        // Invalid year
        ObraRequest invalidYear = ObraRequest.builder()
                .anioCreacion(JsonNullable.of(10000))
                .build();
        assertThat(validator.validate(invalidYear)).isNotEmpty();
    }
}
