package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Edificio;
import com.arrupeteca.persistence.entity.Planta;
import com.arrupeteca.persistence.entity.Salon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SalonRepositoryTest {

    @Autowired
    private SalonRepository salonRepository;

    @Autowired
    private PlantaRepository plantaRepository;

    @Autowired
    private EdificioRepository edificioRepository;

    @Test
    @DisplayName("findByPlantaIdAndBorradoLogicoFalse - Should return page of entities")
    void findByPlantaIdAndBorradoLogicoFalse() {
        Edificio ed = edificioRepository.save(Edificio.builder().nombre("Edificio A").build());
        Planta p = plantaRepository.save(Planta.builder().nombre("Planta 1").edificio(ed).build());
        salonRepository.save(Salon.builder().nombre("Salon A").planta(p).build());
        Page<Salon> page = salonRepository.findByPlantaIdAndBorradoLogicoFalse(p.getId(), PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getNombre()).isEqualTo("Salon A");
    }
}
