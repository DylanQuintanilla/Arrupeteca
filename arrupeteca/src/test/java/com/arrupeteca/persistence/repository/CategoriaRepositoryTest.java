package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Categoria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
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
        Optional<Categoria> found = categoriaRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getNombre()).isEqualTo("Novela");
    }

    @Test
    @DisplayName("Should find active categories list ordered by name")
    void shouldFindActiveCategoriesList() {
        categoriaRepository.save(Categoria.builder().nombre("Cat 2").borradoLogico(false).build());
        categoriaRepository.save(Categoria.builder().nombre("Cat 1").borradoLogico(false).build());
        categoriaRepository.save(Categoria.builder().nombre("Cat 3").borradoLogico(true).build());

        List<Categoria> list = categoriaRepository.buscarActivosPorNombre(null);

        assertThat(list).hasSize(2);
        assertThat(list).extracting(Categoria::getNombre)
                .containsExactly("Cat 1", "Cat 2");
    }

    @Test
    @DisplayName("Should check if name exists")
    void shouldCheckIfNombreExists() {
        categoriaRepository.save(Categoria.builder().nombre("Unico").build());

        assertThat(categoriaRepository.existsByNombreIgnoreCase("unico")).isTrue();
        assertThat(categoriaRepository.existsByNombreIgnoreCase("Otro")).isFalse();
    }
}
