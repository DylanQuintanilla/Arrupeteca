package com.arrupeteca.service.impl;

import com.arrupeteca.mapper.ObraMapper;
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

class ObraServiceImplTest extends BaseServiceTest {
    @Mock private ObraRepository repository;
    @Mock private GeneroRepository generoRepository;
    @Mock private CategoriaRepository categoriaRepository;
    @Mock private AutorRepository autorRepository;
    @Mock private TipoAutoriaRepository tipoAutoriaRepository;
    @Mock private ObraMapper mapper;
    @InjectMocks private ObraServiceImpl service;

    @Test
    @DisplayName("Should return paginated active works")
    void shouldReturnPaginatedActive() {
        Page<com.arrupeteca.persistence.projection.ObraResumen> page = new PageImpl<>(List.of());
        when(repository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerTodosActivos(Pageable.unpaged())).isEqualTo(page);
    }
}
