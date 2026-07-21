package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Edificio;
import com.arrupeteca.persistence.entity.Planta;
import com.arrupeteca.persistence.entity.Salon;
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
    @DisplayName("Should save and find salon by ID")
    void shouldSaveAndFindById() {
        Edificio edif = edificioRepository.save(Edificio.builder().nombre("Edif1").build());
        Planta planta = plantaRepository.save(Planta.builder().nombre("Planta 1").edificio(edif).build());
        Salon entity = Salon.builder().nombre("Salon 1").planta(planta).build();
        Salon saved = salonRepository.save(entity);
        assertThat(salonRepository.findByIdAndBorradoLogicoFalse(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("findByBorradoLogicoFalse - Should return page of entities")
    void findByBorradoLogicoFalse() {
        Edificio edif = edificioRepository.save(Edificio.builder().nombre("Edif2").build());
        Planta planta = plantaRepository.save(Planta.builder().nombre("Planta 2").edificio(edif).build());
        salonRepository.save(Salon.builder().nombre("Test").planta(planta).build());
        Page<Salon> page = salonRepository.findByBorradoLogicoFalse(PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("Should count physical entries including deleted")
    void shouldCountByIdIncludeDeleted() {
        Edificio edif = edificioRepository.save(Edificio.builder().nombre("Edif3").build());
        Planta planta = plantaRepository.save(Planta.builder().nombre("Planta 3").edificio(edif).build());
        Salon saved = salonRepository.save(Salon.builder().nombre("Salon3").planta(planta).borradoLogico(true).build());
        assertThat(salonRepository.countByIdIncludeDeleted(saved.getId())).isEqualTo(1L);
    }
}
