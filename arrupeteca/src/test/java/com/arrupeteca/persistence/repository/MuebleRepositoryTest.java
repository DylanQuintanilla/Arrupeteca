package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Edificio;
import com.arrupeteca.persistence.entity.Mueble;
import com.arrupeteca.persistence.entity.Planta;
import com.arrupeteca.persistence.entity.Salon;
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
    @DisplayName("Should save and find mueble by ID")
    void shouldSaveAndFindById() {
        Edificio edif = edificioRepository.save(Edificio.builder().nombre("Edif1").build());
        Planta planta = plantaRepository.save(Planta.builder().nombre("Planta 1").edificio(edif).build());
        Salon salon = salonRepository.save(Salon.builder().nombre("Salon 1").planta(planta).build());
        Mueble entity = Mueble.builder().nombre("Mueble 1").salon(salon).build();
        Mueble saved = muebleRepository.save(entity);
        assertThat(muebleRepository.findById(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("buscarActivosPorNombre - Should return page of entities")
    void buscarActivosPorNombre() {
        Edificio edif = edificioRepository.save(Edificio.builder().nombre("Edif2").build());
        Planta planta = plantaRepository.save(Planta.builder().nombre("Planta 2").edificio(edif).build());
        Salon salon = salonRepository.save(Salon.builder().nombre("Salon 2").planta(planta).build());
        muebleRepository.save(Mueble.builder().nombre("Test").salon(salon).build());
        java.util.List<Mueble> list = muebleRepository.buscarActivosPorNombre(null);
        assertThat(list).isNotEmpty();
    }

    @Test
    @DisplayName("Should count physical entries including deleted")
    void shouldCountByIdIncludeDeleted() {
        Edificio edif = edificioRepository.save(Edificio.builder().nombre("Edif3").build());
        Planta planta = plantaRepository.save(Planta.builder().nombre("Planta 3").edificio(edif).build());
        Salon salon = salonRepository.save(Salon.builder().nombre("Salon 3").planta(planta).build());
        Mueble saved = muebleRepository.save(Mueble.builder().nombre("Mueble3").salon(salon).borradoLogico(true).build());
        assertThat(muebleRepository.existsById(saved.getId())).isTrue();
    }
}
