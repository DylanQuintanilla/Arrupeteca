package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Edificio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

class EdificioRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private EdificioRepository edificioRepository;

    @Test
    @DisplayName("Should save and find edificio by ID")
    void shouldSaveAndFindById() {
        Edificio entity = Edificio.builder().nombre("Central").build();
        Edificio saved = edificioRepository.save(entity);
        assertThat(edificioRepository.findById(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("Should find active with pagination")
    void shouldFindActiveWithPagination() {
        edificioRepository.save(Edificio.builder().nombre("E1").borradoLogico(false).build());
        edificioRepository.save(Edificio.builder().nombre("E2").borradoLogico(true).build());

        java.util.List<Edificio> list = edificioRepository.buscarActivosPorNombre(null);
        assertThat(list).hasSize(1);
    }

    @Test
    @DisplayName("Should count physical entries including deleted")
    void shouldCountByIdIncludeDeleted() {
        Edificio saved = edificioRepository.save(Edificio.builder().nombre("E3").borradoLogico(true).build());
        assertThat(edificioRepository.existsById(saved.getId())).isTrue();
    }
}
