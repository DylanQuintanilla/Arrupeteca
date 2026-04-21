package com.arrupeteca.service.impl;

import java.util.List;
import java.util.Optional;

import com.arrupeteca.dto.request.EditorialRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EditorialMapper;
import com.arrupeteca.persistence.entity.Editorial;
import com.arrupeteca.persistence.projection.EditorialResumen;
import com.arrupeteca.persistence.repository.EditorialRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EditorialServiceImplTest extends BaseServiceTest {

    @Mock
    private EditorialRepository editorialRepository;

    @Mock
    private EditorialMapper editorialMapper;

    @InjectMocks
    private EditorialServiceImpl editorialService;

    @Test
    @DisplayName("Should return all active paginated")
    void shouldReturnAllActive() {
        Page<EditorialResumen> expectedPage = new PageImpl<>(List.of(mock(EditorialResumen.class)));
        when(editorialRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(expectedPage);

        Page<EditorialResumen> result = editorialService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    @DisplayName("Should return by ID and active")
    void shouldReturnByIdAndActive() {
        EditorialResumen resumen = mock(EditorialResumen.class);
        when(editorialRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(resumen));

        EditorialResumen result = editorialService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw exception when not found or inactive")
    void shouldThrowWhenNotFoundOrInactive() {
        when(editorialRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> editorialService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró una Editorial con ID: 1, o no está activa");
    }

    @Test
    @DisplayName("Should return all for admin")
    void shouldReturnAllAdmin() {
        Page<EditorialResumen> expectedPage = new PageImpl<>(List.of(mock(EditorialResumen.class)));
        when(editorialRepository.findAllProjectedBy(any(Pageable.class))).thenReturn(expectedPage);

        Page<EditorialResumen> result = editorialService.obtenerTodos(Pageable.unpaged());

        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    @DisplayName("Should return by ID")
    void shouldReturnById() {
        EditorialResumen resumen = mock(EditorialResumen.class);
        when(editorialRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        EditorialResumen result = editorialService.obtenerPorId(1L);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw exception when not found")
    void shouldThrowWhenNotFound() {
        when(editorialRepository.findProjectedById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> editorialService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró una Editorial con ID: 1");
    }

    @Test
    @DisplayName("Should search for admin")
    void shouldSearchAdmin() {
        Page<EditorialResumen> expectedPage = new PageImpl<>(List.of(mock(EditorialResumen.class)));
        when(editorialRepository.busquedaMaestraAdmin(any(), any(), any())).thenReturn(expectedPage);

        Page<EditorialResumen> result = editorialService.busquedaMaestraAdmin(false, "test", Pageable.unpaged());

        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    @DisplayName("Should create when name and email are unique")
    void shouldCreate() {
        EditorialRequest request = new EditorialRequest();
        request.setNombre("Nueva");
        request.setCorreo("nueva@test.com");

        when(editorialRepository.existsByNombreIgnoreCase("Nueva")).thenReturn(false);
        when(editorialRepository.existsByCorreoIgnoreCase("nueva@test.com")).thenReturn(false);
        when(editorialMapper.toEntity(any())).thenReturn(new Editorial());

        Editorial saved = new Editorial();
        saved.setId(1L);
        when(editorialRepository.save(any())).thenReturn(saved);

        EditorialResumen resumen = mock(EditorialResumen.class);
        when(editorialRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        EditorialResumen result = editorialService.crear(request);

        assertThat(result).isEqualTo(resumen);
        verify(editorialRepository).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating with duplicate name")
    void shouldThrowWhenDuplicateName() {
        EditorialRequest request = new EditorialRequest();
        request.setNombre("Duplicada");

        when(editorialRepository.existsByNombreIgnoreCase("Duplicada")).thenReturn(true);

        assertThatThrownBy(() -> editorialService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("El nombre: Duplicada ya ha sido utilizado");
    }

    @Test
    @DisplayName("Should throw exception when creating with duplicate email")
    void shouldThrowWhenDuplicateEmail() {
        EditorialRequest request = new EditorialRequest();
        request.setNombre("Nueva");
        request.setCorreo("duplicado@test.com");

        when(editorialRepository.existsByNombreIgnoreCase("Nueva")).thenReturn(false);
        when(editorialRepository.existsByCorreoIgnoreCase("duplicado@test.com")).thenReturn(true);

        assertThatThrownBy(() -> editorialService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("ya está registrado en otra editorial");
    }

    @Test
    @DisplayName("Should update when name and email are unique or same")
    void shouldUpdate() {
        EditorialRequest request = new EditorialRequest();
        request.setNombre("Actualizada");
        request.setCorreo("actualizada@test.com");

        Editorial existing = new Editorial();
        existing.setId(1L);
        existing.setNombre("Antigua");
        existing.setCorreo("antigua@test.com");

        when(editorialRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(editorialRepository.existsByNombreIgnoreCase("Actualizada")).thenReturn(false);
        when(editorialRepository.existsByCorreoIgnoreCase("actualizada@test.com")).thenReturn(false);

        EditorialResumen resumen = mock(EditorialResumen.class);
        when(editorialRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        EditorialResumen result = editorialService.actualizar(1L, request);

        assertThat(result).isEqualTo(resumen);
        verify(editorialMapper).updateEntity(request, existing);
        verify(editorialRepository).save(existing);
    }

    @Test
    @DisplayName("Should throw exception when updating and new name exists")
    void shouldThrowWhenUpdatingToExistingName() {
        EditorialRequest request = new EditorialRequest();
        request.setNombre("Existente");

        Editorial existing = new Editorial();
        existing.setId(1L);
        existing.setNombre("Antigua");

        when(editorialRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(editorialRepository.existsByNombreIgnoreCase("Existente")).thenReturn(true);

        assertThatThrownBy(() -> editorialService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe una editorial con el nombre: Existente");
    }

    @Test
    @DisplayName("Should throw exception when updating and new email exists")
    void shouldThrowWhenUpdatingToExistingEmail() {
        EditorialRequest request = new EditorialRequest();
        request.setNombre("Antigua");
        request.setCorreo("existente@test.com");

        Editorial existing = new Editorial();
        existing.setId(1L);
        existing.setNombre("Antigua");
        existing.setCorreo("antigua@test.com");

        when(editorialRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(editorialRepository.existsByCorreoIgnoreCase("existente@test.com")).thenReturn(true);

        assertThatThrownBy(() -> editorialService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe una editorial con el correo: existente@test.com");
    }

    @Test
    @DisplayName("Should update when name and email are same (ignore case)")
    void shouldUpdateWhenSame() {
        EditorialRequest request = new EditorialRequest();
        request.setNombre("EDITORIAL");
        request.setCorreo("EDITORIAL@TEST.COM");

        Editorial existing = new Editorial();
        existing.setId(1L);
        existing.setNombre("editorial");
        existing.setCorreo("editorial@test.com");

        when(editorialRepository.findById(1L)).thenReturn(Optional.of(existing));

        EditorialResumen resumen = mock(EditorialResumen.class);
        when(editorialRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        EditorialResumen result = editorialService.actualizar(1L, request);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw when updating non-existent")
    void shouldThrowWhenUpdatingNonExistent() {
        when(editorialRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> editorialService.actualizar(1L, new EditorialRequest()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Editorial con ID: 1");
    }

    @Test
    @DisplayName("Should deactivate")
    void shouldDeactivate() {
        when(editorialRepository.existsById(1L)).thenReturn(true);

        editorialService.cambiarEstado(1L, false);

        verify(editorialRepository).desactivar(1L);
    }

    @Test
    @DisplayName("Should activate")
    void shouldActivate() {
        when(editorialRepository.existsById(1L)).thenReturn(true);

        editorialService.cambiarEstado(1L, true);

        verify(editorialRepository).activar(1L);
    }

    @Test
    @DisplayName("Should throw when changing state of non-existent")
    void shouldThrowWhenChangingStateNonExistent() {
        when(editorialRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> editorialService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Editorial con ID: 1");
    }

}
