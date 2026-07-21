package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Pais;
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
        Pais entity = Pais.builder().nombre("El Salvador").build();
        Pais saved = paisRepository.save(entity);
        assertThat(paisRepository.findByIdAndBorradoLogicoFalse(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("findByBorradoLogicoFalse - Should return page of entities")
    void findByBorradoLogicoFalse() {
        paisRepository.save(Pais.builder().nombre("Test").build());
        Page<Pais> page = paisRepository.findByBorradoLogicoFalse(PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("Should count physical entries including deleted")
    void shouldCountByIdIncludeDeleted() {
        Pais saved = paisRepository.save(Pais.builder().nombre("Pais3").borradoLogico(true).build());
        assertThat(paisRepository.countByIdIncludeDeleted(saved.getId())).isEqualTo(1L);
    }
}
