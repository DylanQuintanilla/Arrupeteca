package com.arrupeteca.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@DisplayName("Autor Architecture Rules (ArchUnit)")
class AutorArchitectureTest {

    private final JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.arrupeteca");

    @Test
    @DisplayName("Services should not access controller layer")
    void servicesShouldNotAccessControllers() {
        ArchRule rule = noClasses().that().resideInAPackage("..service..")
                .should().dependOnClassesThat().resideInAPackage("..controller..");
        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Repositories should not depend on service or controller layers")
    void repositoriesShouldBeIsolated() {
        ArchRule rule = noClasses().that().resideInAPackage("..persistence.repository..")
                .should().dependOnClassesThat().resideInAPackage("..service..")
                .orShould().dependOnClassesThat().resideInAPackage("..controller..");
        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Controllers should only be accessed by external requests, not by services or repositories")
    void controllersShouldBeEntryPointsOnly() {
        ArchRule rule = classes().that().resideInAPackage("..controller..")
                .should().onlyBeAccessed().byAnyPackage("..controller..", "..config..", "..aspect..", "..exception..");
        rule.check(importedClasses);
    }
}
