package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.FormatoRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.FormatoMapper;
import com.arrupeteca.persistence.entity.Formato;
import com.arrupeteca.persistence.projection.FormatoResumen;
import com.arrupeteca.persistence.repository.FormatoRepository;
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

class FormatoServiceImplTest extends BaseServiceTest {

    @Mock
    private FormatoRepository formatoRepository;

    @Mock
    private FormatoMapper formatoMapper;

    @InjectMocks
    private FormatoServiceImpl formatoService;

    @Test
    @DisplayName("Should return all active paginated")
    void shouldReturnAllActive() {
        Page<FormatoResumen> expectedPage = new PageImpl<>(List.of(mock(FormatoResumen.class)));
        when(formatoRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(expectedPage);

        Page<FormatoResumen> result = formatoService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result).isEqualTo(expectedPage);
        verify(formatoRepository).findByBorradoLogicoFalse(any(Pageable.class));
    }

    @Test
    @DisplayName("Should return by ID and active")
    void shouldReturnByIdAndActive() {
        FormatoResumen resumen = mock(FormatoResumen.class);
        when(formatoRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(resumen));

        FormatoResumen result = formatoService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw exception when not found or inactive")
    void shouldThrowWhenNotFoundOrInactive() {
        when(formatoRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> formatoService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un Formato con ID: 1, o no está activo");
    }

    @Test
    @DisplayName("Should return all for admin")
    void shouldReturnAllAdmin() {
        Page<FormatoResumen> expectedPage = new PageImpl<>(List.of(mock(FormatoResumen.class)));
        when(formatoRepository.findAllProjectedBy(any(Pageable.class))).thenReturn(expectedPage);

        Page<FormatoResumen> result = formatoService.obtenerTodos(Pageable.unpaged());

        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    @DisplayName("Should return by ID")
    void shouldReturnById() {
        FormatoResumen resumen = mock(FormatoResumen.class);
        when(formatoRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        FormatoResumen result = formatoService.obtenerPorId(1L);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw exception when not found")
    void shouldThrowWhenNotFound() {
        when(formatoRepository.findProjectedById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> formatoService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un Formato con ID: 1");
    }

    @Test
    @DisplayName("Should create when name is unique")
    void shouldCreate() {
        FormatoRequest request = new FormatoRequest();
        request.setNombre("Nuevo");

        when(formatoRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(false);
        when(formatoMapper.toEntity(any())).thenReturn(new Formato());
        Formato saved = new Formato();
        saved.setId(1L);
        when(formatoRepository.save(any())).thenReturn(saved);
        
        FormatoResumen resumen = mock(FormatoResumen.class);
        when(formatoRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        FormatoResumen result = formatoService.crear(request);

        assertThat(result).isEqualTo(resumen);
        verify(formatoRepository).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating with duplicate name")
    void shouldThrowWhenDuplicateName() {
        FormatoRequest request = new FormatoRequest();
        request.setNombre("Duplicado");

        when(formatoRepository.existsByNombreIgnoreCase("Duplicado")).thenReturn(true);

        assertThatThrownBy(() -> formatoService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("ya ha sido utilizado");
    }

    @Test
    @DisplayName("Should update when name is unique or same")
    void shouldUpdate() {
        FormatoRequest request = new FormatoRequest();
        request.setNombre("Actualizado");

        Formato existing = new Formato();
        existing.setId(1L);
        existing.setNombre("Antiguo");

        when(formatoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(formatoRepository.existsByNombreIgnoreCase("Actualizado")).thenReturn(false);
        
        FormatoResumen resumen = mock(FormatoResumen.class);
        when(formatoRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        FormatoResumen result = formatoService.actualizar(1L, request);

        assertThat(result).isEqualTo(resumen);
        verify(formatoMapper).updateEntity(request, existing);
        verify(formatoRepository).save(existing);
    }

    @Test
    @DisplayName("Should throw exception when updating and new name exists")
    void shouldThrowWhenUpdatingToExistingName() {
        FormatoRequest request = new FormatoRequest();
        request.setNombre("Existente");

        Formato existing = new Formato();
        existing.setId(1L);
        existing.setNombre("Antiguo");

        when(formatoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(formatoRepository.existsByNombreIgnoreCase("Existente")).thenReturn(true);

        assertThatThrownBy(() -> formatoService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un Formato con el nombre: Existente");
    }

    @Test
    @DisplayName("Should update when name is same (ignore case)")
    void shouldUpdateWhenNameSame() {
        FormatoRequest request = new FormatoRequest();
        request.setNombre("FORMATO");

        Formato existing = new Formato();
        existing.setId(1L);
        existing.setNombre("formato");

        when(formatoRepository.findById(1L)).thenReturn(Optional.of(existing));
        
        FormatoResumen resumen = mock(FormatoResumen.class);
        when(formatoRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        FormatoResumen result = formatoService.actualizar(1L, request);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw when updating non-existent")
    void shouldThrowWhenUpdatingNonExistent() {
        when(formatoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> formatoService.actualizar(1L, new FormatoRequest()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Formato con ID: 1");
    }

    @Test
    @DisplayName("Should deactivate")
    void shouldDeactivate() {
        when(formatoRepository.existsById(1L)).thenReturn(true);

        formatoService.cambiarEstado(1L, false);

        verify(formatoRepository).desactivar(1L);
    }

    @Test
    @DisplayName("Should activate")
    void shouldActivate() {
        when(formatoRepository.existsById(1L)).thenReturn(true);

        formatoService.cambiarEstado(1L, true);

        verify(formatoRepository).activar(1L);
    }

    @Test
    @DisplayName("Should throw when changing state of non-existent")
    void shouldThrowWhenChangingStateNonExistent() {
        when(formatoRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> formatoService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Formato con ID: 1");
    }
}

