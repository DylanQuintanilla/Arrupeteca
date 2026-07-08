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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AutorServiceImplTest extends BaseServiceTest {

    @Mock
    private AutorRepository autorRepository;

    @Mock
    private AutorMapper autorMapper;

    @Mock
    private PaisRepository paisRepository;

    @Mock
    private NacionalidadRepository nacionalidadRepository;

    @Mock
    private GeneroRepository generoRepository;

    @Mock
    private ObraRepository obraRepository;

    @InjectMocks
    private AutorServiceImpl autorService;

    @Test
    @DisplayName("Should return all active paginated")
    void shouldReturnAllActive() {
        Autor autor = Autor.builder().build();
        Page<Autor> expectedPage = new PageImpl<>(List.of(autor));
        AutorResumenResponse resumen = mock(AutorResumenResponse.class);

        when(autorRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        when(autorMapper.toResumen(autor)).thenReturn(resumen);

        Page<AutorResumenResponse> result = autorService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(resumen);
    }

    @Test
    @DisplayName("Should return by ID and active")
    void shouldReturnByIdAndActive() {
        Autor autor = Autor.builder().build();
        AutorDetalleResponse detalle = mock(AutorDetalleResponse.class);
        when(autorRepository.findOne(any(Specification.class))).thenReturn(Optional.of(autor));
        when(autorMapper.toDetalleResponse(autor)).thenReturn(detalle);

        AutorDetalleResponse result = autorService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(detalle);
    }

    @Test
    @DisplayName("Should throw exception when not found or inactive")
    void shouldThrowWhenNotFoundOrInactive() {
        when(autorRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> autorService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Autor activo con ID: 1");
    }

    @Test
    @DisplayName("Should return all for admin")
    void shouldReturnAllAdmin() {
        Autor autor = Autor.builder().build();
        Page<Autor> expectedPage = new PageImpl<>(List.of(autor));
        AutorResumenResponse resumen = mock(AutorResumenResponse.class);

        when(autorRepository.findAll((Specification<Autor>) any(), any(Pageable.class))).thenReturn(expectedPage);
        when(autorMapper.toResumen(autor)).thenReturn(resumen);

        Page<AutorResumenResponse> result = autorService.obtenerTodos(Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(resumen);
    }

    @Test
    @DisplayName("Should return by ID")
    void shouldReturnById() {
        Autor autor = Autor.builder().build();
        AutorDetalleResponse detalle = mock(AutorDetalleResponse.class);
        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor));
        when(autorMapper.toDetalleResponse(autor)).thenReturn(detalle);

        AutorDetalleResponse result = autorService.obtenerPorId(1L);

        assertThat(result).isEqualTo(detalle);
    }

    @Test
    @DisplayName("Should throw exception when not found")
    void shouldThrowWhenNotFound() {
        when(autorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> autorService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Autor con ID: 1");
    }

    @Test
    @DisplayName("Should perform global advanced search")
    void shouldPerformGlobalAdvancedSearch() {
        Page<Autor> expectedPage = new PageImpl<>(List.of());
        when(autorRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        Page<AutorResumenResponse> result = autorService.busquedaGlobal("test", Year.of(1900), Year.of(2000), Pageable.unpaged());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should perform master search admin")
    void shouldPerformMasterSearchAdmin() {
        Page<Autor> expectedPage = new PageImpl<>(List.of());
        when(autorRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        Page<AutorResumenResponse> result = autorService.busquedaMaestraAdmin(
                false, 1L, 1L, 1L, "test", Pageable.unpaged()
        );

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should create author with default values and valid relations")
    void shouldCreateAuthor() {
        AutorRequest request = AutorRequest.builder()
                .nombre("Test")
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
        when(autorRepository.save(any())).thenReturn(saved);

        AutorDetalleResponse detalle = mock(AutorDetalleResponse.class);
        when(autorMapper.toDetalleResponse(saved)).thenReturn(detalle);

        AutorDetalleResponse result = autorService.crear(request);

        assertThat(result).isEqualTo(detalle);
        verify(autorRepository).save(any());
    }

    @Test
    @DisplayName("Should create author with null relations")
    void shouldCreateAuthorWithNullRelations() {
        AutorRequest request = AutorRequest.builder().nombre("Test").build();

        when(autorRepository.existsByNombreIgnoreCase(any())).thenReturn(false);

        Autor autorEntity = Autor.builder().build();
        when(autorMapper.toEntity(any())).thenReturn(autorEntity);

        Autor saved = Autor.builder().build();
        saved.setId(1L);
        when(autorRepository.save(any())).thenReturn(saved);

        AutorDetalleResponse detalle = mock(AutorDetalleResponse.class);
        when(autorMapper.toDetalleResponse(saved)).thenReturn(detalle);

        AutorDetalleResponse result = autorService.crear(request);

        assertThat(result).isEqualTo(detalle);
    }

    @Test
    @DisplayName("Should update author")
    void shouldUpdateAuthor() {
        AutorRequest request = AutorRequest.builder().nombre("Nuevo Nombre").build();

        Autor existing = Autor.builder().build();
        existing.setId(1L);
        existing.setNombre("Viejo");
        when(autorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(autorRepository.existsByNombreIgnoreCase("Nuevo Nombre")).thenReturn(false);
        
        Autor saved = Autor.builder().build();
        saved.setId(1L);
        when(autorRepository.save(any())).thenReturn(saved);

        AutorDetalleResponse detalle = mock(AutorDetalleResponse.class);
        when(autorMapper.toDetalleResponse(saved)).thenReturn(detalle);

        AutorDetalleResponse result = autorService.actualizar(1L, request);

        assertThat(result).isEqualTo(detalle);
        verify(autorMapper).updateEntity(request, existing);
        verify(autorRepository).save(existing);
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
        when(autorRepository.existsByIdIncludeDeleted(1L)).thenReturn(true);

        autorService.cambiarEstado(1L, false);

        verify(autorRepository).desactivar(1L);
    }

    @Test
    @DisplayName("Should activate author")
    void shouldActivate() {
        when(autorRepository.existsByIdIncludeDeleted(1L)).thenReturn(true);

        autorService.cambiarEstado(1L, true);

        verify(autorRepository).activar(1L);
    }

    @Test
    @DisplayName("Should throw when changing state of non-existent author")
    void shouldThrowWhenChangingStateNonExistent() {
        when(autorRepository.existsByIdIncludeDeleted(1L)).thenReturn(false);

        assertThatThrownBy(() -> autorService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Autor con ID: 1");
    }

    @Test
    @DisplayName("Should update author with same name")
    void shouldUpdateAuthorSameName() {
        AutorRequest request = AutorRequest.builder().nombre("Mismo").build();

        Autor existing = Autor.builder().build();
        existing.setId(1L);
        existing.setNombre("Mismo");
        when(autorRepository.findById(1L)).thenReturn(Optional.of(existing));
        
        Autor saved = Autor.builder().build();
        saved.setId(1L);
        when(autorRepository.save(any())).thenReturn(saved);

        AutorDetalleResponse detalle = mock(AutorDetalleResponse.class);
        when(autorMapper.toDetalleResponse(saved)).thenReturn(detalle);

        AutorDetalleResponse result = autorService.actualizar(1L, request);

        assertThat(result).isEqualTo(detalle);
    }

    @Test
    @DisplayName("Should throw when updating author with name that already exists")
    void shouldThrowWhenUpdateNameExists() {
        AutorRequest request = AutorRequest.builder().nombre("Existente").build();

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
        AutorRequest request = AutorRequest.builder().nombre("Test").build();
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
}
