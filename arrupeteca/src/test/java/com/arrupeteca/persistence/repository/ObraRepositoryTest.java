package com.arrupeteca.persistence.repository;

import com.arrupeteca.dto.response.summary.ObraResumen;
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

import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class ObraRepositoryTest {

    @Autowired
    private ObraRepository obraRepository;

    @Autowired
    private GeneroRepository generoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    @DisplayName("busqueda - Should find work by title")
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

        Page<ObraResumen> page = obraRepository.busqueda(
                false, null, null, null, "Cien", null, null, PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).titulo()).isEqualTo("Cien años de soledad");
    }

    @Test
    @DisplayName("busquedaPorId - Should find work by id")
    void shouldFindById() {
        Obra obra = Obra.builder()
                .titulo("El Coronel no tiene quien le escriba")
                .resumen("Resumen corto")
                .anioCreacion(1961)
                .build();
        Obra saved = obraRepository.saveAndFlush(obra);

        assertThat(obraRepository.busquedaPorId(saved.getId(), false)).isPresent();
    }

    @Test
    @DisplayName("buscarDuplicados - Should find duplicate works by title term")
    void shouldFindDuplicates() {
        Obra obra = Obra.builder()
                .titulo("Don Quijote de la Mancha")
                .resumen("Resumen")
                .anioCreacion(1605)
                .build();
        Obra saved = obraRepository.saveAndFlush(obra);

        java.util.List<Obra> result = obraRepository.buscarDuplicados("Quijote", null, org.springframework.data.domain.Sort.by("titulo"));
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(saved.getId());

        java.util.List<Obra> resultExcluido = obraRepository.buscarDuplicados("Quijote", saved.getId(), org.springframework.data.domain.Sort.by("titulo"));
        assertThat(resultExcluido).isEmpty();
    }
}

