package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.AutorRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.AutorMapper;
import com.arrupeteca.persistence.entity.Autor;
import com.arrupeteca.persistence.entity.Genero;
import com.arrupeteca.persistence.entity.Nacionalidad;
import com.arrupeteca.persistence.entity.Obra;
import com.arrupeteca.persistence.entity.Pais;
import com.arrupeteca.persistence.projection.AutorResumen;
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

import java.util.List;
import java.util.Optional;

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
        Page<AutorResumen> expectedPage = new PageImpl<>(List.of(mock(AutorResumen.class)));
        when(autorRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(expectedPage);

        Page<AutorResumen> result = autorService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    @DisplayName("Should return by ID and active")
    void shouldReturnByIdAndActive() {
        AutorResumen resumen = mock(AutorResumen.class);
        when(autorRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(resumen));

        AutorResumen result = autorService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(resumen);
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
        Page<AutorResumen> expectedPage = new PageImpl<>(List.of(mock(AutorResumen.class)));
        when(autorRepository.findAllProjectedBy(any(Pageable.class))).thenReturn(expectedPage);

        Page<AutorResumen> result = autorService.obtenerTodos(Pageable.unpaged());

        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    @DisplayName("Should return by ID")
    void shouldReturnById() {
        AutorResumen resumen = mock(AutorResumen.class);
        when(autorRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        AutorResumen result = autorService.obtenerPorId(1L);

        assertThat(result).isEqualTo(resumen);
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
        Page<AutorResumen> expectedPage = new PageImpl<>(List.of());
        when(autorRepository.busquedaGlobalAvanzada(any(), any(), any(), any())).thenReturn(expectedPage);

        Page<AutorResumen> result = autorService.busquedaGlobalAvanzada("test", 1900, 2000, Pageable.unpaged());

        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    @DisplayName("Should perform master search admin")
    void shouldPerformMasterSearchAdmin() {
        Page<AutorResumen> expectedPage = new PageImpl<>(List.of());
        when(autorRepository.busquedaMaestraAdmin(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(expectedPage);

        Page<AutorResumen> result = autorService.busquedaMaestraAdmin(false, 1L, 1L, 1L, 1L, "test", 1900, 2000, Pageable.unpaged());

        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    @DisplayName("Should find alive authors in range")
    void shouldFindAliveAuthorsInRange() {
        Page<AutorResumen> expectedPage = new PageImpl<>(List.of());
        when(autorRepository.buscarAutoresVivosEnRango(any(), any(), any())).thenReturn(expectedPage);

        Page<AutorResumen> result = autorService.buscarAutoresVivosEnRango(1900, 2000, Pageable.unpaged());

        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    @DisplayName("Should create author with default values and valid relations")
    void shouldCreateAuthor() {
        AutorRequest request = new AutorRequest();
        // Missing name and pseudonym to test defaults
        request.setIdPaisNacimiento(1L);
        request.setIdNacionalidad(1L);
        request.setIdGeneroPrincipal(1L);
        request.setIdObraCumbre(1L);

        when(paisRepository.findById(1L)).thenReturn(Optional.of(new Pais()));
        when(nacionalidadRepository.findById(1L)).thenReturn(Optional.of(new Nacionalidad()));
        when(generoRepository.findById(1L)).thenReturn(Optional.of(new Genero()));
        when(obraRepository.findById(1L)).thenReturn(Optional.of(new Obra()));

        Autor autorEntity = new Autor();
        when(autorMapper.toEntity(any())).thenReturn(autorEntity);

        Autor saved = new Autor();
        saved.setId(1L);
        when(autorRepository.save(any())).thenReturn(saved);

        AutorResumen resumen = mock(AutorResumen.class);
        when(autorRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        AutorResumen result = autorService.crear(request);

        assertThat(result).isEqualTo(resumen);
        assertThat(request.getNombre()).isEqualTo("Nombre Desconocido");
        assertThat(request.getSeudonimo()).isEqualTo("Seudonimo Desconocido");
        verify(autorRepository).save(any());
    }

    @Test
    @DisplayName("Should create author with null relations")
    void shouldCreateAuthorWithNullRelations() {
        AutorRequest request = new AutorRequest();
        request.setNombre("Test");
        request.setSeudonimo("Test");
        // IDs are null by default

        Autor autorEntity = new Autor();
        when(autorMapper.toEntity(any())).thenReturn(autorEntity);

        Autor saved = new Autor();
        saved.setId(1L);
        when(autorRepository.save(any())).thenReturn(saved);

        AutorResumen resumen = mock(AutorResumen.class);
        when(autorRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        AutorResumen result = autorService.crear(request);

        assertThat(result).isEqualTo(resumen);
        assertThat(autorEntity.getPaisNacimiento()).isNull();
        assertThat(autorEntity.getNacionalidad()).isNull();
        assertThat(autorEntity.getGeneroPrincipal()).isNull();
        assertThat(autorEntity.getObraCumbre()).isNull();
    }

    @Test
    @DisplayName("Should throw exception when relations do not exist")
    void shouldThrowWhenRelationsDoNotExist() {
        AutorRequest request = new AutorRequest();
        request.setNombre("Autor");
        request.setSeudonimo("Seudonimo");
        request.setIdPaisNacimiento(1L);
        request.setIdNacionalidad(1L);
        request.setIdGeneroPrincipal(1L);
        request.setIdObraCumbre(1L);

        when(paisRepository.findById(1L)).thenReturn(Optional.empty());
        when(nacionalidadRepository.findById(1L)).thenReturn(Optional.empty());
        when(generoRepository.findById(1L)).thenReturn(Optional.empty());
        when(obraRepository.findById(1L)).thenReturn(Optional.empty());

        when(autorMapper.toEntity(any())).thenReturn(new Autor());

        assertThatThrownBy(() -> autorService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .satisfies(ex -> {
                    ValidacionNegocioException vne = (ValidacionNegocioException) ex;
                    assertThat(vne.getMessage()).isEqualTo("Se encontraron múltiples errores de validación");
                    assertThat(vne.getErrores()).hasSize(4);
                    assertThat(vne.getErrores().get(0)).contains("no existe");
                });
    }

    @Test
    @DisplayName("Should update author")
    void shouldUpdateAuthor() {
        AutorRequest request = new AutorRequest();
        request.setNombre("Nuevo Nombre");
        request.setSeudonimo("Nuevo Seudonimo");

        Autor existing = new Autor();
        existing.setId(1L);
        when(autorRepository.findById(1L)).thenReturn(Optional.of(existing));

        AutorResumen resumen = mock(AutorResumen.class);
        when(autorRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        AutorResumen result = autorService.actualizar(1L, request);

        assertThat(result).isEqualTo(resumen);
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

        verify(autorRepository).desactivarAutor(1L);
    }

    @Test
    @DisplayName("Should activate author")
    void shouldActivate() {
        when(autorRepository.existsById(1L)).thenReturn(true);

        autorService.cambiarEstado(1L, true);

        verify(autorRepository).activarAutor(1L);
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
    @DisplayName("Should verify duplicates with various terms")
    void shouldVerifyDuplicatesWithTerms() {
        when(autorRepository.buscarPosiblesDuplicados("Termino", 1L)).thenReturn(List.of(mock(AutorResumen.class)));

        assertThat(autorService.verificarDuplicados("Termino", 1L)).isNotEmpty();
        assertThat(autorService.verificarDuplicados(null, 1L)).isEmpty();
        assertThat(autorService.verificarDuplicados("   ", 1L)).isEmpty();
        assertThat(autorService.verificarDuplicados("Seudonimo Desconocido", 1L)).isEmpty();
        assertThat(autorService.verificarDuplicados("Nombre Desconocido", 1L)).isEmpty();
    }

    @Test
    @DisplayName("Should update author with blank name and pseudonym using defaults")
    void shouldUpdateAuthorWithBlanks() {
        AutorRequest request = new AutorRequest();
        request.setNombre(""); // Blank
        request.setSeudonimo(null); // Null

        Autor existing = new Autor();
        existing.setId(1L);
        when(autorRepository.findById(1L)).thenReturn(Optional.of(existing));

        AutorResumen resumen = mock(AutorResumen.class);
        when(autorRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        AutorResumen result = autorService.actualizar(1L, request);

        assertThat(result).isEqualTo(resumen);
        assertThat(request.getNombre()).isEqualTo("Nombre Desconocido");
        assertThat(request.getSeudonimo()).isEqualTo("Seudonimo Desconocido");
        verify(autorRepository).save(existing);
    }

    @Test
    @DisplayName("Should throw exception when relations do not exist on update")
    void shouldThrowWhenRelationsDoNotExistOnUpdate() {
        AutorRequest request = new AutorRequest();
        request.setIdPaisNacimiento(1L);

        Autor existing = new Autor();
        existing.setId(1L);
        when(autorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(paisRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> autorService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .satisfies(ex -> {
                    ValidacionNegocioException vne = (ValidacionNegocioException) ex;
                    assertThat(vne.getErrores()).isNotEmpty();
                });
    }
}

