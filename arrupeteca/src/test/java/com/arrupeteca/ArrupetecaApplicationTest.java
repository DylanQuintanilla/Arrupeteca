package com.arrupeteca;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("ArrupetecaApplication Tests")
class ArrupetecaApplicationTest {

    @Test
    @DisplayName("Context loads")
    void contextLoads() {
        // Test para verificar que el contexto de Spring carga correctamente
    }
}
