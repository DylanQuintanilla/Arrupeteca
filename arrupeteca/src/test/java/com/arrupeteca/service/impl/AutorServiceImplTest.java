package com.arrupeteca.service.impl;

import com.arrupeteca.mapper.AutorMapper;
import com.arrupeteca.persistence.repository.*;
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

class AutorServiceImplTest extends BaseServiceTest {
    @Mock private AutorRepository repository;
    @Mock private AutorMapper mapper;
    @Mock private PaisRepository paisRepository;
    @Mock private NacionalidadRepository nacionalidadRepository;
    @Mock private GeneroRepository generoRepository;
    @Mock private ObraRepository obraRepository;
    @InjectMocks private AutorServiceImpl service;

    @Test
    @DisplayName("Should return paginated active authors")
    void shouldReturnPaginatedActive() {
        Page<com.arrupeteca.persistence.projection.AutorResumen> page = new PageImpl<>(List.of());
        when(repository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerTodosActivos(Pageable.unpaged())).isEqualTo(page);
    }
}
