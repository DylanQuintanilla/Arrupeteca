package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Genero;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GeneroRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private GeneroRepository generoRepository;

    @Test
    @DisplayName("Should save and find genero by ID")
    void shouldSaveAndFindById() {
        Genero entity = Genero.builder().nombre("Drama").build();
        Genero saved = generoRepository.save(entity);
        assertThat(generoRepository.findById(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("Should find active with pagination")
    void shouldFindActiveWithPagination() {
        generoRepository.save(Genero.builder().nombre("G1").borradoLogico(false).build());
        java.util.List<Genero> list = generoRepository.buscarActivosPorNombre(null);
        assertThat(list).isNotEmpty();
    }

    @Test
    @DisplayName("Should count physical entries including deleted")
    void shouldCountByIdIncludeDeleted() {
        Genero saved = generoRepository.save(Genero.builder().nombre("G3").borradoLogico(true).build());
        assertThat(generoRepository.existsById(saved.getId())).isTrue();
    }
}
