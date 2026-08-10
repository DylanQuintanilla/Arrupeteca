package com.arrupeteca.persistence.repository;

import com.arrupeteca.dto.response.summary.AutorResumenResponse;
import com.arrupeteca.persistence.entity.Autor;
import com.arrupeteca.persistence.entity.Pais;
import com.arrupeteca.persistence.entity.Nacionalidad;
import com.arrupeteca.persistence.entity.Genero;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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
    @DisplayName("Should search globally with projected query")
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
        Page<AutorResumenResponse> page = autorRepository.busquedaMaestraAdminProyectada(
                false, null, null, null, null, null, null, "Gabriel", PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should find duplicate authors using buscarDuplicados")
    void shouldFindDuplicates() {
        Autor gabriel1 = Autor.builder().nombre("Gabriel Garcia").seudonimo("Gabo").biografia("Bio").build();
        Autor gabriel2 = Autor.builder().nombre("Gabriel Garcia Marquez").biografia("Bio").build();
        autorRepository.saveAll(List.of(gabriel1, gabriel2));

        List<Autor> duplicates = autorRepository.buscarDuplicados("Gabriel", null, Sort.by("nombre"));
        assertThat(duplicates).extracting(Autor::getNombre).contains("Gabriel Garcia", "Gabriel Garcia Marquez");

        // Test exclusion ID
        List<Autor> duplicatesExcludingOne = autorRepository.buscarDuplicados("Gabriel", gabriel1.getId(), Sort.by("nombre"));
        assertThat(duplicatesExcludingOne).extracting(Autor::getNombre).contains("Gabriel Garcia Marquez").doesNotContain("Gabriel Garcia");
    }

    @Test
    @DisplayName("Should count physical entries and change logical status")
    void shouldCountPhysicalAndDeactivateActivate() {
        Autor autor = Autor.builder().nombre("Test Status").biografia("Bio").borradoLogico(false).build();
        autorRepository.saveAndFlush(autor);

        boolean count = autorRepository.existsById(autor.getId());
        assertThat(count).isTrue();

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
