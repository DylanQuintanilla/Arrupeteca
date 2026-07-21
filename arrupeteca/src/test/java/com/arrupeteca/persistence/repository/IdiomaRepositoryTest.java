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
        assertThat(idiomaRepository.findByIdAndBorradoLogicoFalse(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("findByBorradoLogicoFalse - Should return page of entities")
    void findByBorradoLogicoFalse() {
        idiomaRepository.save(Idioma.builder().nombre("Test").build());
        Page<Idioma> page = idiomaRepository.findByBorradoLogicoFalse(PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("Should count physical entries including deleted")
    void shouldCountByIdIncludeDeleted() {
        Idioma saved = idiomaRepository.save(Idioma.builder().nombre("Idio3").borradoLogico(true).build());
        assertThat(idiomaRepository.countByIdIncludeDeleted(saved.getId())).isEqualTo(1L);
    }
}
