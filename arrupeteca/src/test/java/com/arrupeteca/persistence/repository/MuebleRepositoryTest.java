package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Edificio;
import com.arrupeteca.persistence.entity.Mueble;
import com.arrupeteca.persistence.entity.Salon;
import com.arrupeteca.persistence.entity.Planta;
import com.arrupeteca.persistence.projection.MuebleResumen;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

class MuebleRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private MuebleRepository muebleRepository;
    @Autowired
    private SalonRepository salonRepository;
    @Autowired
    private PlantaRepository plantaRepository;
    @Autowired
    private EdificioRepository edificioRepository;

    @Test
    @DisplayName("Should save and find mueble")
    void shouldSaveAndFind() {
        Mueble entity = createValidMueble("Estante A");
        muebleRepository.save(entity);
        assertThat(muebleRepository.existsByNombreIgnoreCase("estante a")).isTrue();
    }

    @Test
    @DisplayName("Should perform global search")
    void shouldSearchGlobal() {
        muebleRepository.save(createValidMueble("Mueble Especial"));
        Page<MuebleResumen> page = muebleRepository.busquedaGlobal("Especial", PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isGreaterThan(0);
    }

    private Mueble createValidMueble(String nombre) {
        Edificio ed = edificioRepository.save(Edificio.builder().nombre("Ed " + nombre).build());
        Planta planta = plantaRepository.save(Planta.builder().nombre("Planta " + nombre).edificio(ed).build());
        Salon salon = salonRepository.save(Salon.builder().nombre("Salon " + nombre).planta(planta).build());
        return Mueble.builder().nombre(nombre).salon(salon).build();
    }
}
