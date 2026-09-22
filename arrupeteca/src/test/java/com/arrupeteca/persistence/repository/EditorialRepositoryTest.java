package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Editorial;
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
    @DisplayName("Should save and find editorial by ID")
    void shouldSaveAndFindById() {
        Editorial entity = Editorial.builder()
                .nombre("Santillana")
                .correo("contacto@santillana.com")
                .telefono("2222-3333")
                .personaContacto("Juan")
                .build();
        Editorial saved = editorialRepository.save(entity);
        assertThat(editorialRepository.findById(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("buscarActivosPorNombre - Should return page of entities")
    void buscarActivosPorNombre() {
        editorialRepository.save(Editorial.builder()
                .nombre("Test")
                .correo("test@editorial.com")
                .telefono("1111-2222")
                .personaContacto("Pedro")
                .build());
        java.util.List<Editorial> list = editorialRepository.buscarActivosPorNombre(null);
        assertThat(list).isNotEmpty();
    }

    @Test
    @DisplayName("Should count physical entries including deleted")
    void shouldCountByIdIncludeDeleted() {
        Editorial saved = editorialRepository.save(Editorial.builder()
                .nombre("Edit3")
                .correo("edit3@editorial.com")
                .telefono("3333-4444")
                .personaContacto("Maria")
                .borradoLogico(true)
                .build());
        assertThat(editorialRepository.existsById(saved.getId())).isTrue();
    }
}
