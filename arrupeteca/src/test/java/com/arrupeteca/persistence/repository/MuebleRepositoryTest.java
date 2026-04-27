package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Edificio;
import com.arrupeteca.persistence.entity.Mueble;
import com.arrupeteca.persistence.entity.Planta;
import com.arrupeteca.persistence.entity.Salon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MuebleRepositoryTest {

    @Autowired
    private MuebleRepository muebleRepository;

    @Autowired
    private SalonRepository salonRepository;

    @Autowired
    private PlantaRepository plantaRepository;

    @Autowired
    private EdificioRepository edificioRepository;

    private Salon salon;

    @BeforeEach
    void setUp() {
        Edificio ed = edificioRepository.save(Edificio.builder().nombre("Edificio A").build());
        Planta p = plantaRepository.save(Planta.builder().nombre("Planta 1").edificio(ed).build());
        salon = salonRepository.save(Salon.builder().nombre("Salon A").planta(p).build());
    }

    @Test
    @DisplayName("findByNombreContainingIgnoreCase - Should find furniture by name")
    void findByNombre() {
        muebleRepository.save(Mueble.builder().nombre("Especial").salon(salon).build());
        Page<Mueble> page = muebleRepository.findByNombreContainingIgnoreCase("Especial", PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getNombre()).isEqualTo("Especial");
    }

    @Test
    @DisplayName("findProjectedByIdAndBorradoLogicoFalse - Should return optional entity")
    void findProjectedById() {
        Mueble saved = muebleRepository.save(Mueble.builder().nombre("Mueble 1").salon(salon).build());
        Optional<Mueble> found = muebleRepository.findProjectedByIdAndBorradoLogicoFalse(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getNombre()).isEqualTo("Mueble 1");
    }
}
