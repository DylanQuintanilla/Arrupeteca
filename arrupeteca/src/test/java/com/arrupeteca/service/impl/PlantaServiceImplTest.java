package com.arrupeteca.service.impl;

import com.arrupeteca.mapper.PlantaMapper;
import com.arrupeteca.persistence.repository.EdificioRepository;
import com.arrupeteca.persistence.repository.PlantaRepository;
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

class PlantaServiceImplTest extends BaseServiceTest {
    @Mock private PlantaRepository repository;
    @Mock private PlantaMapper mapper;
    @Mock private EdificioRepository edificioRepository;
    @InjectMocks private PlantaServiceImpl service;

    @Test
    @DisplayName("Should return paginated active plants")
    void shouldReturnPaginatedActive() {
        Page<com.arrupeteca.persistence.projection.PlantaResumen> page = new PageImpl<>(List.of());
        when(repository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerTodosActivos(Pageable.unpaged())).isEqualTo(page);
    }
}
