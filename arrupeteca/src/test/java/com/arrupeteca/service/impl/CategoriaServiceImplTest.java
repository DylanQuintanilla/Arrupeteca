package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.dto.response.CategoriaResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.CategoriaMapper;
import com.arrupeteca.persistence.entity.Categoria;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        Categoria entity = Categoria.builder().build();
        Page<Categoria> expectedPage = new PageImpl<>(List.of(entity));
        CategoriaResponse response = mock(CategoriaResponse.class);

        when(categoriaRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(expectedPage);
        when(categoriaMapper.toResponse(entity)).thenReturn(response);

        Page<CategoriaResponse> result = categoriaService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(response);
        verify(categoriaRepository).findByBorradoLogicoFalse(any(Pageable.class));
    }

    @Test
    @DisplayName("Should return category by ID and active")
    void shouldReturnByIdAndActive() {
        Categoria entity = Categoria.builder().build();
        CategoriaResponse response = mock(CategoriaResponse.class);
        when(categoriaRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(categoriaMapper.toResponse(entity)).thenReturn(response);

        CategoriaResponse result = categoriaService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("Should throw exception when category not found or inactive")
    void shouldThrowWhenNotFoundOrInactive() {
        when(categoriaRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoriaService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró una categoría activa con ID: 1");
    }

    @Test
    @DisplayName("Should return all categories for admin")
    void shouldReturnAllAdmin() {
        Categoria entity = Categoria.builder().build();
        Page<Categoria> expectedPage = new PageImpl<>(List.of(entity));
        CategoriaResponse response = mock(CategoriaResponse.class);

        when(categoriaRepository.findAllProjectedBy(any(Pageable.class))).thenReturn(expectedPage);
        when(categoriaMapper.toResponse(entity)).thenReturn(response);

        Page<CategoriaResponse> result = categoriaService.obtenerTodos(Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    @DisplayName("Should return category by ID")
    void shouldReturnById() {
        Categoria entity = Categoria.builder().build();
        CategoriaResponse response = mock(CategoriaResponse.class);
        when(categoriaRepository.findProjectedById(1L)).thenReturn(Optional.of(entity));
        when(categoriaMapper.toResponse(entity)).thenReturn(response);

        CategoriaResponse result = categoriaService.obtenerPorId(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("Should throw exception when category not found")
    void shouldThrowWhenNotFound() {
        when(categoriaRepository.findProjectedById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoriaService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró una categoría con ID: 1");
    }

    @Test
    @DisplayName("Should create category when name is unique")
    void shouldCreateCategory() {
        CategoriaRequest request = CategoriaRequest.builder().nombre("Nueva").build();

        when(categoriaRepository.existsByNombreIgnoreCase("Nueva")).thenReturn(false);
        Categoria entity = Categoria.builder().build();
        when(categoriaMapper.toEntity(any())).thenReturn(entity);
        
        Categoria saved = Categoria.builder().build();
        saved.setId(1L);
        when(categoriaRepository.save(any())).thenReturn(saved);
        
        CategoriaResponse response = mock(CategoriaResponse.class);
        when(categoriaMapper.toResponse(saved)).thenReturn(response);

        CategoriaResponse result = categoriaService.crear(request);

        assertThat(result).isEqualTo(response);
        verify(categoriaRepository).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating category with duplicate name")
    void shouldThrowWhenDuplicateName() {
        CategoriaRequest request = CategoriaRequest.builder().nombre("Duplicada").build();

        when(categoriaRepository.existsByNombreIgnoreCase("Duplicada")).thenReturn(true);

        assertThatThrownBy(() -> categoriaService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe una categoría con el nombre: Duplicada");
    }

    @Test
    @DisplayName("Should update category when name is unique or same")
    void shouldUpdateCategory() {
        CategoriaRequest request = CategoriaRequest.builder().nombre("Actualizada").build();

        Categoria existing = Categoria.builder().build();
        existing.setId(1L);
        existing.setNombre("Antigua");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoriaRepository.existsByNombreIgnoreCase("Actualizada")).thenReturn(false);
        
        Categoria saved = Categoria.builder().build();
        saved.setId(1L);
        when(categoriaRepository.save(any())).thenReturn(saved);

        CategoriaResponse response = mock(CategoriaResponse.class);
        when(categoriaMapper.toResponse(saved)).thenReturn(response);

        CategoriaResponse result = categoriaService.actualizar(1L, request);

        assertThat(result).isEqualTo(response);
        verify(categoriaMapper).updateEntity(request, existing);
        verify(categoriaRepository).save(existing);
    }

    @Test
    @DisplayName("Should throw exception when updating category and new name exists")
    void shouldThrowWhenUpdatingToExistingName() {
        CategoriaRequest request = CategoriaRequest.builder().nombre("Existente").build();

        Categoria existing = Categoria.builder().build();
        existing.setId(1L);
        existing.setNombre("Antigua");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoriaRepository.existsByNombreIgnoreCase("Existente")).thenReturn(true);

        assertThatThrownBy(() -> categoriaService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe otra categoría con el nombre: Existente");
    }

    @Test
    @DisplayName("Should update category when name is same (ignore case)")
    void shouldUpdateWhenNameSame() {
        CategoriaRequest request = CategoriaRequest.builder().nombre("CATEGORIA").build();

        Categoria existing = Categoria.builder().build();
        existing.setId(1L);
        existing.setNombre("categoria");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(existing));
        
        Categoria saved = Categoria.builder().build();
        saved.setId(1L);
        when(categoriaRepository.save(any())).thenReturn(saved);

        CategoriaResponse response = mock(CategoriaResponse.class);
        when(categoriaMapper.toResponse(saved)).thenReturn(response);

        CategoriaResponse result = categoriaService.actualizar(1L, request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("Should throw when updating non-existent category")
    void shouldThrowWhenUpdatingNonExistent() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoriaService.actualizar(1L, CategoriaRequest.builder().nombre("Nueva").build()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró una categoría con ID: 1");
    }

    @Test
    @DisplayName("Should deactivate category")
    void shouldDeactivate() {
        when(categoriaRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);

        categoriaService.cambiarEstado(1L, false);

        verify(categoriaRepository).desactivar(1L);
    }

    @Test
    @DisplayName("Should activate category")
    void shouldActivate() {
        when(categoriaRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);

        categoriaService.cambiarEstado(1L, true);

        verify(categoriaRepository).activar(1L);
    }

    @Test
    @DisplayName("Should throw when changing state of non-existent category")
    void shouldThrowWhenChangingStateNonExistent() {
        when(categoriaRepository.countByIdIncludeDeleted(1L)).thenReturn(0L);

        assertThatThrownBy(() -> categoriaService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró una categoría con ID: 1");
    }
}
