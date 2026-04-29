package com.arrupeteca.persistence.entity;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Base class for entity validation tests.
 */
public abstract class BaseEntityTest {

    protected Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    protected <T> void assertNoViolations(T entity) {
        Set<ConstraintViolation<T>> violations = validator.validate(entity);
        assertThat(violations).isEmpty();
    }

    protected <T> void assertHasViolation(T entity, String propertyPath, String message) {
        Set<ConstraintViolation<T>> violations = validator.validate(entity);
        if (violations.isEmpty() || violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals(propertyPath)
                        && v.getMessage().equals(message))) {
            System.out.println("Violations found: ");
            violations.forEach(v -> System.out.println("  " + v.getPropertyPath() + ": " + v.getMessage()));
        }
        assertThat(violations).anySatisfy(violation -> {
            assertThat(violation.getPropertyPath().toString()).isEqualTo(propertyPath);
            assertThat(violation.getMessage()).isEqualTo(message);
        });
    }

    protected <T> void assertHasViolationCount(T entity, int count) {
        Set<ConstraintViolation<T>> violations = validator.validate(entity);
        assertThat(violations).hasSize(count);
    }

}
