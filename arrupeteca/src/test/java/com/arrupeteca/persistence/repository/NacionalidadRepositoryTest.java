package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Nacionalidad;
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
        Nacionalidad entity = Nacionalidad.builder().nombre("Salvadoreña").build();
        Nacionalidad saved = nacionalidadRepository.save(entity);
        assertThat(nacionalidadRepository.findByIdAndBorradoLogicoFalse(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("findByBorradoLogicoFalse - Should return page of entities")
    void findByBorradoLogicoFalse() {
        nacionalidadRepository.save(Nacionalidad.builder().nombre("Test").build());
        Page<Nacionalidad> page = nacionalidadRepository.findByBorradoLogicoFalse(PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("Should count physical entries including deleted")
    void shouldCountByIdIncludeDeleted() {
        Nacionalidad saved = nacionalidadRepository.save(Nacionalidad.builder().nombre("Nac3").borradoLogico(true).build());
        assertThat(nacionalidadRepository.countByIdIncludeDeleted(saved.getId())).isEqualTo(1L);
    }
}
