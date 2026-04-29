package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EditorialRequest;
import com.arrupeteca.dto.response.detail.EditorialDetalleResponse;
import com.arrupeteca.dto.response.summary.EditorialResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EditorialMapper;
import com.arrupeteca.persistence.entity.Editorial;
import com.arrupeteca.persistence.repository.EditorialRepository;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EditorialServiceImplTest {

    @Mock
    private EditorialRepository editorialRepository;

    @Mock
    private EditorialMapper editorialMapper;

    @InjectMocks
    private EditorialServiceImpl editorialService;

    @Test
    @DisplayName("Should return active editoriales")
    void shouldReturnActive() {
        Editorial entity = new Editorial();
        Page<Editorial> expectedPage = new PageImpl<>(List.of(entity));
        EditorialResumen resumen = mock(EditorialResumen.class);
        
        when(editorialRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(expectedPage);
        when(editorialMapper.toResumen(entity)).thenReturn(resumen);

        Page<EditorialResumen> result = editorialService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(resumen);
    }

    @Test
    @DisplayName("Should return editorial by ID when active")
    void shouldReturnByIdActive() {
        Editorial entity = new Editorial();
        EditorialDetalleResponse detalle = mock(EditorialDetalleResponse.class);
        when(editorialRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(editorialMapper.toDetalleResponse(entity)).thenReturn(detalle);

        EditorialDetalleResponse result = editorialService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(detalle);
    }

    @Test
    @DisplayName("Should throw exception when not found or inactive")
    void shouldThrowWhenNotFoundOrInactive() {
        when(editorialRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> editorialService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Editorial activa con ID: 1");
    }

    @Test
    @DisplayName("Should return all for admin")
    void shouldReturnAllAdmin() {
        Editorial entity = new Editorial();
        Page<Editorial> expectedPage = new PageImpl<>(List.of(entity));
        EditorialResumen resumen = mock(EditorialResumen.class);
        
        when(editorialRepository.findAllProjectedBy(any(Pageable.class))).thenReturn(expectedPage);
        when(editorialMapper.toResumen(entity)).thenReturn(resumen);

        Page<EditorialResumen> result = editorialService.obtenerTodos(Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(resumen);
    }

    @Test
    @DisplayName("Should return editorial by ID for admin")
    void shouldReturnById() {
        Editorial entity = new Editorial();
        EditorialDetalleResponse detalle = mock(EditorialDetalleResponse.class);
        when(editorialRepository.findProjectedById(1L)).thenReturn(Optional.of(entity));
        when(editorialMapper.toDetalleResponse(entity)).thenReturn(detalle);

        EditorialDetalleResponse result = editorialService.obtenerPorId(1L);

        assertThat(result).isEqualTo(detalle);
    }

    @Test
    @DisplayName("Should throw exception when not found")
    void shouldThrowWhenNotFound() {
        when(editorialRepository.findProjectedById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> editorialService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Editorial con ID: 1");
    }

    @Test
    @DisplayName("Should search for admin")
    void shouldSearchAdmin() {
        Editorial entity = new Editorial();
        Page<Editorial> expectedPage = new PageImpl<>(List.of(entity));
        EditorialResumen resumen = mock(EditorialResumen.class);
        
        when(editorialRepository.busquedaMaestraAdmin(any(), any(), any())).thenReturn(expectedPage);
        when(editorialMapper.toResumen(entity)).thenReturn(resumen);

        Page<EditorialResumen> result = editorialService.busquedaMaestraAdmin(false, "term", Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(resumen);
    }

    @Test
    @DisplayName("Should create editorial")
    void shouldCreate() {
        EditorialRequest request = new EditorialRequest();
        request.setNombre("Nueva");
        request.setCorreo("test@test.com");
        
        Editorial entity = new Editorial();
        EditorialDetalleResponse detail = mock(EditorialDetalleResponse.class);

        when(editorialRepository.existsByNombreIgnoreCase("Nueva")).thenReturn(false);
        when(editorialRepository.existsByCorreoIgnoreCase("test@test.com")).thenReturn(false);
        when(editorialMapper.toEntity(request)).thenReturn(entity);
        when(editorialRepository.save(any(Editorial.class))).thenReturn(entity);
        when(editorialMapper.toDetalleResponse(entity)).thenReturn(detail);

        EditorialDetalleResponse result = editorialService.crear(request);

        assertThat(result).isEqualTo(detail);
        verify(editorialRepository).save(any(Editorial.class));
    }

    @Test
    @DisplayName("Should throw when creating with existing name")
    void shouldThrowOnCreateExistingName() {
        EditorialRequest request = new EditorialRequest();
        request.setNombre("Existente");

        when(editorialRepository.existsByNombreIgnoreCase("Existente")).thenReturn(true);

        assertThatThrownBy(() -> editorialService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe una editorial con el nombre: Existente");
    }

    @Test
    @DisplayName("Should throw exception when creating with existing email")
    void shouldThrowOnCreateExistingEmail() {
        EditorialRequest request = new EditorialRequest();
        request.setNombre("Nueva");
        request.setCorreo("existe@test.com");

        when(editorialRepository.existsByNombreIgnoreCase("Nueva")).thenReturn(false);
        when(editorialRepository.existsByCorreoIgnoreCase("existe@test.com")).thenReturn(true);

        assertThatThrownBy(() -> editorialService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe una editorial con el correo: existe@test.com");
    }

    @Test
    @DisplayName("Should update editorial")
    void shouldUpdate() {
        EditorialRequest request = new EditorialRequest();
        request.setNombre("Actualizada");
        request.setCorreo("test@test.com");

        Editorial existing = new Editorial();
        existing.setId(1L);
        existing.setNombre("Antigua");
        existing.setCorreo("antigua@test.com");

        EditorialDetalleResponse detail = mock(EditorialDetalleResponse.class);

        when(editorialRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(editorialRepository.existsByNombreIgnoreCase("Actualizada")).thenReturn(false);
        when(editorialRepository.existsByCorreoIgnoreCase("test@test.com")).thenReturn(false);
        when(editorialRepository.save(existing)).thenReturn(existing);
        when(editorialMapper.toDetalleResponse(existing)).thenReturn(detail);

        EditorialDetalleResponse result = editorialService.actualizar(1L, request);

        assertThat(result).isEqualTo(detail);
        verify(editorialMapper).updateEntity(request, existing);
    }

    @Test
    @DisplayName("Should update editorial with same name and email")
    void shouldUpdateSameValues() {
        EditorialRequest request = new EditorialRequest();
        request.setNombre("Mismo");
        request.setCorreo("mismo@test.com");

        Editorial existing = new Editorial();
        existing.setId(1L);
        existing.setNombre("Mismo");
        existing.setCorreo("mismo@test.com");

        EditorialDetalleResponse detail = mock(EditorialDetalleResponse.class);

        when(editorialRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(editorialRepository.save(existing)).thenReturn(existing);
        when(editorialMapper.toDetalleResponse(existing)).thenReturn(detail);

        EditorialDetalleResponse result = editorialService.actualizar(1L, request);

        assertThat(result).isEqualTo(detail);
        verify(editorialRepository, never()).existsByNombreIgnoreCase(any());
        verify(editorialRepository, never()).existsByCorreoIgnoreCase(any());
    }

    @Test
    @DisplayName("Should throw exception when updating to existing name")
    void shouldThrowOnUpdateExistingName() {
        EditorialRequest request = new EditorialRequest();
        request.setNombre("Ocupado");
        request.setCorreo("test@test.com");

        Editorial existing = new Editorial();
        existing.setId(1L);
        existing.setNombre("Antigua");

        when(editorialRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(editorialRepository.existsByNombreIgnoreCase("Ocupado")).thenReturn(true);

        assertThatThrownBy(() -> editorialService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe otra editorial con el nombre: Ocupado");
    }

    @Test
    @DisplayName("Should throw exception when updating to existing email")
    void shouldThrowOnUpdateExistingEmail() {
        EditorialRequest request = new EditorialRequest();
        request.setNombre("Actualizada");
        request.setCorreo("ocupado@test.com");

        Editorial existing = new Editorial();
        existing.setId(1L);
        existing.setNombre("Actualizada"); // Same name, different email
        existing.setCorreo("antigua@test.com");

        when(editorialRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(editorialRepository.existsByCorreoIgnoreCase("ocupado@test.com")).thenReturn(true);

        assertThatThrownBy(() -> editorialService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe otra editorial con el correo: ocupado@test.com");
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
    @DisplayName("Should activate editorial")
    void shouldActivate() {
        when(editorialRepository.existsById(1L)).thenReturn(true);

        editorialService.cambiarEstado(1L, true);

        verify(editorialRepository).activar(1L);
    }

    @Test
    @DisplayName("Should deactivate editorial")
    void shouldDeactivate() {
        when(editorialRepository.existsById(1L)).thenReturn(true);

        editorialService.cambiarEstado(1L, false);

        verify(editorialRepository).desactivar(1L);
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
