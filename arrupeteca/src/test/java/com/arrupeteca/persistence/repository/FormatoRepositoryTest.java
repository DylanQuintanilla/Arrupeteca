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
        assertThat(formatoRepository.findByIdAndBorradoLogicoFalse(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("findByBorradoLogicoFalse - Should return page of entities")
    void findByBorradoLogicoFalse() {
        formatoRepository.save(Formato.builder().nombre("Test").build());
        Page<Formato> page = formatoRepository.findByBorradoLogicoFalse(PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("Should count physical entries including deleted")
    void shouldCountByIdIncludeDeleted() {
        Formato saved = formatoRepository.save(Formato.builder().nombre("Form3").borradoLogico(true).build());
        assertThat(formatoRepository.countByIdIncludeDeleted(saved.getId())).isEqualTo(1L);
    }
}
