package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.ObraAutorRequest;
import com.arrupeteca.dto.request.ObraRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.ObraMapper;
import com.arrupeteca.persistence.entity.Autor;
import com.arrupeteca.persistence.entity.Categoria;
import com.arrupeteca.persistence.entity.Genero;
import com.arrupeteca.persistence.entity.Obra;
import com.arrupeteca.persistence.entity.TipoAutoria;
import com.arrupeteca.persistence.projection.ObraResumen;
import com.arrupeteca.persistence.repository.AutorRepository;
import com.arrupeteca.persistence.repository.CategoriaRepository;
import com.arrupeteca.persistence.repository.GeneroRepository;
import com.arrupeteca.persistence.repository.ObraRepository;
import com.arrupeteca.persistence.repository.TipoAutoriaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

class ObraServiceImplTest extends BaseServiceTest {
    @Mock private ObraRepository repository;
    @Mock private GeneroRepository generoRepository;
    @Mock private CategoriaRepository categoriaRepository;
    @Mock private AutorRepository autorRepository;
    @Mock private TipoAutoriaRepository tipoAutoriaRepository;
    @Mock private ObraMapper mapper;
    @InjectMocks private ObraServiceImpl service;

    @Test
    @DisplayName("Should return paginated active obras")
    void shouldReturnPaginatedActive() {
        Page<ObraResumen> page = new PageImpl<>(List.of());
        when(repository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerTodosActivos(Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    @DisplayName("Should return obra by id when active")
    void shouldReturnByIdActivo() {
        ObraResumen resumen = mock(ObraResumen.class);
        when(repository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(resumen));
        assertThat(service.obtenerPorIdActivo(1L)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw exception when active obra not found")
    void shouldThrowWhenActiveNotFound() {
        when(repository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Obra activa con ID: 1");
    }

    @Test
    @DisplayName("Should return all obras")
    void shouldReturnAll() {
        Page<ObraResumen> page = new PageImpl<>(List.of());
        when(repository.findAllProjectedBy(any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerTodos(Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    @DisplayName("Should return search results")
    void shouldReturnSearch() {
        Page<ObraResumen> page = new PageImpl<>(List.of());
        when(repository.busquedaGlobal(anyString(), any(Pageable.class))).thenReturn(page);
        assertThat(service.busquedaGlobal("test", Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    @DisplayName("Should return obra by id")
    void shouldReturnById() {
        ObraResumen resumen = mock(ObraResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));
        assertThat(service.obtenerPorId(1L)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw exception when obra not found by id")
    void shouldThrowWhenNotFoundById() {
        when(repository.findProjectedById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Obra con ID: 1");
    }

    @Test
    @DisplayName("Should return busqueda maestra admin results")
    void shouldReturnBusquedaMaestraAdmin() {
        Page<ObraResumen> page = new PageImpl<>(List.of());
        when(repository.busquedaMaestraAdmin(any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(page);
        assertThat(service.busquedaMaestraAdmin(false, "term", 2020, 2024, Pageable.unpaged()))
                .isEqualTo(page);
        verify(repository).busquedaMaestraAdmin(false, "term", 2020, 2024, Pageable.unpaged());
    }

    @Test
    @DisplayName("Should create obra with relations")
    void shouldCreateWithRelations() {
        ObraRequest request = new ObraRequest();
        request.setTitulo("Obra 1");
        request.setIdGeneros(Set.of(1L));
        request.setIdCategorias(Set.of(1L));
        
        ObraAutorRequest autorReq = new ObraAutorRequest();
        autorReq.setIdAutor(1L);
        autorReq.setIdTipoAutoria(1L);
        request.setAutores(Set.of(autorReq));

        when(repository.existsByTituloIgnoreCase("Obra 1")).thenReturn(false);
        when(generoRepository.findAllById(Set.of(1L))).thenReturn(List.of(mock(Genero.class)));
        when(categoriaRepository.findAllById(Set.of(1L))).thenReturn(List.of(mock(Categoria.class)));
        when(autorRepository.findById(1L)).thenReturn(Optional.of(mock(Autor.class)));
        when(tipoAutoriaRepository.findById(1L)).thenReturn(Optional.of(mock(TipoAutoria.class)));

        Obra entity = new Obra();
        entity.setId(1L);
        entity.setObraAutores(new HashSet<>());
        when(mapper.toEntity(any())).thenReturn(entity);
        when(repository.save(any())).thenReturn(entity);

        ObraResumen resumen = mock(ObraResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        assertThat(service.crear(request)).isEqualTo(resumen);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("Should throw exception when title exists on create")
    void shouldThrowWhenTitleExistsOnCreate() {
        ObraRequest request = new ObraRequest();
        request.setTitulo("Obra 1");
        when(repository.existsByTituloIgnoreCase("Obra 1")).thenReturn(true);

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe una obra con el título: Obra 1");
    }

    @Test
    @DisplayName("Should throw exception when genre not found on create")
    void shouldThrowWhenGenreNotFoundOnCreate() {
        ObraRequest request = new ObraRequest();
        request.setTitulo("Obra 1");
        request.setIdGeneros(Set.of(1L, 2L));

        when(repository.existsByTituloIgnoreCase("Obra 1")).thenReturn(false);
        when(generoRepository.findAllById(Set.of(1L, 2L))).thenReturn(List.of(mock(Genero.class)));
        when(mapper.toEntity(any())).thenReturn(new Obra());

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Uno o más Géneros indicados no existen");
    }

    @Test
    @DisplayName("Should throw exception when category not found on create")
    void shouldThrowWhenCategoryNotFoundOnCreate() {
        ObraRequest request = new ObraRequest();
        request.setTitulo("Obra 1");
        request.setIdCategorias(Set.of(1L, 2L));

        when(repository.existsByTituloIgnoreCase("Obra 1")).thenReturn(false);
        when(categoriaRepository.findAllById(Set.of(1L, 2L))).thenReturn(List.of(mock(Categoria.class)));
        when(mapper.toEntity(any())).thenReturn(new Obra());

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Una o más Categorías indicadas no existen");
    }

    @Test
    @DisplayName("Should throw exception when author not found on create")
    void shouldThrowWhenAuthorNotFoundOnCreate() {
        ObraRequest request = new ObraRequest();
        request.setTitulo("Obra 1");
        ObraAutorRequest autorReq = new ObraAutorRequest();
        autorReq.setIdAutor(1L);
        request.setAutores(Set.of(autorReq));

        when(repository.existsByTituloIgnoreCase("Obra 1")).thenReturn(false);
        when(autorRepository.findById(1L)).thenReturn(Optional.empty());
        when(mapper.toEntity(any())).thenReturn(new Obra());

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Autor con ID 1 no encontrado");
    }

    @Test
    @DisplayName("Should throw exception when tipoAutoria not found on create")
    void shouldThrowWhenTipoAutoriaNotFoundOnCreate() {
        ObraRequest request = new ObraRequest();
        request.setTitulo("Obra 1");
        ObraAutorRequest autorReq = new ObraAutorRequest();
        autorReq.setIdAutor(1L);
        autorReq.setIdTipoAutoria(1L);
        request.setAutores(Set.of(autorReq));

        when(repository.existsByTituloIgnoreCase("Obra 1")).thenReturn(false);
        when(autorRepository.findById(1L)).thenReturn(Optional.of(mock(Autor.class)));
        when(tipoAutoriaRepository.findById(1L)).thenReturn(Optional.empty());
        when(mapper.toEntity(any())).thenReturn(new Obra());

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Tipo de Autoría con ID 1 no encontrado");
    }

    @Test
    @DisplayName("Should update obra")
    void shouldUpdate() {
        ObraRequest request = new ObraRequest();
        request.setTitulo("Obra 2");
        request.setIdGeneros(new HashSet<>());
        request.setIdCategorias(new HashSet<>());
        request.setAutores(new HashSet<>());

        Obra existing = new Obra();
        existing.setId(1L);
        existing.setTitulo("Obra 1");
        existing.setObraAutores(new HashSet<>());

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existsByTituloIgnoreCase("Obra 2")).thenReturn(false);

        ObraResumen resumen = mock(ObraResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        assertThat(service.actualizar(1L, request)).isEqualTo(resumen);
        verify(repository, atLeastOnce()).save(existing);
    }

    @Test
    @DisplayName("Should throw exception when genre not found on update")
    void shouldThrowWhenGenreNotFoundOnUpdate() {
        ObraRequest request = new ObraRequest();
        request.setTitulo("Obra 1");
        request.setIdGeneros(Set.of(1L));

        Obra existing = new Obra();
        existing.setId(1L);
        existing.setTitulo("Obra 1");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(generoRepository.findAllById(any())).thenReturn(List.of());

        assertThatThrownBy(() -> service.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("Should throw exception when category not found on update")
    void shouldThrowWhenCategoryNotFoundOnUpdate() {
        ObraRequest request = new ObraRequest();
        request.setTitulo("Obra 1");
        request.setIdCategorias(Set.of(1L));

        Obra existing = new Obra();
        existing.setId(1L);
        existing.setTitulo("Obra 1");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoriaRepository.findAllById(any())).thenReturn(List.of());

        assertThatThrownBy(() -> service.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("Should throw exception when obra not found on update")
    void shouldThrowWhenNotFoundOnUpdate() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.actualizar(1L, new ObraRequest()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Obra con ID: 1");
    }

    @Test
    @DisplayName("Should throw exception when title already in use on update")
    void shouldThrowWhenTitleInUseOnUpdate() {
        ObraRequest request = new ObraRequest();
        request.setTitulo("Obra 2");

        Obra existing = new Obra();
        existing.setId(1L);
        existing.setTitulo("Obra 1");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existsByTituloIgnoreCase("Obra 2")).thenReturn(true);

        assertThatThrownBy(() -> service.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe una obra con el título: Obra 2");
    }

    @Test
    @DisplayName("Should handle null fields in existing obra during update")
    void shouldHandleNullFieldsOnUpdate() {
        ObraRequest request = new ObraRequest();
        request.setTitulo("Obra 1");
        request.setIdGeneros(Set.of(1L));
        request.setIdCategorias(Set.of(1L));

        Obra existing = new Obra();
        existing.setId(1L);
        existing.setTitulo("Obra 1");
        existing.setGeneros(null); // Explicitly null
        existing.setCategorias(null); // Explicitly null
        existing.setObraAutores(new HashSet<>());

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(generoRepository.findAllById(any())).thenReturn(List.of(mock(Genero.class)));
        when(categoriaRepository.findAllById(any())).thenReturn(List.of(mock(Categoria.class)));

        ObraResumen resumen = mock(ObraResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        assertThat(service.actualizar(1L, request)).isEqualTo(resumen);
        assertThat(existing.getGeneros()).isNotNull();
        assertThat(existing.getCategorias()).isNotNull();
    }

    @Test
    @DisplayName("Should activate obra")
    void shouldActivate() {
        when(repository.existsById(1L)).thenReturn(true);
        service.cambiarEstado(1L, true);
        verify(repository).activar(1L);
    }

    @Test
    @DisplayName("Should deactivate obra")
    void shouldDeactivate() {
        when(repository.existsById(1L)).thenReturn(true);
        service.cambiarEstado(1L, false);
        verify(repository).desactivar(1L);
    }

    @Test
    @DisplayName("Should throw exception when obra not found on status change")
    void shouldThrowWhenNotFoundOnStatusChange() {
        when(repository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> service.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Obra con ID: 1");
        verify(repository, never()).activar(any());
        verify(repository, never()).desactivar(any());
    }
}

