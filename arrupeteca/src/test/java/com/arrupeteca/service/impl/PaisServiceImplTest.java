package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.PaisRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.PaisMapper;
import com.arrupeteca.persistence.entity.Pais;
import com.arrupeteca.persistence.projection.PaisResumen;
import com.arrupeteca.persistence.repository.PaisRepository;
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

class PaisServiceImplTest extends BaseServiceTest {

    @Mock
    private PaisRepository paisRepository;

    @Mock
    private PaisMapper paisMapper;

    @InjectMocks
    private PaisServiceImpl paisService;

    @Test
    @DisplayName("Should return all active countries paginated")
    void shouldReturnAllActive() {
        Page<PaisResumen> expectedPage = new PageImpl<>(List.of(mock(PaisResumen.class)));
        when(paisRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(expectedPage);

        Page<PaisResumen> result = paisService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result).isEqualTo(expectedPage);
        verify(paisRepository).findByBorradoLogicoFalse(any(Pageable.class));
    }

    @Test
    @DisplayName("Should return country by ID and active")
    void shouldReturnByIdAndActive() {
        PaisResumen resumen = mock(PaisResumen.class);
        when(paisRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(resumen));

        PaisResumen result = paisService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw exception when country not found or inactive")
    void shouldThrowWhenNotFoundOrInactive() {
        when(paisRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paisService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe un país con ID: 1, o no está activo");
    }

    @Test
    @DisplayName("Should return all countries for admin")
    void shouldReturnAllAdmin() {
        Page<PaisResumen> expectedPage = new PageImpl<>(List.of(mock(PaisResumen.class)));
        when(paisRepository.findAllProjectedBy(any(Pageable.class))).thenReturn(expectedPage);

        Page<PaisResumen> result = paisService.obtenerTodos(Pageable.unpaged());

        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    @DisplayName("Should return country by ID")
    void shouldReturnById() {
        PaisResumen resumen = mock(PaisResumen.class);
        when(paisRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        PaisResumen result = paisService.obtenerPorId(1L);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw exception when country not found")
    void shouldThrowWhenNotFound() {
        when(paisRepository.findProjectedById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paisService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe un país con ID: 1");
    }

    @Test
    @DisplayName("Should create country when name is unique")
    void shouldCreateCountry() {
        PaisRequest request = new PaisRequest();
        request.setNombre("Nuevo");

        when(paisRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(false);
        when(paisMapper.toEntity(any())).thenReturn(new Pais());
        Pais saved = new Pais();
        saved.setId(1L);
        when(paisRepository.save(any())).thenReturn(saved);
        
        PaisResumen resumen = mock(PaisResumen.class);
        when(paisRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        PaisResumen result = paisService.crear(request);

        assertThat(result).isEqualTo(resumen);
        verify(paisRepository).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating country with duplicate name")
    void shouldThrowWhenDuplicateName() {
        PaisRequest request = new PaisRequest();
        request.setNombre("Duplicado");

        when(paisRepository.existsByNombreIgnoreCase("Duplicado")).thenReturn(true);

        assertThatThrownBy(() -> paisService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un país con el nombre: Duplicado");
    }

    @Test
    @DisplayName("Should update country when name is unique or same")
    void shouldUpdateCountry() {
        PaisRequest request = new PaisRequest();
        request.setNombre("Actualizado");

        Pais existing = new Pais();
        existing.setId(1L);
        existing.setNombre("Antiguo");

        when(paisRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(paisRepository.existsByNombreIgnoreCase("Actualizado")).thenReturn(false);
        
        PaisResumen resumen = mock(PaisResumen.class);
        when(paisRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        PaisResumen result = paisService.actualizar(1L, request);

        assertThat(result).isEqualTo(resumen);
        verify(paisMapper).updateEntity(request, existing);
        verify(paisRepository).save(existing);
    }

    @Test
    @DisplayName("Should throw exception when updating country and new name exists")
    void shouldThrowWhenUpdatingToExistingName() {
        PaisRequest request = new PaisRequest();
        request.setNombre("Existente");

        Pais existing = new Pais();
        existing.setId(1L);
        existing.setNombre("Antiguo");

        when(paisRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(paisRepository.existsByNombreIgnoreCase("Existente")).thenReturn(true);

        assertThatThrownBy(() -> paisService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe otro país con el nombre: Existente");
    }

    @Test
    @DisplayName("Should update country when name is same (ignore case)")
    void shouldUpdateWhenNameSame() {
        PaisRequest request = new PaisRequest();
        request.setNombre("PAIS");

        Pais existing = new Pais();
        existing.setId(1L);
        existing.setNombre("pais");

        when(paisRepository.findById(1L)).thenReturn(Optional.of(existing));
        
        PaisResumen resumen = mock(PaisResumen.class);
        when(paisRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        PaisResumen result = paisService.actualizar(1L, request);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw when updating non-existent country")
    void shouldThrowWhenUpdatingNonExistent() {
        when(paisRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paisService.actualizar(1L, new PaisRequest()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encuentra el país con ID: 1");
    }

    @Test
    @DisplayName("Should deactivate country")
    void shouldDeactivate() {
        when(paisRepository.existsById(1L)).thenReturn(true);

        paisService.cambiarEstado(1L, false);

        verify(paisRepository).desactivar(1L);
    }

    @Test
    @DisplayName("Should activate country")
    void shouldActivate() {
        when(paisRepository.existsById(1L)).thenReturn(true);

        paisService.cambiarEstado(1L, true);

        verify(paisRepository).activar(1L);
    }

    @Test
    @DisplayName("Should throw when changing state of non-existent country")
    void shouldThrowWhenChangingStateNonExistent() {
        when(paisRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> paisService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encuentra el país con ID: 1");
    }
}

