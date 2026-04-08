package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Categoria;
import com.arrupeteca.persistence.projection.CategoriaResumen;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CategoriaRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    @DisplayName("Should save and find category by ID")
    void shouldSaveAndFindById() {
        Categoria categoria = Categoria.builder()
                .nombre("Novela")
                .borradoLogico(false)
                .build();

        Categoria saved = categoriaRepository.save(categoria);
        Optional<CategoriaResumen> found = categoriaRepository.findProjectedByIdAndBorradoLogicoFalse(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getNombre()).isEqualTo("Novela");
    }

    @Test
    @DisplayName("Should find active categories with pagination")
    void shouldFindActiveCategoriesWithPagination() {
        categoriaRepository.save(Categoria.builder().nombre("Cat 1").borradoLogico(false).build());
        categoriaRepository.save(Categoria.builder().nombre("Cat 2").borradoLogico(false).build());
        categoriaRepository.save(Categoria.builder().nombre("Cat 3").borradoLogico(true).build());

        Page<CategoriaResumen> page = categoriaRepository.findByBorradoLogicoFalse(PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).extracting(CategoriaResumen::getNombre)
                .containsExactlyInAnyOrder("Cat 1", "Cat 2");
    }

    @Test
    @DisplayName("Should check if name exists")
    void shouldCheckIfNombreExists() {
        categoriaRepository.save(Categoria.builder().nombre("Unico").build());

        assertThat(categoriaRepository.existsByNombreIgnoreCase("unico")).isTrue();
        assertThat(categoriaRepository.existsByNombreIgnoreCase("Otro")).isFalse();
    }
}
