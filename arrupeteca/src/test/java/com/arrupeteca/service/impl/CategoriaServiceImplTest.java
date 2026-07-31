package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.dto.response.CategoriaResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.CategoriaMapper;
import com.arrupeteca.persistence.entity.Categoria;
import com.arrupeteca.persistence.repository.CategoriaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceImplTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private CategoriaMapper categoriaMapper;

    @InjectMocks
    private CategoriaServiceImpl categoriaService;

    @Test
    @DisplayName("Should return all active categories list")
    void shouldReturnAllActive() {
        Categoria entity = Categoria.builder().build();
        CategoriaResponse response = mock(CategoriaResponse.class);

        when(categoriaRepository.findByBorradoLogicoFalseOrderByNombreAsc()).thenReturn(List.of(entity));
        when(categoriaMapper.toResponse(entity)).thenReturn(response);

        List<CategoriaResponse> result = categoriaService.obtenerTodosActivos();

        assertThat(result).containsExactly(response);
        verify(categoriaRepository).findByBorradoLogicoFalseOrderByNombreAsc();
    }

    @Test
    @DisplayName("Should return all categories for admin")
    void shouldReturnAllAdmin() {
        Categoria entity = Categoria.builder().build();
        Page<Categoria> expectedPage = new PageImpl<>(List.of(entity));
        CategoriaResponse response = mock(CategoriaResponse.class);

        when(categoriaRepository.busquedaAdmin(isNull(), isNull(), any(Pageable.class))).thenReturn(expectedPage);
        when(categoriaMapper.toResponse(entity)).thenReturn(response);

        Page<CategoriaResponse> result = categoriaService.obtenerTodos(null, null, Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    @DisplayName("Should return category by ID")
    void shouldReturnById() {
        Categoria entity = Categoria.builder().build();
        CategoriaResponse response = mock(CategoriaResponse.class);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(categoriaMapper.toResponse(entity)).thenReturn(response);

        CategoriaResponse result = categoriaService.obtenerPorId(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("Should throw exception when category not found")
    void shouldThrowWhenNotFound() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoriaService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró una categoría con ID: 1");
    }

    @Test
    @DisplayName("Should create category successfully")
    void shouldCreateCategory() {
        CategoriaRequest request = CategoriaRequest.builder().nombre("General").build();
        Categoria entity = Categoria.builder().build();
        Categoria savedEntity = Categoria.builder().build();
        CategoriaResponse response = mock(CategoriaResponse.class);

        when(categoriaRepository.existsByNombreIgnoreCase("General")).thenReturn(false);
        when(categoriaMapper.toEntity(request)).thenReturn(entity);
        when(categoriaRepository.save(entity)).thenReturn(savedEntity);
        when(categoriaMapper.toResponse(savedEntity)).thenReturn(response);

        assertThat(categoriaService.crear(request)).isEqualTo(response);
    }

    @Test
    @DisplayName("Should throw exception when creating duplicate category")
    void shouldThrowWhenDuplicateNameOnCreate() {
        CategoriaRequest request = CategoriaRequest.builder().nombre("General").build();
        when(categoriaRepository.existsByNombreIgnoreCase("General")).thenReturn(true);

        assertThatThrownBy(() -> categoriaService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe una categoría con el nombre: General");
    }

    @Test
    @DisplayName("Should change estado of category successfully")
    void shouldCambiarEstado() {
        when(categoriaRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);

        categoriaService.cambiarEstado(1L, false);
        verify(categoriaRepository).desactivar(1L);

        categoriaService.cambiarEstado(1L, true);
        verify(categoriaRepository).activar(1L);
    }
}
