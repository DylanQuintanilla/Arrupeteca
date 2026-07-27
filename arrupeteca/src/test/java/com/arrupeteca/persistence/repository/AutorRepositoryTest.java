package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Autor;
import com.arrupeteca.persistence.entity.Pais;
import com.arrupeteca.persistence.entity.Nacionalidad;
import com.arrupeteca.persistence.entity.Genero;
import com.arrupeteca.persistence.entity.Obra;
import com.arrupeteca.persistence.specification.AutorSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import org.instancio.Instancio;
import static org.instancio.Select.field;

class AutorRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should save and find autor by name (existsByNombreIgnoreCase)")
    void shouldSaveAndFind() {
        Autor autor = Autor.builder().nombre("Cervantes").biografia("Bio").build();
        autorRepository.save(autor);
        assertThat(autorRepository.existsByNombreIgnoreCase("cervantes")).isTrue();
    }

    @Test
    @DisplayName("Should search globally with filters")
    void shouldSearchGlobal() {
        Autor autor = Instancio.of(Autor.class)
                .ignore(field(Autor::getId))
                .ignore(field(Autor::getVersion))
                .ignore(field(Autor::getPaisNacimiento))
                .ignore(field(Autor::getNacionalidad))
                .ignore(field(Autor::getGeneroPrincipal))
                .ignore(field(Autor::getObraCumbre))
                .set(field(Autor::getNombre), "Gabriel Garcia")
                .set(field(Autor::getBiografia), "Bio")
                .set(field(Autor::isBorradoLogico), false)
                .create();
        
        autorRepository.save(autor);
        Specification<Autor> spec = AutorSpecification.filtroBusquedaGlobal("Gabriel", null, null);
        Page<Autor> page = autorRepository.findAll(spec, PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should filter by country, nationality, and genre specifications")
    void shouldFilterByRelationsAndSpecs() {
        Pais pais = Pais.builder().nombre("Colombia").borradoLogico(false).build();
        Nacionalidad nac = Nacionalidad.builder().nombre("Colombiana").borradoLogico(false).build();
        Genero gen = Genero.builder().nombre("Realismo Magico").borradoLogico(false).build();

        entityManager.persist(pais);
        entityManager.persist(nac);
        entityManager.persist(gen);

        Autor autor = Autor.builder()
                .nombre("Gabriel")
                .biografia("Bio")
                .paisNacimiento(pais)
                .nacionalidad(nac)
                .generoPrincipal(gen)
                .build();
        autorRepository.save(autor);

        // Test tieneId
        assertThat(autorRepository.findAll(AutorSpecification.tieneId(autor.getId()), PageRequest.of(0, 1)).getTotalElements()).isEqualTo(1);
        assertThat(autorRepository.findAll(AutorSpecification.tieneId(null), PageRequest.of(0, 1)).getTotalElements()).isGreaterThan(0);

        // Test tienePais
        assertThat(autorRepository.findAll(AutorSpecification.tienePais(pais.getId()), PageRequest.of(0, 10)).getContent()).contains(autor);
        assertThat(autorRepository.findAll(AutorSpecification.tienePais(999L), PageRequest.of(0, 10)).getContent()).isEmpty();
        assertThat(autorRepository.findAll(AutorSpecification.tienePais(null), PageRequest.of(0, 10)).getContent()).isNotEmpty();

        // Test tieneNacionalidad
        assertThat(autorRepository.findAll(AutorSpecification.tieneNacionalidad(nac.getId()), PageRequest.of(0, 10)).getContent()).contains(autor);
        assertThat(autorRepository.findAll(AutorSpecification.tieneNacionalidad(999L), PageRequest.of(0, 10)).getContent()).isEmpty();
        assertThat(autorRepository.findAll(AutorSpecification.tieneNacionalidad(null), PageRequest.of(0, 10)).getContent()).isNotEmpty();

        // Test tieneGenero
        assertThat(autorRepository.findAll(AutorSpecification.tieneGenero(gen.getId()), PageRequest.of(0, 10)).getContent()).contains(autor);
        assertThat(autorRepository.findAll(AutorSpecification.tieneGenero(999L), PageRequest.of(0, 10)).getContent()).isEmpty();
        assertThat(autorRepository.findAll(AutorSpecification.tieneGenero(null), PageRequest.of(0, 10)).getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("Should test vivoEnPeriodo specification with intersection logic")
    void shouldFilterByLifePeriod() {
        // Cervantes: 1547 - 1616
        Autor cervantes = Autor.builder()
                .nombre("Miguel de Cervantes")
                .biografia("Bio")
                .anioNacimiento(Year.of(1547))
                .anioFallecimiento(Year.of(1616))
                .build();

        // Shakespeare: 1564 - 1616
        Autor shakespeare = Autor.builder()
                .nombre("William Shakespeare")
                .biografia("Bio")
                .anioNacimiento(Year.of(1564))
                .anioFallecimiento(Year.of(1616))
                .build();

        // SigueVivo: 1980 - null
        Autor sigueVivo = Autor.builder()
                .nombre("Autor Joven")
                .biografia("Bio")
                .anioNacimiento(Year.of(1980))
                .anioFallecimiento(null)
                .build();

        autorRepository.saveAll(List.of(cervantes, shakespeare, sigueVivo));

        // Period 1500 - 1550: should match only Cervantes (birth 1547 <= 1550 AND death 1616 >= 1500)
        List<Autor> p1 = autorRepository.findAll(AutorSpecification.vivoEnPeriodo(Year.of(1500), Year.of(1550)));
        assertThat(p1).contains(cervantes).doesNotContain(shakespeare, sigueVivo);

        // Period 1560 - 1620: should match Cervantes and Shakespeare
        List<Autor> p2 = autorRepository.findAll(AutorSpecification.vivoEnPeriodo(Year.of(1560), Year.of(1620)));
        assertThat(p2).contains(cervantes, shakespeare).doesNotContain(sigueVivo);

        // Period 1990 - 2020: should match sigueVivo (birth 1980 <= 2020 AND death is null)
        List<Autor> p3 = autorRepository.findAll(AutorSpecification.vivoEnPeriodo(Year.of(1990), Year.of(2020)));
        assertThat(p3).contains(sigueVivo).doesNotContain(cervantes, shakespeare);

        // Period null - null: should return all
        List<Autor> p4 = autorRepository.findAll(AutorSpecification.vivoEnPeriodo(null, null));
        assertThat(p4).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    @DisplayName("Should find duplicate authors using specification search")
    void shouldFindDuplicates() {
        Autor gabriel1 = Autor.builder().nombre("Gabriel Garcia").seudonimo("Gabo").biografia("Bio").build();
        Autor gabriel2 = Autor.builder().nombre("Gabriel Garcia Marquez").biografia("Bio").build();
        autorRepository.saveAll(List.of(gabriel1, gabriel2));

        List<Autor> duplicates = autorRepository.findAll(AutorSpecification.posiblesDuplicados("Gabriel", null), Sort.by("nombre"));
        assertThat(duplicates).contains(gabriel1, gabriel2);

        // Test exclusion ID
        List<Autor> duplicatesExcludingOne = autorRepository.findAll(AutorSpecification.posiblesDuplicados("Gabriel", gabriel1.getId()), Sort.by("nombre"));
        assertThat(duplicatesExcludingOne).contains(gabriel2).doesNotContain(gabriel1);
    }

    @Test
    @DisplayName("Should count physical entries and change logical status")
    void shouldCountPhysicalAndDeactivateActivate() {
        Autor autor = Autor.builder().nombre("Test Status").biografia("Bio").borradoLogico(false).build();
        autorRepository.saveAndFlush(autor);

        long count = autorRepository.countByIdIncludeDeleted(autor.getId());
        assertThat(count).isEqualTo(1L);

        // Deactivate
        autorRepository.desactivar(autor.getId());
        entityManager.clear();
        Autor deactivated = autorRepository.findById(autor.getId()).orElseThrow();
        assertThat(deactivated.isBorradoLogico()).isTrue();

        // Activate
        autorRepository.activar(autor.getId());
        entityManager.clear();
        Autor activated = autorRepository.findById(autor.getId()).orElseThrow();
        assertThat(activated.isBorradoLogico()).isFalse();
    }
}
