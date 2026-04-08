package com.arrupeteca.service.impl;

import com.arrupeteca.mapper.LibroMapper;
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

class LibroServiceImplTest extends BaseServiceTest {
    @Mock private LibroRepository repository;
    @Mock private LibroMapper mapper;
    @Mock private ObraRepository obraRepository;
    @Mock private EditorialRepository editorialRepository;
    @Mock private FormatoRepository formatoRepository;
    @Mock private IdiomaRepository idiomaRepository;
    @InjectMocks private LibroServiceImpl service;

    @Test
    @DisplayName("Should return paginated active books")
    void shouldReturnPaginatedActive() {
        Page<com.arrupeteca.persistence.projection.LibroResumen> page = new PageImpl<>(List.of());
        when(repository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerTodosActivos(Pageable.unpaged())).isEqualTo(page);
    }
}
