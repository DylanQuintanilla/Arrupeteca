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
import static org.mockito.ArgumentMatchers.eq;
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
    @DisplayName("busquedaAdmin - Should return page of Resumen")
    void busquedaAdmin() {
        ObraResumen resumen = new ObraResumen(1L, "Test", 2021, "Resumen", false);
        Page<ObraResumen> page = new PageImpl<>(List.of(resumen));

        when(obraRepository.busqueda(eq(true), any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

        Page<ObraResumen> result = obraService.busquedaAdmin(true, null, null, null, null, null, null, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("busquedaAdminPorId - Should return DetalleResponse")
    void busquedaAdminPorId() {
        Obra entity = Obra.builder().id(1L).titulo("Test").build();
        ObraDetalleResponse detail = mock(ObraDetalleResponse.class);

        when(obraRepository.busquedaPorId(1L, null)).thenReturn(Optional.of(entity));
        when(obraMapper.toDetalleResponse(entity)).thenReturn(detail);

        ObraDetalleResponse result = obraService.busquedaAdminPorId(1L);

        assertThat(result).isEqualTo(detail);
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
        when(obraRepository.saveAndFlush(any(Obra.class))).thenReturn(entity);
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

        when(obraRepository.busquedaPorId(1L, null)).thenReturn(Optional.of(existing));
        when(obraRepository.existsByTituloIgnoreCase("Nuevo")).thenReturn(false);
        when(generoRepository.findById(1L)).thenReturn(Optional.of(Genero.builder().build()));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(Categoria.builder().build()));
        when(autorRepository.findById(1L)).thenReturn(Optional.of(Autor.builder().build()));
        when(tipoAutoriaRepository.findById(1L)).thenReturn(Optional.of(TipoAutoria.builder().build()));

        when(obraRepository.saveAndFlush(existing)).thenReturn(saved);
        when(obraMapper.toDetalleResponse(saved)).thenReturn(detail);

        ObraDetalleResponse result = obraService.actualizar(1L, request);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("actualizar - Should throw when not found")
    void actualizarNotFound() {
        when(obraRepository.busquedaPorId(1L, null)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> obraService.actualizar(1L, ObraRequest.builder().build()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con ID: 1");
    }

    @Test
    @DisplayName("cambiarEstado - Should activate work")
    void activarObra() {
        when(obraRepository.existsById(1L)).thenReturn(true);
        obraService.cambiarEstado(1L, true);
        verify(obraRepository).activar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should deactivate work")
    void desactivarObra() {
        when(obraRepository.existsById(1L)).thenReturn(true);
        obraService.cambiarEstado(1L, false);
        verify(obraRepository).desactivar(1L);
    }

    @Test
    @DisplayName("buscarDuplicados - Should return list of duplicates")
    void buscarDuplicados() {
        Obra obra = Obra.builder().id(1L).titulo("Cien años de soledad").build();
        ObraResumen resumen = new ObraResumen(1L, "Cien años de soledad", 1967, "Resumen", false);

        when(obraRepository.buscarDuplicados(eq("Cien"), eq(null), any())).thenReturn(List.of(obra));
        when(obraMapper.toResumen(obra)).thenReturn(resumen);

        List<ObraResumen> result = obraService.buscarDuplicados("Cien", null);

        assertThat(result).containsExactly(resumen);
    }
}

