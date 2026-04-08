package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Autor;
import com.arrupeteca.persistence.projection.AutorResumen;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

class AutorRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private AutorRepository autorRepository;

    @Test
    @DisplayName("Should save and find autor")
    void shouldSaveAndFind() {
        Autor autor = Autor.builder().nombre("Cervantes").biografia("Bio").build();
        autorRepository.save(autor);
        assertThat(autorRepository.existsByNombreIgnoreCase("cervantes")).isTrue();
    }

    @Test
    @DisplayName("Should search globally")
    void shouldSearchGlobal() {
        autorRepository.save(Autor.builder().nombre("Gabriel Garcia").biografia("Bio").build());
        Page<AutorResumen> page = autorRepository.busquedaGlobalAvanzada("Gabriel", null, null, PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isGreaterThan(0);
    }
}
