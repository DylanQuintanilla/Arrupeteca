package com.arrupeteca.service.impl;

import com.arrupeteca.mapper.TipoAutoriaMapper;
import com.arrupeteca.persistence.repository.TipoAutoriaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TipoAutoriaServiceImplTest extends BaseServiceTest {
    @Mock private TipoAutoriaRepository repository;
    @Mock private TipoAutoriaMapper mapper;
    @InjectMocks private TipoAutoriaServiceImpl service;

    @Test
    @DisplayName("Should return paginated active authorship types")
    void shouldReturnPaginatedActive() {
        Page<com.arrupeteca.persistence.projection.TipoAutoriaResumen> page = new PageImpl<>(List.of());
        when(repository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerTodosActivos(Pageable.unpaged())).isEqualTo(page);
    }
}
