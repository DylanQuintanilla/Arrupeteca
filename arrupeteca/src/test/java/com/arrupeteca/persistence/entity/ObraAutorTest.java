package com.arrupeteca.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import com.arrupeteca.config.JpaConfig;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
@ActiveProfiles("test")
@DisplayName("ObraAutor Persistence Tests")
class ObraAutorTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should persist ObraAutor relationship correctly")
    void shouldPersistObraAutor() {
        // Given
        Autor autor = Autor.builder()
                .nombre("Autor Test")
                .biografia("Bio Test")
                .build();
        entityManager.persist(autor);

        Genero genero = Genero.builder().nombre("Genero Test").build();
        entityManager.persist(genero);

        Categoria categoria = Categoria.builder().nombre("Categoria Test").build();
        entityManager.persist(categoria);

        Obra obra = Obra.builder()
                .titulo("Obra Test")
                .resumen("Resumen Test")
                .generos(java.util.Set.of(genero))
                .categorias(java.util.Set.of(categoria))
                .build();
        entityManager.persist(obra);

        TipoAutoria tipoAutoria = TipoAutoria.builder()
                .nombre("Principal")
                .build();
        entityManager.persist(tipoAutoria);

        ObraAutor obraAutor = ObraAutor.builder()
                .autor(autor)
                .obra(obra)
                .tipoAutoria(tipoAutoria)
                .build();

        // When
        ObraAutor persisted = entityManager.persistFlushFind(obraAutor);

        // Then
        assertThat(persisted).isNotNull();
        assertThat(persisted.getId()).isNotNull();
        assertThat(persisted.getAutor()).isEqualTo(autor);
        assertThat(persisted.getObra()).isEqualTo(obra);
        assertThat(persisted.getTipoAutoria()).isEqualTo(tipoAutoria);
    }
}
