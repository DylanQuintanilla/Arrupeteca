package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.*;
import com.arrupeteca.persistence.enums.Disponibilidad;
import com.arrupeteca.persistence.enums.EstadoFisico;
import com.arrupeteca.persistence.projection.EjemplarLibroResumen;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Set;

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

    @Test
    @DisplayName("Should save and find ejemplar")
    void shouldSaveAndFind() {
        EjemplarLibro entity = createValidEjemplar("Test");
        EjemplarLibro saved = ejemplarLibroRepository.save(entity);
        assertThat(ejemplarLibroRepository.findProjectedByIdAndBorradoLogicoFalse(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("Should perform global search")
    void shouldSearchGlobal() {
        ejemplarLibroRepository.save(createValidEjemplar("Especial"));
        Page<EjemplarLibroResumen> page = ejemplarLibroRepository.busquedaGlobal("Especial", PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isGreaterThan(0);
    }

    private EjemplarLibro createValidEjemplar(String comentario) {
        Genero genero = generoRepository.save(Genero.builder().nombre("Genero " + comentario).build());
        Categoria categoria = categoriaRepository.save(Categoria.builder().nombre("Categoria " + comentario).build());
        Obra obra = obraRepository.save(Obra.builder().titulo("Obra " + comentario).resumen("Resumen").generos(Set.of(genero)).categorias(Set.of(categoria)).build());
        Editorial editorial = editorialRepository.save(Editorial.builder().nombre("Ed " + comentario).correo(comentario + "@e.com").personaContacto("C").telefono("1234-5678").build());
        Formato formato = formatoRepository.save(Formato.builder().nombre("Formato " + comentario).build());
        Idioma idioma = idiomaRepository.save(Idioma.builder().nombre("Idioma " + comentario).build());

        Libro libro = libroRepository.save(Libro.builder()
                .isbn("9780306406157").anioPublicacion(2020).obra(obra).editorial(editorial).formato(formato).idiomas(Set.of(idioma)).build());

        Edificio ed = edificioRepository.save(Edificio.builder().nombre("Ed " + comentario).build());
        Planta p = plantaRepository.save(Planta.builder().nombre("P " + comentario).edificio(ed).build());
        Salon s = salonRepository.save(Salon.builder().nombre("S " + comentario).planta(p).build());
        Mueble mueble = muebleRepository.save(Mueble.builder().nombre("Mueble " + comentario).salon(s).build());

        return EjemplarLibro.builder()
                .comentario(comentario)
                .libro(libro)
                .mueble(mueble)
                .estadoFisico(EstadoFisico.BUEN_ESTADO)
                .disponibilidad(Disponibilidad.DISPONIBLE)
                .build();
    }
}
