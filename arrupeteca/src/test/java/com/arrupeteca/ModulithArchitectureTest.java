package com.arrupeteca;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModulithArchitectureTest {

    @Test
    void verificarEstructuraYLimitesDeModulos() {
        ApplicationModules modules = ApplicationModules.of(ArrupetecaApplication.class);
        modules.verify();
    }
}
