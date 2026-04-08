package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Pais;
import com.arrupeteca.persistence.projection.PaisResumen;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

class PaisRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private PaisRepository paisRepository;

    @Test
    @DisplayName("Should save and find pais by ID")
    void shouldSaveAndFindById() {
        Pais entity = Pais.builder().nombre("Francia").build();
        Pais saved = paisRepository.save(entity);
        assertThat(paisRepository.findProjectedByIdAndBorradoLogicoFalse(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("Should find active with pagination")
    void shouldFindActiveWithPagination() {
        paisRepository.save(Pais.builder().nombre("P1").borradoLogico(false).build());
        Page<PaisResumen> page = paisRepository.findByBorradoLogicoFalse(PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isGreaterThan(0);
    }
}
