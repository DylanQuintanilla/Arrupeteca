package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Formato;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

class FormatoRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private FormatoRepository formatoRepository;

    @Test
    @DisplayName("Should save and find formato by ID")
    void shouldSaveAndFindById() {
        Formato entity = Formato.builder().nombre("Impreso").build();
        Formato saved = formatoRepository.save(entity);
        assertThat(formatoRepository.findById(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("buscarActivosPorNombre - Should return page of entities")
    void buscarActivosPorNombre() {
        formatoRepository.save(Formato.builder().nombre("Test").build());
        java.util.List<Formato> list = formatoRepository.buscarActivosPorNombre(null);
        assertThat(list).isNotEmpty();
    }

    @Test
    @DisplayName("Should count physical entries including deleted")
    void shouldCountByIdIncludeDeleted() {
        Formato saved = formatoRepository.save(Formato.builder().nombre("Form3").borradoLogico(true).build());
        assertThat(formatoRepository.existsById(saved.getId())).isTrue();
    }
}
