package com.arrupeteca.persistence.repository;

import java.util.Set;

import com.arrupeteca.dto.response.summary.EjemplarLibroResumen;
import com.arrupeteca.persistence.entity.Categoria;
import com.arrupeteca.persistence.entity.Disponibilidad;
import com.arrupeteca.persistence.entity.Edificio;
import com.arrupeteca.persistence.entity.Editorial;
import com.arrupeteca.persistence.entity.EjemplarLibro;
import com.arrupeteca.persistence.entity.EstadoFisico;
import com.arrupeteca.persistence.entity.Formato;
import com.arrupeteca.persistence.entity.Genero;
import com.arrupeteca.persistence.entity.Idioma;
import com.arrupeteca.persistence.entity.Libro;
import com.arrupeteca.persistence.entity.Mueble;
import com.arrupeteca.persistence.entity.Obra;
import com.arrupeteca.persistence.entity.Planta;
import com.arrupeteca.persistence.entity.Salon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

class EjemplarLibroRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private EjemplarLibroRepository ejemplarLibroRepository;
    @Autowired
    private LibroRepository libroRepository;
    @Autowired
    private MuebleRepository muebleRepository;
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
    @Autowired
    private EdificioRepository edificioRepository;
    @Autowired
    private PlantaRepository plantaRepository;
    @Autowired
    private SalonRepository salonRepository;
    @Autowired
    private EstadoFisicoRepository estadoFisicoRepository;
    @Autowired
    private DisponibilidadRepository disponibilidadRepository;

    @Test
    @DisplayName("Should save and find ejemplar")
    void shouldSaveAndFind() {
        EjemplarLibro entity = createValidEjemplar("Test");
        EjemplarLibro saved = ejemplarLibroRepository.save(entity);
        assertThat(ejemplarLibroRepository.busquedaPorId(saved.getId(), false)).isPresent();
    }

    @Test
    @DisplayName("Should count physical entries including deleted")
    void shouldCountByIdIncludeDeleted() {
        EjemplarLibro entity = createValidEjemplar("Count");
        EjemplarLibro saved = ejemplarLibroRepository.save(entity);
        assertThat(ejemplarLibroRepository.existsById(saved.getId())).isTrue();
    }

    @Test
    @DisplayName("Should perform global search")
    void shouldSearchGlobal() {
        ejemplarLibroRepository.save(createValidEjemplar("Especial"));
        Page<EjemplarLibroResumen> page = ejemplarLibroRepository.busqueda(
                false, null, null, null, null, null, null, null, "Especial", PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isGreaterThan(0);
    }

    private EjemplarLibro createValidEjemplar(String comentario) {
        Genero genero = generoRepository.save(Genero.builder().nombre("Genero " + comentario).build());
        Categoria categoria = categoriaRepository.save(Categoria.builder().nombre("Categoria " + comentario).build());

        Obra obra = obraRepository.save(Obra.builder().titulo("Obra " + comentario).resumen("Resumen")
                .generos(Set.of(genero)).categorias(Set.of(categoria)).build());

        Editorial editorial = editorialRepository.save(Editorial.builder().nombre("Ed " + comentario)
                .correo(comentario + "@e.com").personaContacto("C").telefono("1234-5678").build());

        Formato formato = formatoRepository.save(Formato.builder().nombre("Formato " + comentario).build());
        Idioma idioma = idiomaRepository.save(Idioma.builder().nombre("Idioma " + comentario).build());

        Libro libro = libroRepository.save(Libro.builder()
                .isbn("9780306406157").anioPublicacion(2020).obra(obra).editorial(editorial)
                .formato(formato).idiomas(Set.of(idioma)).build());

        Edificio ed = edificioRepository.save(Edificio.builder().nombre("Ed " + comentario).build());
        Planta p = plantaRepository.save(Planta.builder().nombre("P " + comentario).edificio(ed).build());
        Salon s = salonRepository.save(Salon.builder().nombre("S " + comentario).planta(p).build());
        Mueble mueble = muebleRepository.save(Mueble.builder().nombre("Mueble " + comentario).salon(s).build());

        EstadoFisico ef = estadoFisicoRepository.save(EstadoFisico.builder().nombre("Buen estado " + comentario).build());
        Disponibilidad disp = disponibilidadRepository.save(Disponibilidad.builder().nombre("Disponible " + comentario).build());

        return EjemplarLibro.builder()
                .comentario(comentario)
                .libro(libro)
                .mueble(mueble)
                .estadoFisico(ef)
                .disponibilidad(disp)
                .build();
    }

}
