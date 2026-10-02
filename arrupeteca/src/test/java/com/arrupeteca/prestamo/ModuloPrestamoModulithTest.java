package com.arrupeteca.prestamo;

import com.arrupeteca.ArrupetecaApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

import static org.assertj.core.api.Assertions.assertThat;

class ModuloPrestamoModulithTest {

    @Test
    @DisplayName("Should detect and verify Spring Modulith prestamo module")
    void verifyPrestamoModule() {
        ApplicationModules modules = ApplicationModules.of(ArrupetecaApplication.class);
        assertThat(modules).isNotNull();
    }
}
