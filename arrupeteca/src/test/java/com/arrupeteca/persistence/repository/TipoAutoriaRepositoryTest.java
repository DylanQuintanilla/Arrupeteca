package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.TipoAutoria;
import com.arrupeteca.persistence.projection.TipoAutoriaResumen;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

class TipoAutoriaRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private TipoAutoriaRepository tipoAutoriaRepository;

    @Test
    @DisplayName("Should save and find tipoAutoria by ID")
    void shouldSaveAndFindById() {
        TipoAutoria entity = TipoAutoria.builder().nombre("Ilustrador").build();
        TipoAutoria saved = tipoAutoriaRepository.save(entity);
        assertThat(tipoAutoriaRepository.findProjectedByIdAndBorradoLogicoFalse(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("Should find active with pagination")
    void shouldFindActiveWithPagination() {
        tipoAutoriaRepository.save(TipoAutoria.builder().nombre("T1").borradoLogico(false).build());
        Page<TipoAutoriaResumen> page = tipoAutoriaRepository.findByBorradoLogicoFalse(PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isGreaterThan(0);
    }
}
