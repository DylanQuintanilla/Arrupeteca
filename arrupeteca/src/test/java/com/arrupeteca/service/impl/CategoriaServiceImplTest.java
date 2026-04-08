package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.CategoriaMapper;
import com.arrupeteca.persistence.entity.Categoria;
import com.arrupeteca.persistence.projection.CategoriaResumen;
import com.arrupeteca.persistence.repository.CategoriaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoriaServiceImplTest extends BaseServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private CategoriaMapper categoriaMapper;

    @InjectMocks
    private CategoriaServiceImpl categoriaService;

    @Test
    @DisplayName("Should return all active categories paginated")
    void shouldReturnAllActive() {
        Page<CategoriaResumen> expectedPage = new PageImpl<>(List.of(mock(CategoriaResumen.class)));
        when(categoriaRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(expectedPage);

        Page<CategoriaResumen> result = categoriaService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result).isEqualTo(expectedPage);
        verify(categoriaRepository).findByBorradoLogicoFalse(any(Pageable.class));
    }

    @Test
    @DisplayName("Should create category when name is unique")
    void shouldCreateCategory() {
        CategoriaRequest request = new CategoriaRequest();
        request.setNombre("Nueva");

        when(categoriaRepository.existsByNombreIgnoreCase("Nueva")).thenReturn(false);
        when(categoriaMapper.toEntity(any())).thenReturn(new Categoria());
        Categoria saved = new Categoria();
        saved.setId(1L);
        when(categoriaRepository.save(any())).thenReturn(saved);
        
        CategoriaResumen resumen = mock(CategoriaResumen.class);
        when(categoriaRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        CategoriaResumen result = categoriaService.crear(request);

        assertThat(result).isEqualTo(resumen);
        verify(categoriaRepository).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating category with duplicate name")
    void shouldThrowWhenDuplicateName() {
        CategoriaRequest request = new CategoriaRequest();
        request.setNombre("Duplicada");

        when(categoriaRepository.existsByNombreIgnoreCase("Duplicada")).thenReturn(true);

        assertThatThrownBy(() -> categoriaService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("ya ha sido utilizado");
    }
}
