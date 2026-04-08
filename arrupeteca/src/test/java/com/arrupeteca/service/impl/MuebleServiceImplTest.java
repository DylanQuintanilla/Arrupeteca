package com.arrupeteca.service.impl;

import com.arrupeteca.mapper.MuebleMapper;
import com.arrupeteca.persistence.repository.MuebleRepository;
import com.arrupeteca.persistence.repository.SalonRepository;
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

class MuebleServiceImplTest extends BaseServiceTest {
    @Mock private MuebleRepository repository;
    @Mock private MuebleMapper mapper;
    @Mock private SalonRepository salonRepository;
    @InjectMocks private MuebleServiceImpl service;

    @Test
    @DisplayName("Should return paginated active furniture")
    void shouldReturnPaginatedActive() {
        Page<com.arrupeteca.persistence.projection.MuebleResumen> page = new PageImpl<>(List.of());
        when(repository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerTodosActivos(Pageable.unpaged())).isEqualTo(page);
    }
}
