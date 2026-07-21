package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.TipoAutoria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

class TipoAutoriaRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private TipoAutoriaRepository tipoAutoriaRepository;

    @Test
    @DisplayName("Should save and find tipoAutoria by ID")
    void shouldSaveAndFindById() {
        TipoAutoria entity = TipoAutoria.builder().nombre("Principal").build();
        TipoAutoria saved = tipoAutoriaRepository.save(entity);
        assertThat(tipoAutoriaRepository.findByIdAndBorradoLogicoFalse(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("findByBorradoLogicoFalse - Should return page of entities")
    void findByBorradoLogicoFalse() {
        tipoAutoriaRepository.save(TipoAutoria.builder().nombre("Coautor").build());
        Page<TipoAutoria> page = tipoAutoriaRepository.findByBorradoLogicoFalse(PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("Should count physical entries including deleted")
    void shouldCountByIdIncludeDeleted() {
        TipoAutoria saved = tipoAutoriaRepository.save(TipoAutoria.builder().nombre("Traductor").borradoLogico(true).build());
        assertThat(tipoAutoriaRepository.countByIdIncludeDeleted(saved.getId())).isEqualTo(1L);
    }
}
