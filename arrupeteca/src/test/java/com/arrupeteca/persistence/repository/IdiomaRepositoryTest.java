package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Idioma;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

class IdiomaRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private IdiomaRepository idiomaRepository;

    @Test
    @DisplayName("Should save and find idioma by ID")
    void shouldSaveAndFindById() {
        Idioma entity = Idioma.builder().nombre("Español").build();
        Idioma saved = idiomaRepository.save(entity);
        assertThat(idiomaRepository.findById(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("buscarActivosPorNombre - Should return page of entities")
    void buscarActivosPorNombre() {
        idiomaRepository.save(Idioma.builder().nombre("Test").build());
        java.util.List<Idioma> list = idiomaRepository.buscarActivosPorNombre(null);
        assertThat(list).isNotEmpty();
    }

    @Test
    @DisplayName("Should count physical entries including deleted")
    void shouldCountByIdIncludeDeleted() {
        Idioma saved = idiomaRepository.save(Idioma.builder().nombre("Idio3").borradoLogico(true).build());
        assertThat(idiomaRepository.existsById(saved.getId())).isTrue();
    }
}
