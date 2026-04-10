package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.IdiomaRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.IdiomaMapper;
import com.arrupeteca.persistence.entity.Idioma;
import com.arrupeteca.persistence.projection.IdiomaResumen;
import com.arrupeteca.persistence.repository.IdiomaRepository;
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

class IdiomaServiceImplTest extends BaseServiceTest {

    @Mock
    private IdiomaRepository idiomaRepository;

    @Mock
    private IdiomaMapper idiomaMapper;

    @InjectMocks
    private IdiomaServiceImpl idiomaService;

    @Test
    @DisplayName("Should return all active paginated")
    void shouldReturnAllActive() {
        Page<IdiomaResumen> expectedPage = new PageImpl<>(List.of(mock(IdiomaResumen.class)));
        when(idiomaRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(expectedPage);

        Page<IdiomaResumen> result = idiomaService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result).isEqualTo(expectedPage);
        verify(idiomaRepository).findByBorradoLogicoFalse(any(Pageable.class));
    }

    @Test
    @DisplayName("Should return by ID and active")
    void shouldReturnByIdAndActive() {
        IdiomaResumen resumen = mock(IdiomaResumen.class);
        when(idiomaRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(resumen));

        IdiomaResumen result = idiomaService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw exception when not found or inactive")
    void shouldThrowWhenNotFoundOrInactive() {
        when(idiomaRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> idiomaService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un Idioma con ID: 1, o no está activo");
    }

    @Test
    @DisplayName("Should return all for admin")
    void shouldReturnAllAdmin() {
        Page<IdiomaResumen> expectedPage = new PageImpl<>(List.of(mock(IdiomaResumen.class)));
        when(idiomaRepository.findAllProjectedBy(any(Pageable.class))).thenReturn(expectedPage);

        Page<IdiomaResumen> result = idiomaService.obtenerTodos(Pageable.unpaged());

        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    @DisplayName("Should return by ID")
    void shouldReturnById() {
        IdiomaResumen resumen = mock(IdiomaResumen.class);
        when(idiomaRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        IdiomaResumen result = idiomaService.obtenerPorId(1L);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw exception when not found")
    void shouldThrowWhenNotFound() {
        when(idiomaRepository.findProjectedById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> idiomaService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un Idioma con ID: 1");
    }

    @Test
    @DisplayName("Should create when name is unique")
    void shouldCreate() {
        IdiomaRequest request = new IdiomaRequest();
        request.setNombre("Nuevo");

        when(idiomaRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(false);
        when(idiomaMapper.toEntity(any())).thenReturn(new Idioma());
        Idioma saved = new Idioma();
        saved.setId(1L);
        when(idiomaRepository.save(any())).thenReturn(saved);
        
        IdiomaResumen resumen = mock(IdiomaResumen.class);
        when(idiomaRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        IdiomaResumen result = idiomaService.crear(request);

        assertThat(result).isEqualTo(resumen);
        verify(idiomaRepository).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating with duplicate name")
    void shouldThrowWhenDuplicateName() {
        IdiomaRequest request = new IdiomaRequest();
        request.setNombre("Duplicado");

        when(idiomaRepository.existsByNombreIgnoreCase("Duplicado")).thenReturn(true);

        assertThatThrownBy(() -> idiomaService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("ya ha sido utilizado");
    }

    @Test
    @DisplayName("Should update when name is unique or same")
    void shouldUpdate() {
        IdiomaRequest request = new IdiomaRequest();
        request.setNombre("Actualizado");

        Idioma existing = new Idioma();
        existing.setId(1L);
        existing.setNombre("Antiguo");

        when(idiomaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(idiomaRepository.existsByNombreIgnoreCase("Actualizado")).thenReturn(false);
        
        IdiomaResumen resumen = mock(IdiomaResumen.class);
        when(idiomaRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        IdiomaResumen result = idiomaService.actualizar(1L, request);

        assertThat(result).isEqualTo(resumen);
        verify(idiomaMapper).updateEntity(request, existing);
        verify(idiomaRepository).save(existing);
    }

    @Test
    @DisplayName("Should throw exception when updating and new name exists")
    void shouldThrowWhenUpdatingToExistingName() {
        IdiomaRequest request = new IdiomaRequest();
        request.setNombre("Existente");

        Idioma existing = new Idioma();
        existing.setId(1L);
        existing.setNombre("Antiguo");

        when(idiomaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(idiomaRepository.existsByNombreIgnoreCase("Existente")).thenReturn(true);

        assertThatThrownBy(() -> idiomaService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un Idioma con el nombre: Existente");
    }

    @Test
    @DisplayName("Should update when name is same (ignore case)")
    void shouldUpdateWhenNameSame() {
        IdiomaRequest request = new IdiomaRequest();
        request.setNombre("IDIOMA");

        Idioma existing = new Idioma();
        existing.setId(1L);
        existing.setNombre("idioma");

        when(idiomaRepository.findById(1L)).thenReturn(Optional.of(existing));
        
        IdiomaResumen resumen = mock(IdiomaResumen.class);
        when(idiomaRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        IdiomaResumen result = idiomaService.actualizar(1L, request);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw when updating non-existent")
    void shouldThrowWhenUpdatingNonExistent() {
        when(idiomaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> idiomaService.actualizar(1L, new IdiomaRequest()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Idioma con ID: 1");
    }

    @Test
    @DisplayName("Should deactivate")
    void shouldDeactivate() {
        when(idiomaRepository.existsById(1L)).thenReturn(true);

        idiomaService.cambiarEstado(1L, false);

        verify(idiomaRepository).desactivar(1L);
    }

    @Test
    @DisplayName("Should activate")
    void shouldActivate() {
        when(idiomaRepository.existsById(1L)).thenReturn(true);

        idiomaService.cambiarEstado(1L, true);

        verify(idiomaRepository).activar(1L);
    }

    @Test
    @DisplayName("Should throw when changing state of non-existent")
    void shouldThrowWhenChangingStateNonExistent() {
        when(idiomaRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> idiomaService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Idioma con ID: 1");
    }
}

