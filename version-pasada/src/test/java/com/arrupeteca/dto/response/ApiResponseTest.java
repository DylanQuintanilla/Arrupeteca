package com.arrupeteca.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("ApiResponse JSON Tests")
class ApiResponseTest {

    @Autowired
    private JacksonTester<ApiResponse<String>> json;

    @Test
    @DisplayName("Should serialize success response correctly")
    void shouldSerializeSuccess() throws IOException {
        ApiResponse<String> response = ApiResponse.exito("Operación exitosa", "Data");
        
        assertThat(json.write(response)).hasJsonPathBooleanValue("@.exito", true);
        assertThat(json.write(response)).hasJsonPathStringValue("@.mensaje", "Operación exitosa");
        assertThat(json.write(response)).hasJsonPathStringValue("@.data", "Data");
    }

    @Test
    @DisplayName("Should serialize error response correctly")
    void shouldSerializeError() throws IOException {
        ApiResponse<String> response = ApiResponse.error("Error detectado");
        
        assertThat(json.write(response)).hasJsonPathBooleanValue("@.exito", false);
        assertThat(json.write(response)).hasJsonPathStringValue("@.mensaje", "Error detectado");
        assertThat(json.write(response)).hasJsonPath("@.data");
    }
}
