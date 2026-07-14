package com.arrupeteca.service.impl;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import com.arrupeteca.dto.request.AutorRequest;
import org.openapitools.jackson.nullable.JsonNullable;
import com.arrupeteca.dto.response.detail.AutorDetalleResponse;
import com.arrupeteca.dto.response.summary.AutorResumenResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.AutorMapper;
import com.arrupeteca.persistence.entity.Autor;
import com.arrupeteca.persistence.repository.AutorRepository;
import com.arrupeteca.persistence.repository.GeneroRepository;
import com.arrupeteca.persistence.repository.NacionalidadRepository;
import com.arrupeteca.persistence.repository.ObraRepository;
import com.arrupeteca.persistence.repository.PaisRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AutorServiceImplTest {

    @Mock
    private AutorRepository autorRepository;

    @Mock
    private PaisRepository paisRepository;

    @Mock
    private NacionalidadRepository nacionalidadRepository;

    @Mock
    private GeneroRepository generoRepository;

    @Mock
    private ObraRepository obraRepository;

    @Mock
    private AutorMapper autorMapper;

    @InjectMocks
    private AutorServiceImpl autorService;

    @Test
    @DisplayName("Should return active authors page")
    void shouldReturnActiveAuthors() {
        Page<Autor> page = new PageImpl<>(List.of());
        when(autorRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<AutorResumenResponse> result = autorService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return active author by ID")
    void shouldReturnActiveAuthorById() {
        Autor autor = Autor.builder().id(1L).borradoLogico(false).build();
        when(autorRepository.findOne(any(Specification.class))).thenReturn(Optional.of(autor));

        AutorDetalleResponse detalle = mock(AutorDetalleResponse.class);
        when(autorMapper.toDetalleResponse(autor)).thenReturn(detalle);

        AutorDetalleResponse result = autorService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(detalle);
    }

    @Test
    @DisplayName("Should throw exception when active author by ID is not found")
    void shouldThrowWhenActiveAuthorNotFound() {
        when(autorRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> autorService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Autor activo con ID: 1");
    }

    @Test
    @DisplayName("Should return all authors page")
    void shouldReturnAllAuthors() {
        Page<Autor> page = new PageImpl<>(List.of());
        when(autorRepository.findAll(nullable(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<AutorResumenResponse> result = autorService.obtenerTodos(Pageable.unpaged());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return author by ID including deleted")
    void shouldReturnAuthorByIdIncludingDeleted() {
        Autor autor = Autor.builder().id(1L).build();
        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));

        AutorDetalleResponse detalle = mock(AutorDetalleResponse.class);
        when(autorMapper.toDetalleResponse(autor)).thenReturn(detalle);

        AutorDetalleResponse result = autorService.obtenerPorId(1L);

        assertThat(result).isEqualTo(detalle);
    }

    @Test
    @DisplayName("Should throw exception when author by ID is not found")
    void shouldThrowWhenAuthorNotFound() {
        when(autorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> autorService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Autor con ID: 1");
    }

    @Test
    @DisplayName("Should perform global search")
    void shouldPerformGlobalSearch() {
        Page<Autor> page = new PageImpl<>(List.of());
        when(autorRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<AutorResumenResponse> result = autorService.busquedaGlobal("palabra", Year.of(1900), Year.of(2000), Pageable.unpaged());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should perform admin master search")
    void shouldPerformAdminMasterSearch() {
        Page<Autor> page = new PageImpl<>(List.of());
        when(autorRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<AutorResumenResponse> result = autorService.busquedaMaestraAdmin(
                false, 1L, 1L, 1L, null, null, "test", Pageable.unpaged()
        );

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should create author with default values and valid relations")
    void shouldCreateAuthor() {
        AutorRequest request = AutorRequest.builder()
                .nombre(JsonNullable.of("Test"))
                .biografia(JsonNullable.of("Bio"))
                .idPaisNacimiento(JsonNullable.of(1L))
                .idNacionalidad(JsonNullable.of(1L))
                .idGeneroPrincipal(JsonNullable.of(1L))
                .idObraCumbre(JsonNullable.of(1L))
                .build();

        when(autorRepository.existsByNombreIgnoreCase(any())).thenReturn(false);
        when(paisRepository.findById(1L)).thenReturn(Optional.of(new com.arrupeteca.persistence.entity.Pais()));
        when(nacionalidadRepository.findById(1L)).thenReturn(Optional.of(new com.arrupeteca.persistence.entity.Nacionalidad()));
        when(generoRepository.findById(1L)).thenReturn(Optional.of(new com.arrupeteca.persistence.entity.Genero()));
        when(obraRepository.findById(1L)).thenReturn(Optional.of(new com.arrupeteca.persistence.entity.Obra()));
        
        Autor autorEntity = Autor.builder().build();
        when(autorMapper.toEntity(any())).thenReturn(autorEntity);

        Autor saved = Autor.builder().build();
        saved.setId(1L);
        when(autorRepository.saveAndFlush(any())).thenReturn(saved);

        AutorDetalleResponse detalle = mock(AutorDetalleResponse.class);
        when(autorMapper.toDetalleResponse(saved)).thenReturn(detalle);

        AutorDetalleResponse result = autorService.crear(request);

        assertThat(result).isEqualTo(detalle);
        verify(autorRepository).saveAndFlush(any());
    }

    @Test
    @DisplayName("Should create author with null relations")
    void shouldCreateAuthorWithNullRelations() {
        AutorRequest request = AutorRequest.builder()
                .nombre(JsonNullable.of("Test"))
                .biografia(JsonNullable.of("Bio"))
                .build();

        when(autorRepository.existsByNombreIgnoreCase(any())).thenReturn(false);

        Autor autorEntity = Autor.builder().build();
        when(autorMapper.toEntity(any())).thenReturn(autorEntity);

        Autor saved = Autor.builder().build();
        saved.setId(1L);
        when(autorRepository.saveAndFlush(any())).thenReturn(saved);

        AutorDetalleResponse detalle = mock(AutorDetalleResponse.class);
        when(autorMapper.toDetalleResponse(saved)).thenReturn(detalle);

        AutorDetalleResponse result = autorService.crear(request);

        assertThat(result).isEqualTo(detalle);
    }

    @Test
    @DisplayName("Should update author")
    void shouldUpdateAuthor() {
        AutorRequest request = AutorRequest.builder().nombre(JsonNullable.of("Nuevo Nombre")).build();

        Autor existing = Autor.builder().build();
        existing.setId(1L);
        existing.setNombre("Viejo");
        when(autorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(autorRepository.existsByNombreIgnoreCase("Nuevo Nombre")).thenReturn(false);
        
        Autor saved = Autor.builder().build();
        saved.setId(1L);
        when(autorRepository.saveAndFlush(any())).thenReturn(saved);

        AutorDetalleResponse detalle = mock(AutorDetalleResponse.class);
        when(autorMapper.toDetalleResponse(saved)).thenReturn(detalle);

        AutorDetalleResponse result = autorService.actualizar(1L, request);

        assertThat(result).isEqualTo(detalle);
        verify(autorMapper).updateEntity(request, existing);
        verify(autorRepository).saveAndFlush(existing);
    }

    @Test
    @DisplayName("Should throw when updating non-existent author")
    void shouldThrowWhenUpdatingNonExistent() {
        when(autorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> autorService.actualizar(1L, AutorRequest.builder().build()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Autor con ID: 1");
    }

    @Test
    @DisplayName("Should deactivate author")
    void shouldDeactivate() {
        when(autorRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);

        autorService.cambiarEstado(1L, false);

        verify(autorRepository).desactivar(1L);
    }

    @Test
    @DisplayName("Should activate author")
    void shouldActivate() {
        when(autorRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);

        autorService.cambiarEstado(1L, true);

        verify(autorRepository).activar(1L);
    }

    @Test
    @DisplayName("Should throw when changing state of non-existent author")
    void shouldThrowWhenChangingStateNonExistent() {
        when(autorRepository.countByIdIncludeDeleted(1L)).thenReturn(0L);

        assertThatThrownBy(() -> autorService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Autor con ID: 1");
    }

    @Test
    @DisplayName("Should update author with same name")
    void shouldUpdateAuthorSameName() {
        AutorRequest request = AutorRequest.builder().nombre(JsonNullable.of("Mismo")).build();

        Autor existing = Autor.builder().build();
        existing.setId(1L);
        existing.setNombre("Mismo");
        when(autorRepository.findById(1L)).thenReturn(Optional.of(existing));
        
        Autor saved = Autor.builder().build();
        saved.setId(1L);
        when(autorRepository.saveAndFlush(any())).thenReturn(saved);

        AutorDetalleResponse detalle = mock(AutorDetalleResponse.class);
        when(autorMapper.toDetalleResponse(saved)).thenReturn(detalle);

        AutorDetalleResponse result = autorService.actualizar(1L, request);

        assertThat(result).isEqualTo(detalle);
    }

    @Test
    @DisplayName("Should throw when updating author with name that already exists")
    void shouldThrowWhenUpdateNameExists() {
        AutorRequest request = AutorRequest.builder().nombre(JsonNullable.of("Existente")).build();

        Autor existing = Autor.builder().build();
        existing.setId(1L);
        existing.setNombre("Viejo");
        when(autorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(autorRepository.existsByNombreIgnoreCase("Existente")).thenReturn(true);

        assertThatThrownBy(() -> autorService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un autor con el nombre: Existente");
    }

    @Test
    @DisplayName("Should throw exception if author name exists during creation")
    void crearNombreExists() {
        AutorRequest request = AutorRequest.builder()
                .nombre(JsonNullable.of("Test"))
                .biografia(JsonNullable.of("Bio"))
                .build();
        when(autorRepository.existsByNombreIgnoreCase("Test")).thenReturn(true);

        assertThatThrownBy(() -> autorService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un autor con el nombre: Test");
    }

    @Test
    @DisplayName("Should verify duplicates with trimming")
    void shouldVerifyDuplicatesWithTrimming() {
        Autor autor = Autor.builder().build();
        when(autorRepository.buscarPosiblesDuplicados("Termino", 1L))
                .thenReturn(List.of(autor));
        when(autorMapper.toResumen(autor)).thenReturn(mock(AutorResumenResponse.class));

        assertThat(autorService.buscarDuplicados("  Termino  ", 1L)).isNotEmpty();
        verify(autorRepository).buscarPosiblesDuplicados("Termino", 1L);
    }

    @Test
    @DisplayName("Should throw exception when creating author with empty identity")
    void shouldThrowWhenCreatingEmptyIdentity() {
        AutorRequest request = AutorRequest.builder()
                .nombre(JsonNullable.of(""))
                .seudonimo(JsonNullable.of(""))
                .biografia(JsonNullable.of("Bio"))
                .build();

        assertThatThrownBy(() -> autorService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("El autor debe tener al menos un Nombre o un Seudonimo");
    }

    @Test
    @DisplayName("Should throw exception when creating author with empty biography")
    void shouldThrowWhenCreatingEmptyBiography() {
        AutorRequest request = AutorRequest.builder()
                .nombre(JsonNullable.of("Test"))
                .biografia(JsonNullable.of(""))
                .build();

        assertThatThrownBy(() -> autorService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("La biografia no puede estar vacía");
    }

    @Test
    @DisplayName("Should throw exception when updating with optimistic locking version mismatch")
    void shouldThrowWhenOptimisticLockingMismatch() {
        AutorRequest request = AutorRequest.builder()
                .version(1L)
                .build();

        Autor existing = Autor.builder().build();
        existing.setId(1L);
        existing.setVersion(2L);
        when(autorRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> autorService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("El registro fue modificado por otro usuario");
    }

    @Test
    @DisplayName("Should throw exception when updating with empty name if present")
    void shouldThrowWhenUpdatingWithEmptyName() {
        AutorRequest request = AutorRequest.builder()
                .nombre(JsonNullable.of(""))
                .build();

        Autor existing = Autor.builder().build();
        existing.setId(1L);
        existing.setVersion(1L);
        when(autorRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> autorService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("El nombre no puede estar vacío");
    }
}
