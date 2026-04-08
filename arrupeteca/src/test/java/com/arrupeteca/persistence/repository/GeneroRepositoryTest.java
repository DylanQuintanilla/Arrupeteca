package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Genero;
import com.arrupeteca.persistence.projection.GeneroResumen;
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
        assertThat(generoRepository.findProjectedByIdAndBorradoLogicoFalse(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("Should find active with pagination")
    void shouldFindActiveWithPagination() {
        generoRepository.save(Genero.builder().nombre("G1").borradoLogico(false).build());
        Page<GeneroResumen> page = generoRepository.findByBorradoLogicoFalse(PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isGreaterThan(0);
    }
}
