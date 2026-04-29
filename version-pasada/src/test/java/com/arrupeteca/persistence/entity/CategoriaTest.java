package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Categoria Persistence Tests")
class CategoriaTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should persist Categoria")
    void shouldPersistCategoria() {
        Categoria cat = Categoria.builder()
                .nombre("Test Cat")
                .build();
        Categoria persisted = entityManager.persistFlushFind(cat);
        assertThat(persisted.getId()).isNotNull();
    }
}
