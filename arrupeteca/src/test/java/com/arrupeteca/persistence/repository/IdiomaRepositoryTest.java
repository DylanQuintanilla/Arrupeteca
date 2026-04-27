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
        Idioma entity = Idioma.builder().nombre("Francés").build();
        Idioma saved = idiomaRepository.save(entity);
        assertThat(idiomaRepository.findProjectedByIdAndBorradoLogicoFalse(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("Should find active with pagination")
    void shouldFindActiveWithPagination() {
        idiomaRepository.save(Idioma.builder().nombre("I1").borradoLogico(false).build());
        Page<Idioma> page = idiomaRepository.findByBorradoLogicoFalse(PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isGreaterThan(0);
    }
}
