package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Edificio;
import com.arrupeteca.persistence.entity.Planta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PlantaRepositoryTest {

    @Autowired
    private PlantaRepository plantaRepository;

    @Autowired
    private EdificioRepository edificioRepository;

    @Test
    @DisplayName("findByEdificioIdAndBorradoLogicoFalse - Should return page of entities")
    void findByEdificioIdAndBorradoLogicoFalse() {
        Edificio ed = edificioRepository.save(Edificio.builder().nombre("Edificio A").build());
        plantaRepository.save(Planta.builder().nombre("Planta 1").edificio(ed).build());
        Page<Planta> page = plantaRepository.findByEdificioIdAndBorradoLogicoFalse(ed.getId(), PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getNombre()).isEqualTo("Planta 1");
    }
}
