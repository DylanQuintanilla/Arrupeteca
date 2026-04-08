package com.arrupeteca.service.impl;

import com.arrupeteca.mapper.FormatoMapper;
import com.arrupeteca.persistence.repository.FormatoRepository;
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

class FormatoServiceImplTest extends BaseServiceTest {
    @Mock private FormatoRepository repository;
    @Mock private FormatoMapper mapper;
    @InjectMocks private FormatoServiceImpl service;

    @Test
    @DisplayName("Should return paginated active formats")
    void shouldReturnPaginatedActive() {
        Page<com.arrupeteca.persistence.projection.FormatoResumen> page = new PageImpl<>(List.of());
        when(repository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerTodosActivos(Pageable.unpaged())).isEqualTo(page);
    }
}
