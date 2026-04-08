package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Categoria;
import com.arrupeteca.persistence.entity.Genero;
import com.arrupeteca.persistence.entity.Obra;
import com.arrupeteca.persistence.projection.ObraResumen;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ObraRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ObraRepository obraRepository;
    @Autowired
    private GeneroRepository generoRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    @DisplayName("Should save and find obra")
    void shouldSaveAndFind() {
        Obra obra = createValidObra("Quijote");
        obraRepository.save(obra);
        assertThat(obraRepository.existsByTituloIgnoreCase("quijote")).isTrue();
    }

    @Test
    @DisplayName("Should perform global search")
    void shouldSearchGlobal() {
        obraRepository.save(createValidObra("Cien años de soledad"));
        Page<ObraResumen> page = obraRepository.busquedaGlobal("Cien", PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isGreaterThan(0);
    }

    private Obra createValidObra(String titulo) {
        Genero genero = generoRepository.save(Genero.builder().nombre("Genero " + titulo).build());
        Categoria categoria = categoriaRepository.save(Categoria.builder().nombre("Categoria " + titulo).build());

        return Obra.builder()
                .titulo(titulo)
                .resumen("Resumen de la obra")
                .generos(Set.of(genero))
                .categorias(Set.of(categoria))
                .build();
    }
}
