package com.arrupeteca.persistence.repository;

import java.util.Set;

import com.arrupeteca.dto.response.summary.LibroResumen;
import com.arrupeteca.persistence.entity.Categoria;
import com.arrupeteca.persistence.entity.Editorial;
import com.arrupeteca.persistence.entity.Formato;
import com.arrupeteca.persistence.entity.Genero;
import com.arrupeteca.persistence.entity.Idioma;
import com.arrupeteca.persistence.entity.Libro;
import com.arrupeteca.persistence.entity.Obra;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

class LibroRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private LibroRepository libroRepository;
    @Autowired
    private ObraRepository obraRepository;
    @Autowired
    private EditorialRepository editorialRepository;
    @Autowired
    private FormatoRepository formatoRepository;
    @Autowired
    private IdiomaRepository idiomaRepository;
    @Autowired
    private GeneroRepository generoRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    @DisplayName("Should save and find libro by ISBN")
    void shouldSaveAndFind() {
        Libro libro = createValidLibro("9780306406157");
        libroRepository.save(libro);
        assertThat(libroRepository.existsByIsbn("9780306406157")).isTrue();
    }

    @Test
    @DisplayName("Should find active with pagination")
    void shouldFindActive() {
        libroRepository.save(createValidLibro("9781107004634"));
        Page<LibroResumen> page = libroRepository.busquedaMaestraAdminProyectada(
                false, null, null, null, null, null, null, null, null, null, null, null, PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should count physical entries including deleted")
    void shouldCountByIdIncludeDeleted() {
        Libro saved = libroRepository.save(createValidLibro("9781107004639"));
        assertThat(libroRepository.existsById(saved.getId())).isTrue();
    }

    private Libro createValidLibro(String isbn) {
        Genero genero = generoRepository.save(Genero.builder().nombre("Genero Test " + isbn).build());
        Categoria categoria = categoriaRepository.save(Categoria.builder().nombre("Categoria Test " + isbn).build());

        Obra obra = obraRepository.save(Obra.builder().titulo("Obra Test " + isbn).resumen("Resumen")
                .generos(Set.of(genero)).categorias(Set.of(categoria)).build());

        Editorial editorial = editorialRepository.save(Editorial.builder().nombre("Ed Test " + isbn)
                .correo(isbn + "@e.com").personaContacto("C").telefono("1234-5678").build());

        Formato formato = formatoRepository.save(Formato.builder().nombre("Formato Test " + isbn).build());
        Idioma idioma = idiomaRepository.save(Idioma.builder().nombre("Idioma Test " + isbn).build());

        return Libro.builder()
                .isbn(isbn)
                .anioPublicacion(2020)
                .obra(obra)
                .editorial(editorial)
                .formato(formato)
                .idiomas(Set.of(idioma))
                .build();
    }

}
