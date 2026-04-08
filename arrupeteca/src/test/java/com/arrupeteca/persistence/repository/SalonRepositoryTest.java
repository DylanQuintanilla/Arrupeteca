package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Planta;
import com.arrupeteca.persistence.entity.Edificio;
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
        Salon s = createValidSalon("S1");
        salonRepository.save(s);
        assertThat(salonRepository.existsByNombreIgnoreCaseAndPlanta_Id("s1", s.getPlanta().getId())).isTrue();
    }

    @Test
    @DisplayName("Should find by planta")
    void shouldFindByPlanta() {
        Salon s = createValidSalon("S2");
        salonRepository.save(s);
        Page<SalonResumen> page = salonRepository.findProjectedByPlanta_IdAndBorradoLogicoFalse(s.getPlanta().getId(), PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    private Salon createValidSalon(String nombre) {
        Edificio ed = edificioRepository.save(Edificio.builder().nombre("Ed " + nombre).build());
        Planta p = plantaRepository.save(Planta.builder().nombre("P " + nombre).edificio(ed).build());
        return Salon.builder().nombre(nombre).planta(p).build();
    }
}
