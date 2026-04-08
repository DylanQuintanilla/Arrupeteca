package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Editorial;
import com.arrupeteca.persistence.projection.EditorialResumen;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

class EditorialRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private EditorialRepository editorialRepository;

    @Test
    @DisplayName("Should save and find editorial")
    void shouldSaveAndFind() {
        Editorial ed = Editorial.builder()
                .nombre("Arrupe Ed")
                .correo("info@arrupe.com")
                .personaContacto("Admin")
                .telefono("1234-5678")
                .build();
        editorialRepository.save(ed);
        assertThat(editorialRepository.existsByNombreIgnoreCase("arrupe ed")).isTrue();
    }

    @Test
    @DisplayName("Should perform master search")
    void shouldPerformMasterSearch() {
        editorialRepository.save(Editorial.builder().nombre("Planeta").correo("p@p.com").personaContacto("C").telefono("1234-5678").borradoLogico(false).build());
        editorialRepository.save(Editorial.builder().nombre("Oveja").correo("o@o.com").personaContacto("C").telefono("1234-5678").borradoLogico(true).build());

        Page<EditorialResumen> page = editorialRepository.busquedaMaestraAdmin(false, "Plan", PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(1);
    }
}
