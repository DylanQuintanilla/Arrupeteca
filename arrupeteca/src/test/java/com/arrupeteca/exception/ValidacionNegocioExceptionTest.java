package com.arrupeteca.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ValidacionNegocioException Tests")
class ValidacionNegocioExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    void shouldCreateWithMessage() {
        ValidacionNegocioException ex = new ValidacionNegocioException("Error de negocio");
        assertThat(ex.getMessage()).isEqualTo("Error de negocio");
    }
}
