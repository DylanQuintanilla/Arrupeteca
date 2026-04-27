package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Categoria;
import com.arrupeteca.persistence.entity.Genero;
import com.arrupeteca.persistence.entity.Obra;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ObraRepositoryTest {

    @Autowired
    private ObraRepository obraRepository;

    @Autowired
    private GeneroRepository generoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    @DisplayName("findByTituloContainingIgnoreCase - Should find work by title")
    void findByTitulo() {
        Genero g = generoRepository.save(Genero.builder().nombre("Genero Test").build());
        Categoria c = categoriaRepository.save(Categoria.builder().nombre("Categoria Test").build());

        Obra obra = Obra.builder()
                .titulo("Cien años de soledad")
                .resumen("Un resumen muy largo y detallado para pasar la validacion.")
                .anioCreacion(1967)
                .generos(Set.of(g))
                .categorias(Set.of(c))
                .build();
        
        obra.setFechaCreacion(LocalDateTime.now());
        
        obraRepository.saveAndFlush(obra);

        Page<Obra> page = obraRepository.findByTituloContainingIgnoreCase("Cien", PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getTitulo()).isEqualTo("Cien años de soledad");
    }
}
