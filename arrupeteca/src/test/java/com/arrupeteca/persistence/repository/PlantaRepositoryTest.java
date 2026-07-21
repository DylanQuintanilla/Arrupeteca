package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Edificio;
import com.arrupeteca.persistence.entity.Planta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

class PlantaRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private PlantaRepository plantaRepository;

    @Autowired
    private EdificioRepository edificioRepository;

    @Test
    @DisplayName("Should save and find planta by ID")
    void shouldSaveAndFindById() {
        Edificio edif = edificioRepository.save(Edificio.builder().nombre("Edif1").build());
        Planta entity = Planta.builder().nombre("Planta 1").edificio(edif).build();
        Planta saved = plantaRepository.save(entity);
        assertThat(plantaRepository.findByIdAndBorradoLogicoFalse(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("findByBorradoLogicoFalse - Should return page of entities")
    void findByBorradoLogicoFalse() {
        Edificio edif = edificioRepository.save(Edificio.builder().nombre("Edif2").build());
        plantaRepository.save(Planta.builder().nombre("Test").edificio(edif).build());
        Page<Planta> page = plantaRepository.findByBorradoLogicoFalse(PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("Should count physical entries including deleted")
    void shouldCountByIdIncludeDeleted() {
        Edificio edif = edificioRepository.save(Edificio.builder().nombre("Edif3").build());
        Planta saved = plantaRepository.save(Planta.builder().nombre("Planta3").edificio(edif).borradoLogico(true).build());
        assertThat(plantaRepository.countByIdIncludeDeleted(saved.getId())).isEqualTo(1L);
    }
}
