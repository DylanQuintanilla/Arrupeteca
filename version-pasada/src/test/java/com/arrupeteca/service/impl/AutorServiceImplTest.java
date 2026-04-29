package com.arrupeteca.service.impl;

import java.util.List;
import java.util.Optional;

import com.arrupeteca.dto.request.AutorRequest;
import com.arrupeteca.dto.response.detail.AutorDetalleResponse;
import com.arrupeteca.dto.response.summary.AutorResumen;
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
        Autor autor = new Autor();
        Page<Autor> expectedPage = new PageImpl<>(List.of(autor));
        AutorResumen resumen = mock(AutorResumen.class);

        when(autorRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(expectedPage);
        when(autorMapper.toResumen(autor)).thenReturn(resumen);

        Page<AutorResumen> result = autorService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(resumen);
    }

    @Test
    @DisplayName("Should return by ID and active")
    void shouldReturnByIdAndActive() {
        Autor autor = new Autor();
        AutorDetalleResponse detalle = mock(AutorDetalleResponse.class);
        when(autorRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(autor));
        when(autorMapper.toDetalleResponse(autor)).thenReturn(detalle);

        AutorDetalleResponse result = autorService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(detalle);
    }

    @Test
    @DisplayName("Should throw exception when not found or inactive")
    void shouldThrowWhenNotFoundOrInactive() {
        when(autorRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> autorService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Autor activo con ID: 1");
    }

    @Test
    @DisplayName("Should return all for admin")
    void shouldReturnAllAdmin() {
        Autor autor = new Autor();
        Page<Autor> expectedPage = new PageImpl<>(List.of(autor));
        AutorResumen resumen = mock(AutorResumen.class);

        when(autorRepository.findAllProjectedBy(any(Pageable.class))).thenReturn(expectedPage);
        when(autorMapper.toResumen(autor)).thenReturn(resumen);

        Page<AutorResumen> result = autorService.obtenerTodos(Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(resumen);
    }

    @Test
    @DisplayName("Should return by ID")
    void shouldReturnById() {
        Autor autor = new Autor();
        AutorDetalleResponse detalle = mock(AutorDetalleResponse.class);
        when(autorRepository.findProjectedById(1L)).thenReturn(Optional.of(autor));
        when(autorMapper.toDetalleResponse(autor)).thenReturn(detalle);

        AutorDetalleResponse result = autorService.obtenerPorId(1L);

        assertThat(result).isEqualTo(detalle);
    }

    @Test
    @DisplayName("Should throw exception when not found")
    void shouldThrowWhenNotFound() {
        when(autorRepository.findProjectedById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> autorService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Autor con ID: 1");
    }

    @Test
    @DisplayName("Should perform global advanced search")
    void shouldPerformGlobalAdvancedSearch() {
        Page<Autor> expectedPage = new PageImpl<>(List.of());
        when(autorRepository.busquedaGlobalAvanzada(any(), any(), any(), any())).thenReturn(expectedPage);

        Page<AutorResumen> result = autorService.busquedaGlobal("test", 1900, 2000, Pageable.unpaged());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should perform master search admin")
    void shouldPerformMasterSearchAdmin() {
        Page<Autor> expectedPage = new PageImpl<>(List.of());
        when(autorRepository.busquedaMaestraAdmin(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(expectedPage);

        Page<AutorResumen> result = autorService.busquedaMaestraAdmin(
                false, 1L, 1L, 1L, 1L, "test", 1900, 2000, Pageable.unpaged()
        );

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should find alive authors in range")
    void shouldFindAliveAuthorsInRange() {
        Page<Autor> expectedPage = new PageImpl<>(List.of());
        when(autorRepository.buscarAutoresVivosEnRango(any(), any(), any())).thenReturn(expectedPage);

        Page<AutorResumen> result = autorService.buscarVivosEnRango(1900, 2000, Pageable.unpaged());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should create author with default values and valid relations")
    void shouldCreateAuthor() {
        AutorRequest request = new AutorRequest();
        request.setNombre("Test");
        request.setIdPaisNacimiento(1L);
        request.setIdNacionalidad(1L);
        request.setIdGeneroPrincipal(1L);
        request.setIdObraCumbre(1L);

        when(autorRepository.existsByNombreIgnoreCase(any())).thenReturn(false);
        
        Autor autorEntity = new Autor();
        when(autorMapper.toEntity(any())).thenReturn(autorEntity);

        Autor saved = new Autor();
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
        AutorRequest request = new AutorRequest();
        request.setNombre("Test");

        when(autorRepository.existsByNombreIgnoreCase(any())).thenReturn(false);

        Autor autorEntity = new Autor();
        when(autorMapper.toEntity(any())).thenReturn(autorEntity);

        Autor saved = new Autor();
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
        AutorRequest request = new AutorRequest();
        request.setNombre("Nuevo Nombre");

        Autor existing = new Autor();
        existing.setId(1L);
        existing.setNombre("Viejo");
        when(autorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(autorRepository.existsByNombreIgnoreCase("Nuevo Nombre")).thenReturn(false);
        
        Autor saved = new Autor();
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

        assertThatThrownBy(() -> autorService.actualizar(1L, new AutorRequest()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Autor con ID: 1");
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
                .hasMessageContaining("No se encontró Autor con ID: 1");
    }

    @Test
    @DisplayName("Should update author with same name")
    void shouldUpdateAuthorSameName() {
        AutorRequest request = new AutorRequest();
        request.setNombre("Mismo");

        Autor existing = new Autor();
        existing.setId(1L);
        existing.setNombre("Mismo");
        when(autorRepository.findById(1L)).thenReturn(Optional.of(existing));
        
        Autor saved = new Autor();
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
        AutorRequest request = new AutorRequest();
        request.setNombre("Existente");

        Autor existing = new Autor();
        existing.setId(1L);
        existing.setNombre("Viejo");
        when(autorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(autorRepository.existsByNombreIgnoreCase("Existente")).thenReturn(true);

        assertThatThrownBy(() -> autorService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe otro autor con el nombre: Existente");
    }

    @Test
    @DisplayName("Should throw exception if author name exists during creation")
    void crearNombreExists() {
        AutorRequest request = new AutorRequest();
        request.setNombre("Test");
        when(autorRepository.existsByNombreIgnoreCase("Test")).thenReturn(true);

        assertThatThrownBy(() -> autorService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un autor con el nombre: Test");
    }

    @Test
    @DisplayName("Should verify duplicates with trimming")
    void shouldVerifyDuplicatesWithTrimming() {
        Autor autor = new Autor();
        when(autorRepository.buscarPosiblesDuplicados("Termino", 1L))
                .thenReturn(List.of(autor));
        when(autorMapper.toResumen(autor)).thenReturn(mock(AutorResumen.class));

        assertThat(autorService.buscarDuplicados("  Termino  ", 1L)).isNotEmpty();
        verify(autorRepository).buscarPosiblesDuplicados("Termino", 1L);
    }
}
