package com.arrupeteca.service.impl;

import com.arrupeteca.mapper.NacionalidadMapper;
import com.arrupeteca.persistence.repository.NacionalidadRepository;
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

class NacionalidadServiceImplTest extends BaseServiceTest {
    @Mock private NacionalidadRepository repository;
    @Mock private NacionalidadMapper mapper;
    @InjectMocks private NacionalidadServiceImpl service;

    @Test
    @DisplayName("Should return paginated active nationalities")
    void shouldReturnPaginatedActive() {
        Page<com.arrupeteca.persistence.projection.NacionalidadResumen> page = new PageImpl<>(List.of());
        when(repository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerTodasActivas(Pageable.unpaged())).isEqualTo(page);
    }
}
