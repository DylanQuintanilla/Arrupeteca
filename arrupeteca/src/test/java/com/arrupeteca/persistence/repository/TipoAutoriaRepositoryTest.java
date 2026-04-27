package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.TipoAutoria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TipoAutoriaRepositoryTest {

    @Autowired
    private TipoAutoriaRepository tipoAutoriaRepository;

    @Test
    @DisplayName("findByBorradoLogicoFalse - Should return page of entities")
    void findByBorradoLogicoFalse() {
        tipoAutoriaRepository.save(TipoAutoria.builder().nombre("Test").build());
        Page<TipoAutoria> page = tipoAutoriaRepository.findByBorradoLogicoFalse(PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getNombre()).isEqualTo("Test");
    }
}
