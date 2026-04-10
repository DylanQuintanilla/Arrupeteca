package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.GeneroRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.GeneroMapper;
import com.arrupeteca.persistence.entity.Genero;
import com.arrupeteca.persistence.projection.GeneroResumen;
import com.arrupeteca.persistence.repository.GeneroRepository;
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

class GeneroServiceImplTest extends BaseServiceTest {

    @Mock
    private GeneroRepository generoRepository;

    @Mock
    private GeneroMapper generoMapper;

    @InjectMocks
    private GeneroServiceImpl generoService;

    @Test
    @DisplayName("Should return all active genres paginated")
    void shouldReturnAllActive() {
        Page<GeneroResumen> expectedPage = new PageImpl<>(List.of(mock(GeneroResumen.class)));
        when(generoRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(expectedPage);

        Page<GeneroResumen> result = generoService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result).isEqualTo(expectedPage);
        verify(generoRepository).findByBorradoLogicoFalse(any(Pageable.class));
    }

    @Test
    @DisplayName("Should return genre by ID and active")
    void shouldReturnByIdAndActive() {
        GeneroResumen resumen = mock(GeneroResumen.class);
        when(generoRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(resumen));

        GeneroResumen result = generoService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw exception when genre not found or inactive")
    void shouldThrowWhenNotFoundOrInactive() {
        when(generoRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> generoService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe un género con el ID: 1, o no está activo");
    }

    @Test
    @DisplayName("Should return all genres for admin")
    void shouldReturnAllAdmin() {
        Page<GeneroResumen> expectedPage = new PageImpl<>(List.of(mock(GeneroResumen.class)));
        when(generoRepository.findAllProjectedBy(any(Pageable.class))).thenReturn(expectedPage);

        Page<GeneroResumen> result = generoService.obtenerTodos(Pageable.unpaged());

        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    @DisplayName("Should return genre by ID")
    void shouldReturnById() {
        GeneroResumen resumen = mock(GeneroResumen.class);
        when(generoRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        GeneroResumen result = generoService.obtenerPorId(1L);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw exception when genre not found")
    void shouldThrowWhenNotFound() {
        when(generoRepository.findProjectedById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> generoService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe un género con el ID: 1");
    }

    @Test
    @DisplayName("Should create genre when name is unique")
    void shouldCreateGenre() {
        GeneroRequest request = new GeneroRequest();
        request.setNombre("Nuevo");

        when(generoRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(false);
        when(generoMapper.toEntity(any())).thenReturn(new Genero());
        Genero saved = new Genero();
        saved.setId(1L);
        when(generoRepository.save(any())).thenReturn(saved);
        
        GeneroResumen resumen = mock(GeneroResumen.class);
        when(generoRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        GeneroResumen result = generoService.crear(request);

        assertThat(result).isEqualTo(resumen);
        verify(generoRepository).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating genre with duplicate name")
    void shouldThrowWhenDuplicateName() {
        GeneroRequest request = new GeneroRequest();
        request.setNombre("Duplicado");

        when(generoRepository.existsByNombreIgnoreCase("Duplicado")).thenReturn(true);

        assertThatThrownBy(() -> generoService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un género con el nombre: Duplicado");
    }

    @Test
    @DisplayName("Should update genre when name is unique or same")
    void shouldUpdateGenre() {
        GeneroRequest request = new GeneroRequest();
        request.setNombre("Actualizado");

        Genero existing = new Genero();
        existing.setId(1L);
        existing.setNombre("Antiguo");

        when(generoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(generoRepository.existsByNombreIgnoreCase("Actualizado")).thenReturn(false);
        
        GeneroResumen resumen = mock(GeneroResumen.class);
        when(generoRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        GeneroResumen result = generoService.actualizar(1L, request);

        assertThat(result).isEqualTo(resumen);
        verify(generoMapper).updateEntity(request, existing);
        verify(generoRepository).save(existing);
    }

    @Test
    @DisplayName("Should throw exception when updating genre and new name exists")
    void shouldThrowWhenUpdatingToExistingName() {
        GeneroRequest request = new GeneroRequest();
        request.setNombre("Existente");

        Genero existing = new Genero();
        existing.setId(1L);
        existing.setNombre("Antiguo");

        when(generoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(generoRepository.existsByNombreIgnoreCase("Existente")).thenReturn(true);

        assertThatThrownBy(() -> generoService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un género con el nombre: Existente");
    }

    @Test
    @DisplayName("Should update genre when name is same (ignore case)")
    void shouldUpdateWhenNameSame() {
        GeneroRequest request = new GeneroRequest();
        request.setNombre("GENERO");

        Genero existing = new Genero();
        existing.setId(1L);
        existing.setNombre("genero");

        when(generoRepository.findById(1L)).thenReturn(Optional.of(existing));
        
        GeneroResumen resumen = mock(GeneroResumen.class);
        when(generoRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        GeneroResumen result = generoService.actualizar(1L, request);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw when updating non-existent genre")
    void shouldThrowWhenUpdatingNonExistent() {
        when(generoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> generoService.actualizar(1L, new GeneroRequest()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe un género con el ID: 1");
    }

    @Test
    @DisplayName("Should deactivate genre")
    void shouldDeactivate() {
        when(generoRepository.existsById(1L)).thenReturn(true);

        generoService.cambiarEstado(1L, false);

        verify(generoRepository).desactivar(1L);
    }

    @Test
    @DisplayName("Should activate genre")
    void shouldActivate() {
        when(generoRepository.existsById(1L)).thenReturn(true);

        generoService.cambiarEstado(1L, true);

        verify(generoRepository).activar(1L);
    }

    @Test
    @DisplayName("Should throw when changing state of non-existent genre")
    void shouldThrowWhenChangingStateNonExistent() {
        when(generoRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> generoService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe un género con el ID: 1");
    }
}

