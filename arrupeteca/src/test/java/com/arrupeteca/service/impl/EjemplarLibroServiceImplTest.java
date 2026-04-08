package com.arrupeteca.service.impl;

import com.arrupeteca.mapper.EjemplarLibroMapper;
import com.arrupeteca.persistence.repository.EjemplarLibroRepository;
import com.arrupeteca.persistence.repository.LibroRepository;
import com.arrupeteca.persistence.repository.MuebleRepository;
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

class EjemplarLibroServiceImplTest extends BaseServiceTest {
    @Mock private EjemplarLibroRepository repository;
    @Mock private EjemplarLibroMapper mapper;
    @Mock private LibroRepository libroRepository;
    @Mock private MuebleRepository muebleRepository;
    @InjectMocks private EjemplarLibroServiceImpl service;

    @Test
    @DisplayName("Should return paginated active exemplars")
    void shouldReturnPaginatedActive() {
        Page<com.arrupeteca.persistence.projection.EjemplarLibroResumen> page = new PageImpl<>(List.of());
        when(repository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerTodosActivos(Pageable.unpaged())).isEqualTo(page);
    }
}
