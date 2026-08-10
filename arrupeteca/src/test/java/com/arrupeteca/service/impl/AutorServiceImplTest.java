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
import org.springframework.data.domain.Sort;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AutorServiceImplTest extends BaseServiceTest {

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
        Page<AutorResumenResponse> page = new PageImpl<>(List.of());
        when(autorRepository.busquedaMaestraAdminProyectada(any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

        Page<AutorResumenResponse> result = autorService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return active author by ID")
    void shouldReturnActiveAuthorById() {
        Autor autor = Autor.builder().id(1L).borradoLogico(false).build();
        when(autorRepository.findByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(autor));

        AutorDetalleResponse detalle = mock(AutorDetalleResponse.class);
        when(autorMapper.toDetalleResponse(autor)).thenReturn(detalle);

        AutorDetalleResponse result = autorService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(detalle);
    }

    @Test
    @DisplayName("Should throw exception when active author by ID is not found")
    void shouldThrowWhenActiveAuthorNotFound() {
        when(autorRepository.findByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> autorService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con ID: 1");
    }

    @Test
    @DisplayName("Should return all authors page")
    void shouldReturnAllAuthors() {
        Page<AutorResumenResponse> page = new PageImpl<>(List.of());
        when(autorRepository.busquedaMaestraAdminProyectada(any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

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
                .hasMessageContaining("con ID: 1");
    }

    @Test
    @DisplayName("Should perform global search")
    void shouldPerformGlobalSearch() {
        Page<AutorResumenResponse> page = new PageImpl<>(List.of());
        when(autorRepository.busquedaMaestraAdminProyectada(any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

        Page<AutorResumenResponse> result = autorService.busquedaGlobal("palabra", Year.of(1900), Year.of(2000), Pageable.unpaged());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should perform admin master search")
    void shouldPerformAdminMasterSearch() {
        Page<AutorResumenResponse> page = new PageImpl<>(List.of());
        when(autorRepository.busquedaMaestraAdminProyectada(any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

        Page<AutorResumenResponse> result = autorService.busquedaMaestraAdmin(
                false, 1L, 1L, 1L, null, null, null, "test", Pageable.unpaged()
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
        when(paisRepository.findById(1L)).thenReturn(Optional.of(com.arrupeteca.persistence.entity.Pais.builder().build()));
        when(nacionalidadRepository.findById(1L)).thenReturn(Optional.of(com.arrupeteca.persistence.entity.Nacionalidad.builder().build()));
        when(generoRepository.findById(1L)).thenReturn(Optional.of(com.arrupeteca.persistence.entity.Genero.builder().build()));
        when(obraRepository.findById(1L)).thenReturn(Optional.of(com.arrupeteca.persistence.entity.Obra.builder().build()));
        
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
        AutorRequest request = AutorRequest.builder().nombre(JsonNullable.of("Nuevo Nombre")).version(0L).build();

        Autor existing = Autor.builder().build();
        existing.setId(1L);
        existing.setVersion(0L);
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
                .hasMessageContaining("con ID: 1");
    }

    @Test
    @DisplayName("Should deactivate author")
    void shouldDeactivate() {
        when(autorRepository.existsById(1L)).thenReturn(true);

        autorService.cambiarEstado(1L, false);

        verify(autorRepository).desactivar(1L);
    }

    @Test
    @DisplayName("Should activate author")
    void shouldActivate() {
        when(autorRepository.existsById(1L)).thenReturn(true);

        autorService.cambiarEstado(1L, true);

        verify(autorRepository).activar(1L);
    }

    @Test
    @DisplayName("Should throw when changing state of non-existent author")
    void shouldThrowWhenChangingStateNonExistent() {
        when(autorRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> autorService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con ID: 1");
    }

    @Test
    @DisplayName("Should update author with same name")
    void shouldUpdateAuthorSameName() {
        AutorRequest request = AutorRequest.builder().nombre(JsonNullable.of("Mismo")).version(0L).build();

        Autor existing = Autor.builder().build();
        existing.setId(1L);
        existing.setVersion(0L);
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
        AutorRequest request = AutorRequest.builder().nombre(JsonNullable.of("Existente")).version(0L).build();

        Autor existing = Autor.builder().build();
        existing.setId(1L);
        existing.setVersion(0L);
        existing.setNombre("Viejo");
        when(autorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(autorRepository.existsByNombreIgnoreCase("Existente")).thenReturn(true);

        assertThatThrownBy(() -> autorService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con el nombre: Existente");
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
                .hasMessageContaining("con el nombre: Test");
    }

    @Test
    @DisplayName("Should verify duplicates with trimming")
    void shouldVerifyDuplicatesWithTrimming() {
        Autor autor = Autor.builder().build();
        when(autorRepository.buscarDuplicados(eq("Termino"), eq(1L), any(Sort.class)))
                .thenReturn(List.of(autor));
        when(autorMapper.toResumen(autor)).thenReturn(mock(AutorResumenResponse.class));

        assertThat(autorService.buscarDuplicados("  Termino  ", 1L)).isNotEmpty();
        verify(autorRepository).buscarDuplicados(eq("Termino"), eq(1L), any(Sort.class));
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
                .version(1L)
                .build();

        Autor existing = Autor.builder().build();
        existing.setId(1L);
        existing.setVersion(1L);
        when(autorRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> autorService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("El nombre no puede estar vacío");
    }

    @Test
    @DisplayName("Should throw exception when updating with empty pseudonym if present")
    void shouldThrowWhenUpdatingWithEmptyPseudonym() {
        AutorRequest request = AutorRequest.builder()
                .seudonimo(JsonNullable.of(""))
                .version(1L)
                .build();

        Autor existing = Autor.builder().build();
        existing.setId(1L);
        existing.setVersion(1L);
        when(autorRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> autorService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("El seudonimo no puede estar vacío");
    }

    @Test
    @DisplayName("Should throw exception when updating with empty biography if present")
    void shouldThrowWhenUpdatingWithEmptyBiography() {
        AutorRequest request = AutorRequest.builder()
                .biografia(JsonNullable.of(""))
                .version(1L)
                .build();

        Autor existing = Autor.builder().build();
        existing.setId(1L);
        existing.setVersion(1L);
        when(autorRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> autorService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("La biografia no puede estar vacía");
    }

    @Test
    @DisplayName("Should throw exception when update results in both empty name and pseudonym")
    void shouldThrowWhenUpdatingResultsInEmptyIdentity() {
        AutorRequest request = AutorRequest.builder().version(1L).build(); // No fields sent

        Autor existing = Autor.builder().build();
        existing.setId(1L);
        existing.setVersion(1L);
        existing.setNombre(null);
        existing.setSeudonimo(null);
        when(autorRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> autorService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("El autor debe tener al menos un Nombre o un Seudonimo");
    }

    @Test
    @DisplayName("Should throw exception when birth year is greater than death year after update")
    void shouldThrowWhenUpdatingWithBirthYearAfterDeathYear() {
        AutorRequest request = AutorRequest.builder()
                .anioNacimiento(JsonNullable.of(Year.of(2010)))
                .version(1L)
                .build();

        Autor existing = Autor.builder()
                .id(1L)
                .version(1L)
                .nombre("Existente")
                .anioFallecimiento(Year.of(2000))
                .build();

        when(autorRepository.findById(1L)).thenReturn(Optional.of(existing));
        org.mockito.Mockito.doAnswer(invocation -> {
            AutorRequest req = invocation.getArgument(0);
            Autor ent = invocation.getArgument(1);
            if (req.anioNacimiento() != null && req.anioNacimiento().isPresent()) {
                ent.setAnioNacimiento(req.anioNacimiento().get());
            }
            return null;
        }).when(autorMapper).updateEntity(any(AutorRequest.class), any(Autor.class));

        assertThatThrownBy(() -> autorService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("El año de nacimiento no puede ser mayor al año de fallecimiento");
    }

    @Test
    @DisplayName("Should accumulate errors when multiple relation entities do not exist")
    void shouldAccumulateErrorsWhenMultipleRelationsNotFound() {
        AutorRequest request = AutorRequest.builder()
                .nombre(JsonNullable.of("Nuevo"))
                .biografia(JsonNullable.of("Bio"))
                .idPaisNacimiento(JsonNullable.of(99L))
                .idNacionalidad(JsonNullable.of(88L))
                .idGeneroPrincipal(JsonNullable.of(77L))
                .idObraCumbre(JsonNullable.of(66L))
                .build();

        when(autorRepository.existsByNombreIgnoreCase(any())).thenReturn(false);
        when(paisRepository.findById(99L)).thenReturn(Optional.empty());
        when(nacionalidadRepository.findById(88L)).thenReturn(Optional.empty());
        when(generoRepository.findById(77L)).thenReturn(Optional.empty());
        when(obraRepository.findById(66L)).thenReturn(Optional.empty());

        Autor entity = Autor.builder().build();
        when(autorMapper.toEntity(any())).thenReturn(entity);

        assertThatThrownBy(() -> autorService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .satisfies(ex -> {
                    ValidacionNegocioException exception = (ValidacionNegocioException) ex;
                    assertThat(exception.getErrores()).contains(
                            "El país con ID 99 no existe.",
                            "La nacionalidad con ID 88 no existe.",
                            "El género con ID 77 no existe.",
                            "La obra con ID 66 no existe."
                    );
                });
    }
}
