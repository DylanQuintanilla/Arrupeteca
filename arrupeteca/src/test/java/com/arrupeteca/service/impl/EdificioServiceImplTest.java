package com.arrupeteca.service.impl;

import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EdificioMapper;
import com.arrupeteca.persistence.repository.EdificioRepository;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EdificioServiceImplTest extends BaseServiceTest {
    @Mock private EdificioRepository repository;
    @Mock private EdificioMapper mapper;
    @InjectMocks private EdificioServiceImpl service;

    @Test
    @DisplayName("Should return paginated active buildings")
    void shouldReturnPaginatedActive() {
        Page<com.arrupeteca.persistence.projection.EdificioResumen> page = new PageImpl<>(List.of());
        when(repository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerTodosActivos(Pageable.unpaged())).isEqualTo(page);
    }
}
