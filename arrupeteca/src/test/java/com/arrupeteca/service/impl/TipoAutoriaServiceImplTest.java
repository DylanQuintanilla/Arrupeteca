package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.TipoAutoriaRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.TipoAutoriaMapper;
import com.arrupeteca.persistence.entity.TipoAutoria;
import com.arrupeteca.persistence.projection.TipoAutoriaResumen;
import com.arrupeteca.persistence.repository.TipoAutoriaRepository;
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

class TipoAutoriaServiceImplTest extends BaseServiceTest {

    @Mock
    private TipoAutoriaRepository tipoAutoriaRepository;

    @Mock
    private TipoAutoriaMapper tipoAutoriaMapper;

    @InjectMocks
    private TipoAutoriaServiceImpl tipoAutoriaService;

    @Test
    @DisplayName("Should return all active paginated")
    void shouldReturnAllActive() {
        Page<TipoAutoriaResumen> expectedPage = new PageImpl<>(List.of(mock(TipoAutoriaResumen.class)));
        when(tipoAutoriaRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(expectedPage);

        Page<TipoAutoriaResumen> result = tipoAutoriaService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result).isEqualTo(expectedPage);
        verify(tipoAutoriaRepository).findByBorradoLogicoFalse(any(Pageable.class));
    }

    @Test
    @DisplayName("Should return by ID and active")
    void shouldReturnByIdAndActive() {
        TipoAutoriaResumen resumen = mock(TipoAutoriaResumen.class);
        when(tipoAutoriaRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(resumen));

        TipoAutoriaResumen result = tipoAutoriaService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw exception when not found or inactive")
    void shouldThrowWhenNotFoundOrInactive() {
        when(tipoAutoriaRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoAutoriaService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un tipo de autoría con ID: 1, o no está activo");
    }

    @Test
    @DisplayName("Should return all for admin")
    void shouldReturnAllAdmin() {
        Page<TipoAutoriaResumen> expectedPage = new PageImpl<>(List.of(mock(TipoAutoriaResumen.class)));
        when(tipoAutoriaRepository.findAllProjectedBy(any(Pageable.class))).thenReturn(expectedPage);

        Page<TipoAutoriaResumen> result = tipoAutoriaService.obtenerTodos(Pageable.unpaged());

        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    @DisplayName("Should return by ID")
    void shouldReturnById() {
        TipoAutoriaResumen resumen = mock(TipoAutoriaResumen.class);
        when(tipoAutoriaRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        TipoAutoriaResumen result = tipoAutoriaService.obtenerPorId(1L);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw exception when not found")
    void shouldThrowWhenNotFound() {
        when(tipoAutoriaRepository.findProjectedById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoAutoriaService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un tipo de autoría con ID: 1");
    }

    @Test
    @DisplayName("Should create when name is unique")
    void shouldCreate() {
        TipoAutoriaRequest request = new TipoAutoriaRequest();
        request.setNombre("Nuevo");

        when(tipoAutoriaRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(false);
        when(tipoAutoriaMapper.toEntity(any())).thenReturn(new TipoAutoria());
        TipoAutoria saved = new TipoAutoria();
        saved.setId(1L);
        when(tipoAutoriaRepository.save(any())).thenReturn(saved);
        
        TipoAutoriaResumen resumen = mock(TipoAutoriaResumen.class);
        when(tipoAutoriaRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        TipoAutoriaResumen result = tipoAutoriaService.crear(request);

        assertThat(result).isEqualTo(resumen);
        verify(tipoAutoriaRepository).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating with duplicate name")
    void shouldThrowWhenDuplicateName() {
        TipoAutoriaRequest request = new TipoAutoriaRequest();
        request.setNombre("Duplicado");

        when(tipoAutoriaRepository.existsByNombreIgnoreCase("Duplicado")).thenReturn(true);

        assertThatThrownBy(() -> tipoAutoriaService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("ya ha sido utilizado");
    }

    @Test
    @DisplayName("Should update when name is unique or same")
    void shouldUpdate() {
        TipoAutoriaRequest request = new TipoAutoriaRequest();
        request.setNombre("Actualizado");

        TipoAutoria existing = new TipoAutoria();
        existing.setId(1L);
        existing.setNombre("Antiguo");

        when(tipoAutoriaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(tipoAutoriaRepository.existsByNombreIgnoreCase("Actualizado")).thenReturn(false);
        
        TipoAutoriaResumen resumen = mock(TipoAutoriaResumen.class);
        when(tipoAutoriaRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        TipoAutoriaResumen result = tipoAutoriaService.actualizar(1L, request);

        assertThat(result).isEqualTo(resumen);
        verify(tipoAutoriaMapper).updateEntity(request, existing);
        verify(tipoAutoriaRepository).save(existing);
    }

    @Test
    @DisplayName("Should throw exception when updating and new name exists")
    void shouldThrowWhenUpdatingToExistingName() {
        TipoAutoriaRequest request = new TipoAutoriaRequest();
        request.setNombre("Existente");

        TipoAutoria existing = new TipoAutoria();
        existing.setId(1L);
        existing.setNombre("Antiguo");

        when(tipoAutoriaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(tipoAutoriaRepository.existsByNombreIgnoreCase("Existente")).thenReturn(true);

        assertThatThrownBy(() -> tipoAutoriaService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe una autoría con el nombre: Existente");
    }

    @Test
    @DisplayName("Should update when name is same (ignore case)")
    void shouldUpdateWhenNameSame() {
        TipoAutoriaRequest request = new TipoAutoriaRequest();
        request.setNombre("TIPO");

        TipoAutoria existing = new TipoAutoria();
        existing.setId(1L);
        existing.setNombre("tipo");

        when(tipoAutoriaRepository.findById(1L)).thenReturn(Optional.of(existing));
        
        TipoAutoriaResumen resumen = mock(TipoAutoriaResumen.class);
        when(tipoAutoriaRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        TipoAutoriaResumen result = tipoAutoriaService.actualizar(1L, request);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw when updating non-existent")
    void shouldThrowWhenUpdatingNonExistent() {
        when(tipoAutoriaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tipoAutoriaService.actualizar(1L, new TipoAutoriaRequest()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un tipo de autoría con ID: 1");
    }

    @Test
    @DisplayName("Should deactivate")
    void shouldDeactivate() {
        when(tipoAutoriaRepository.existsById(1L)).thenReturn(true);

        tipoAutoriaService.cambiarEstado(1L, false);

        verify(tipoAutoriaRepository).desactivar(1L);
    }

    @Test
    @DisplayName("Should activate")
    void shouldActivate() {
        when(tipoAutoriaRepository.existsById(1L)).thenReturn(true);

        tipoAutoriaService.cambiarEstado(1L, true);

        verify(tipoAutoriaRepository).activar(1L);
    }

    @Test
    @DisplayName("Should throw when changing state of non-existent")
    void shouldThrowWhenChangingStateNonExistent() {
        when(tipoAutoriaRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> tipoAutoriaService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un tipo de autoría con ID: 1");
    }
}

