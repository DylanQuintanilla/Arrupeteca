package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Nacionalidad;
import com.arrupeteca.persistence.projection.NacionalidadResumen;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

class NacionalidadRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private NacionalidadRepository nacionalidadRepository;

    @Test
    @DisplayName("Should save and find nacionalidad by ID")
    void shouldSaveAndFindById() {
        Nacionalidad entity = Nacionalidad.builder().nombre("Española").build();
        Nacionalidad saved = nacionalidadRepository.save(entity);
        assertThat(nacionalidadRepository.findProjectedByIdAndBorradoLogicoFalse(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("Should find active with pagination")
    void shouldFindActiveWithPagination() {
        nacionalidadRepository.save(Nacionalidad.builder().nombre("N1").borradoLogico(false).build());
        Page<NacionalidadResumen> page = nacionalidadRepository.findByBorradoLogicoFalse(PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isGreaterThan(0);
    }
}
