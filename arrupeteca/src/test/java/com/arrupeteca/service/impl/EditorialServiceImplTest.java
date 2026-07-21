package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EditorialRequest;
import com.arrupeteca.dto.response.EditorialResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EditorialMapper;
import com.arrupeteca.persistence.entity.Editorial;
import com.arrupeteca.persistence.repository.EditorialRepository;
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
    @DisplayName("obtenerTodosActivos - Should return page of EditorialResponse")
    void obtenerTodosActivos() {
        Editorial entity = Editorial.builder().id(1L).nombre("Test").build();
        Page<Editorial> page = new PageImpl<>(List.of(entity));
        EditorialResponse response = new EditorialResponse(1L, "Test", "Dir", "correo@test.com", "1234-5678", "Juan", false);

        when(editorialRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        when(editorialMapper.toResponse(entity)).thenReturn(response);

        Page<EditorialResponse> result = editorialService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should return EditorialResponse when found and active")
    void obtenerPorIdActivoSuccess() {
        EditorialResponse response = new EditorialResponse(1L, "Test", "Dir", "correo@test.com", "1234-5678", "Juan", false);
        Editorial entity = Editorial.builder().id(1L).nombre("Test").build();
        
        when(editorialRepository.findByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(editorialMapper.toResponse(entity)).thenReturn(response);

        EditorialResponse result = editorialService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should throw exception when not found")
    void obtenerPorIdActivoNotFound() {
        when(editorialRepository.findByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> editorialService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Editorial activa con ID: 1");
    }

    @Test
    @DisplayName("obtenerTodos - Should return page of EditorialResponse")
    void obtenerTodos() {
        Editorial entity = Editorial.builder().id(1L).nombre("Test").build();
        Page<Editorial> page = new PageImpl<>(List.of(entity));
        EditorialResponse response = new EditorialResponse(1L, "Test", "Dir", "correo@test.com", "1234-5678", "Juan", false);
        
        when(editorialRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(editorialMapper.toResponse(entity)).thenReturn(response);

        Page<EditorialResponse> result = editorialService.obtenerTodos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorId - Should return EditorialResponse when found")
    void obtenerPorIdSuccess() {
        Editorial entity = Editorial.builder().id(1L).nombre("Test").build();
        EditorialResponse response = new EditorialResponse(1L, "Test", "Dir", "correo@test.com", "1234-5678", "Juan", false);
        
        when(editorialRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(editorialMapper.toResponse(entity)).thenReturn(response);

        EditorialResponse result = editorialService.obtenerPorId(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorId - Should throw exception when not found")
    void obtenerPorIdNotFound() {
        when(editorialRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> editorialService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Editorial con ID: 1");
    }

    @Test
    @DisplayName("busquedaMaestraAdmin - Should return page of EditorialResponse")
    void busquedaMaestraAdmin() {
        Editorial entity = Editorial.builder().id(1L).nombre("Test").build();
        Page<Editorial> page = new PageImpl<>(List.of(entity));
        EditorialResponse response = new EditorialResponse(1L, "Test", "Dir", "correo@test.com", "1234-5678", "Juan", false);

        when(editorialRepository.busquedaMaestraAdmin(false, "term", Pageable.unpaged())).thenReturn(page);
        when(editorialMapper.toResponse(entity)).thenReturn(response);

        Page<EditorialResponse> result = editorialService.busquedaMaestraAdmin(false, "term", Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(response);
    }

    @Test
    @DisplayName("crear - Should create and return EditorialResponse")
    void crearSuccess() {
        EditorialRequest request = EditorialRequest.builder()
                .nombre("Nuevo")
                .correo("nuevo@editorial.com")
                .telefono("1234-5678")
                .personaContacto("Contacto")
                .build();
        Editorial entity = Editorial.builder().id(1L).nombre("Nuevo").build();
        Editorial savedEntity = Editorial.builder().id(1L).nombre("Nuevo").build();
        EditorialResponse response = new EditorialResponse(1L, "Nuevo", null, "nuevo@editorial.com", "1234-5678", "Contacto", false);

        when(editorialRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(false);
        when(editorialRepository.existsByCorreoIgnoreCase("nuevo@editorial.com")).thenReturn(false);
        when(editorialMapper.toEntity(request)).thenReturn(entity);
        when(editorialRepository.save(entity)).thenReturn(savedEntity);
        when(editorialMapper.toResponse(savedEntity)).thenReturn(response);

        EditorialResponse result = editorialService.crear(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("crear - Should throw exception when name exists")
    void crearDuplicateName() {
        EditorialRequest request = EditorialRequest.builder().nombre("Existe").correo("nuevo@editorial.com").build();

        when(editorialRepository.existsByNombreIgnoreCase("Existe")).thenReturn(true);

        assertThatThrownBy(() -> editorialService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe una editorial con el nombre: Existe");
    }

    @Test
    @DisplayName("crear - Should throw exception when email exists")
    void crearDuplicateCorreo() {
        EditorialRequest request = EditorialRequest.builder().nombre("Nuevo").correo("existe@editorial.com").build();

        when(editorialRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(false);
        when(editorialRepository.existsByCorreoIgnoreCase("existe@editorial.com")).thenReturn(true);

        assertThatThrownBy(() -> editorialService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe una editorial con el correo: existe@editorial.com");
    }

    @Test
    @DisplayName("actualizar - Should update and return EditorialResponse")
    void actualizarSuccess() {
        Long id = 1L;
        EditorialRequest request = EditorialRequest.builder()
                .nombre("Actualizado")
                .correo("actualizado@editorial.com")
                .build();
        Editorial existingEntity = Editorial.builder()
                .id(1L)
                .nombre("Original")
                .correo("original@editorial.com")
                .build();
        EditorialResponse response = new EditorialResponse(id, "Actualizado", null, "actualizado@editorial.com", null, null, false);

        when(editorialRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(editorialRepository.existsByNombreIgnoreCase("Actualizado")).thenReturn(false);
        when(editorialRepository.existsByCorreoIgnoreCase("actualizado@editorial.com")).thenReturn(false);
        when(editorialRepository.save(existingEntity)).thenReturn(existingEntity);
        when(editorialMapper.toResponse(existingEntity)).thenReturn(response);

        EditorialResponse result = editorialService.actualizar(id, request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("actualizar - Should throw exception when not found")
    void actualizarNotFound() {
        Long id = 1L;
        EditorialRequest request = EditorialRequest.builder().nombre("Cualquiera").correo("c@test.com").build();

        when(editorialRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> editorialService.actualizar(id, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Editorial con ID: 1");
    }

    @Test
    @DisplayName("actualizar - Should throw exception when updating to existing name")
    void actualizarDuplicateName() {
        Long id = 1L;
        EditorialRequest request = EditorialRequest.builder().nombre("Existe").correo("or@test.com").build();
        Editorial existingEntity = Editorial.builder().id(1L).nombre("Original").correo("or@test.com").build();

        when(editorialRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(editorialRepository.existsByNombreIgnoreCase("Existe")).thenReturn(true);

        assertThatThrownBy(() -> editorialService.actualizar(id, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe otra editorial con el nombre: Existe");
    }

    @Test
    @DisplayName("cambiarEstado - Should activate when state is true")
    void cambiarEstadoActivar() {
        when(editorialRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);

        editorialService.cambiarEstado(1L, true);

        verify(editorialRepository).activar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should deactivate when state is false")
    void cambiarEstadoDesactivar() {
        when(editorialRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);

        editorialService.cambiarEstado(1L, false);

        verify(editorialRepository).desactivar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should throw exception when not found")
    void cambiarEstadoNotFound() {
        when(editorialRepository.countByIdIncludeDeleted(1L)).thenReturn(0L);

        assertThatThrownBy(() -> editorialService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Editorial con ID: 1");
    }
}
