package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.ObraRequest;
import com.arrupeteca.dto.request.ObraAutorRequest;
import com.arrupeteca.dto.response.detail.ObraDetalleResponse;
import com.arrupeteca.dto.response.summary.ObraResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.ObraMapper;
import com.arrupeteca.persistence.entity.Obra;
import com.arrupeteca.persistence.entity.Genero;
import com.arrupeteca.persistence.entity.Categoria;
import com.arrupeteca.persistence.entity.Autor;
import com.arrupeteca.persistence.entity.TipoAutoria;
import com.arrupeteca.persistence.repository.ObraRepository;
import com.arrupeteca.persistence.repository.GeneroRepository;
import com.arrupeteca.persistence.repository.CategoriaRepository;
import com.arrupeteca.persistence.repository.AutorRepository;
import com.arrupeteca.persistence.repository.TipoAutoriaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ObraServiceImplTest extends BaseServiceTest {

    @Mock
    private ObraRepository obraRepository;

    @Mock
    private GeneroRepository generoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private AutorRepository autorRepository;

    @Mock
    private TipoAutoriaRepository tipoAutoriaRepository;

    @Mock
    private ObraMapper obraMapper;

    @InjectMocks
    private ObraServiceImpl obraService;

    @Test
    @DisplayName("obtenerTodosActivos - Should return page of Resumen")
    void obtenerTodosActivos() {
        ObraResumen resumen = new ObraResumen(1L, "Test", 2021, "Resumen", false);
        Page<ObraResumen> page = new PageImpl<>(List.of(resumen));

        when(obraRepository.busquedaMaestraAdminProyectada(any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

        Page<ObraResumen> result = obraService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should return DetalleResponse")
    void obtenerPorIdActivo() {
        Obra entity = Obra.builder().id(1L).titulo("Test").build();
        ObraDetalleResponse detail = ObraDetalleResponse.builder()
                .id(1L).titulo("Test").anioCreacion(2021).resumen("Resumen").version(0L).borradoLogico(false)
                .creadoPor("admin").modificadoPor("admin")
                .generos(Collections.emptySet()).categorias(Collections.emptySet()).etiquetas(Collections.emptySet()).autores(Collections.emptySet()).build();

        when(obraRepository.findByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(obraMapper.toDetalleResponse(entity)).thenReturn(detail);

        ObraDetalleResponse result = obraService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should throw exception when not found")
    void obtenerPorIdActivoNotFound() {
        when(obraRepository.findByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> obraService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Obra activa con ID: 1");
    }

    @Test
    @DisplayName("crear - Should save and return DetalleResponse")
    void crear() {
        ObraRequest request = ObraRequest.builder()
                .titulo(JsonNullable.of("Test"))
                .resumen(JsonNullable.of("Resumen de la obra"))
                .idGeneros(JsonNullable.of(Set.of(1L)))
                .idCategorias(JsonNullable.of(Set.of(1L)))
                .autores(JsonNullable.of(Set.of(new ObraAutorRequest(1L, 1L))))
                .build();
        Obra entity = Obra.builder().id(1L).titulo("Test").build();
        ObraDetalleResponse detail = mock(ObraDetalleResponse.class);

        when(obraRepository.existsByTituloIgnoreCase("Test")).thenReturn(false);
        when(generoRepository.findById(1L)).thenReturn(Optional.of(Genero.builder().build()));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(Categoria.builder().build()));
        when(autorRepository.findById(1L)).thenReturn(Optional.of(Autor.builder().build()));
        when(tipoAutoriaRepository.findById(1L)).thenReturn(Optional.of(TipoAutoria.builder().build()));

        when(obraMapper.toEntity(request)).thenReturn(entity);
        when(obraRepository.save(any(Obra.class))).thenReturn(entity);
        when(obraMapper.toDetalleResponse(entity)).thenReturn(detail);

        ObraDetalleResponse result = obraService.crear(request);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("crear - Should throw exception if title exists")
    void crearTituloExists() {
        ObraRequest request = ObraRequest.builder()
                .titulo(JsonNullable.of("Test"))
                .resumen(JsonNullable.of("Resumen de la obra"))
                .idGeneros(JsonNullable.of(Set.of(1L)))
                .idCategorias(JsonNullable.of(Set.of(1L)))
                .autores(JsonNullable.of(Set.of(new ObraAutorRequest(1L, 1L))))
                .build();

        when(obraRepository.existsByTituloIgnoreCase("Test")).thenReturn(true);

        assertThatThrownBy(() -> obraService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe una obra con el título: Test");
    }

    @Test
    @DisplayName("actualizar - Should update and return DetalleResponse")
    void actualizar() {
        ObraRequest request = ObraRequest.builder()
                .titulo(JsonNullable.of("Nuevo"))
                .resumen(JsonNullable.of("Nuevo resumen"))
                .idGeneros(JsonNullable.of(Set.of(1L)))
                .idCategorias(JsonNullable.of(Set.of(1L)))
                .autores(JsonNullable.of(Set.of(new ObraAutorRequest(1L, 1L))))
                .version(0L)
                .build();
        Obra existing = Obra.builder().id(1L).titulo("Viejo").version(0L).build();
        Obra saved = Obra.builder().id(1L).titulo("Nuevo").build();
        ObraDetalleResponse detail = mock(ObraDetalleResponse.class);

        when(obraRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(obraRepository.existsByTituloIgnoreCase("Nuevo")).thenReturn(false);
        when(generoRepository.findById(1L)).thenReturn(Optional.of(Genero.builder().build()));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(Categoria.builder().build()));
        when(autorRepository.findById(1L)).thenReturn(Optional.of(Autor.builder().build()));
        when(tipoAutoriaRepository.findById(1L)).thenReturn(Optional.of(TipoAutoria.builder().build()));

        when(obraRepository.save(existing)).thenReturn(saved);
        when(obraMapper.toDetalleResponse(saved)).thenReturn(detail);

        ObraDetalleResponse result = obraService.actualizar(1L, request);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("actualizar - Should throw when not found")
    void actualizarNotFound() {
        when(obraRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> obraService.actualizar(1L, ObraRequest.builder().build()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Obra con ID: 1");
    }

    @Test
    @DisplayName("cambiarEstado - Should activate work")
    void activarObra() {
        when(obraRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);
        obraService.cambiarEstado(1L, true);
        verify(obraRepository).activar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should deactivate work")
    void desactivarObra() {
        when(obraRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);
        obraService.cambiarEstado(1L, false);
        verify(obraRepository).desactivar(1L);
    }
}
