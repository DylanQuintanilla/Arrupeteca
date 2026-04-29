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
        Formato entity = Formato.builder().nombre("PDF").build();
        Formato saved = formatoRepository.save(entity);
        assertThat(formatoRepository.findProjectedByIdAndBorradoLogicoFalse(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("Should find active with pagination")
    void shouldFindActiveWithPagination() {
        formatoRepository.save(Formato.builder().nombre("F1").borradoLogico(false).build());
        Page<Formato> page = formatoRepository.findByBorradoLogicoFalse(PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isGreaterThan(0);
    }
}
