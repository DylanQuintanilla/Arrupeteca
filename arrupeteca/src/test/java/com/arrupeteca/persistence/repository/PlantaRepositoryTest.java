package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Edificio;
import com.arrupeteca.persistence.entity.Planta;
import com.arrupeteca.persistence.projection.PlantaResumen;
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
    @DisplayName("Should save and find planta")
    void shouldSaveAndFind() {
        Edificio ed = edificioRepository.save(Edificio.builder().nombre("ed1").build());
        Planta p = plantaRepository.save(Planta.builder().nombre("p1").edificio(ed).build());
        assertThat(plantaRepository.findById(p.getId())).isPresent();
        assertThat(plantaRepository.qExistePorNombreYEdificioId("p1", ed.getId())).isTrue();
    }

    @Test
    @DisplayName("Should find by edificio")
    void shouldFindByEdificio() {
        Edificio ed = edificioRepository.save(Edificio.builder().nombre("ed2").build());
        Planta p = plantaRepository.save(Planta.builder().nombre("p2").edificio(ed).build());
        Page<PlantaResumen> page = plantaRepository.qBuscarPorEdificioIdYActivo(ed.getId(), PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
    }

}
