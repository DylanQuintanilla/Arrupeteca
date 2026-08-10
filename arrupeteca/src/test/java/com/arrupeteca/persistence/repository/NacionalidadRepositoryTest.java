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
        assertThat(nacionalidadRepository.findById(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("buscarActivosPorNombre - Should return page of entities")
    void buscarActivosPorNombre() {
        nacionalidadRepository.save(Nacionalidad.builder().nombre("Test").build());
        java.util.List<Nacionalidad> list = nacionalidadRepository.buscarActivosPorNombre(null);
        assertThat(list).isNotEmpty();
    }

    @Test
    @DisplayName("Should count physical entries including deleted")
    void shouldCountByIdIncludeDeleted() {
        Nacionalidad saved = nacionalidadRepository.save(Nacionalidad.builder().nombre("Nac3").borradoLogico(true).build());
        assertThat(nacionalidadRepository.existsById(saved.getId())).isTrue();
    }
}
