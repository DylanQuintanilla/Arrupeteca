package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Edificio;
import com.arrupeteca.persistence.entity.Planta;
import com.arrupeteca.persistence.entity.Salon;
import com.arrupeteca.persistence.projection.SalonResumen;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

class SalonRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private SalonRepository salonRepository;

    @Autowired
    private PlantaRepository plantaRepository;

    @Autowired
    private EdificioRepository edificioRepository;

    @Test
    @DisplayName("Should save and find salon")
    void shouldSaveAndFind() {
        Edificio ed = edificioRepository.save(Edificio.builder().nombre("ed3").build());
        Planta p = plantaRepository.save(Planta.builder().nombre("p3").edificio(ed).build());
        Salon s = salonRepository.save(Salon.builder().nombre("s1").planta(p).build());
        assertThat(salonRepository.findById(s.getId())).isPresent();
        assertThat(salonRepository.qExistePorNombreYPlantaId("s1", p.getId())).isTrue();
    }

    @Test
    @DisplayName("Should find by planta")
    void shouldFindByPlanta() {
        Edificio ed = edificioRepository.save(Edificio.builder().nombre("ed4").build());
        Planta p = plantaRepository.save(Planta.builder().nombre("p4").edificio(ed).build());
        salonRepository.save(Salon.builder().nombre("s2").planta(p).build());
        Page<SalonResumen> page = salonRepository.qBuscarPorPlantaIdYActivo(p.getId(), PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
    }

}
