package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.ObraRequest;
import com.arrupeteca.dto.response.detail.ObraDetalleResponse;
import com.arrupeteca.dto.response.summary.ObraResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.ObraMapper;
import com.arrupeteca.persistence.entity.Obra;
import com.arrupeteca.persistence.repository.ObraRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObraServiceImplTest {

    @Mock
    private ObraRepository obraRepository;

    @Mock
    private ObraMapper obraMapper;

    @InjectMocks
    private ObraServiceImpl obraService;

    @Test
    @DisplayName("obtenerTodosActivos - Should return page of Resumen")
    void obtenerTodosActivos() {
        Obra entity = Obra.builder().id(1L).titulo("Test").build();
        Page<Obra> page = new PageImpl<>(List.of(entity));
        ObraResumen resumen = new ObraResumen(1L, "Test", 2021, "Resumen", false);

        when(obraRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        when(obraMapper.toResumen(entity)).thenReturn(resumen);

        Page<ObraResumen> result = obraService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should return DetalleResponse")
    void obtenerPorIdActivo() {
        Obra entity = Obra.builder().id(1L).titulo("Test").build();
        ObraDetalleResponse detail = new ObraDetalleResponse(1L, "Test", 2021, "Resumen", false,
                "admin", null, "admin", null, Collections.emptySet(), Collections.emptySet(), Collections.emptySet());

        when(obraRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(obraMapper.toDetalleResponse(entity)).thenReturn(detail);

        ObraDetalleResponse result = obraService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should throw exception when not found")
    void obtenerPorIdActivoNotFound() {
        when(obraRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> obraService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Obra activa con ID: 1");
    }

    @Test
    @DisplayName("busquedaGlobal - Should return page of Resumen")
    void busquedaGlobal() {
        Obra entity = Obra.builder().id(1L).build();
        Page<Obra> page = new PageImpl<>(List.of(entity));
        ObraResumen resumen = mock(ObraResumen.class);
        when(obraRepository.busquedaGlobal("palabra", Pageable.unpaged())).thenReturn(page);
        when(obraMapper.toResumen(entity)).thenReturn(resumen);

        Page<ObraResumen> result = obraService.busquedaGlobal("palabra", Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(resumen);
    }

    @Test
    @DisplayName("obtenerTodos - Should return all works")
    void obtenerTodos() {
        Obra entity = Obra.builder().id(1L).build();
        Page<Obra> page = new PageImpl<>(List.of(entity));
        ObraResumen resumen = mock(ObraResumen.class);
        when(obraRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(obraMapper.toResumen(entity)).thenReturn(resumen);

        Page<ObraResumen> result = obraService.obtenerTodos(Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(resumen);
    }

    @Test
    @DisplayName("obtenerPorId - Should return DetalleResponse")
    void obtenerPorId() {
        Obra entity = Obra.builder().id(1L).build();
        ObraDetalleResponse detail = mock(ObraDetalleResponse.class);
        when(obraRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(obraMapper.toDetalleResponse(entity)).thenReturn(detail);

        ObraDetalleResponse result = obraService.obtenerPorId(1L);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("obtenerPorId - Should throw exception when not found")
    void obtenerPorIdNotFound() {
        when(obraRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> obraService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Obra con ID: 1");
    }

    @Test
    @DisplayName("busquedaMaestraAdmin - Should return results")
    void busquedaMaestraAdmin() {
        Obra entity = Obra.builder().id(1L).build();
        Page<Obra> page = new PageImpl<>(List.of(entity));
        ObraResumen resumen = mock(ObraResumen.class);
        when(obraRepository.busquedaMaestraAdmin(any(), any(), any(), any(), any())).thenReturn(page);
        when(obraMapper.toResumen(entity)).thenReturn(resumen);

        Page<ObraResumen> result = obraService.busquedaMaestraAdmin(false, "term", 2000, 2021, Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(resumen);
    }

    @Test
    @DisplayName("crear - Should save and return DetalleResponse")
    void crear() {
        ObraRequest request = new ObraRequest();
        request.setTitulo("Test");
        Obra entity = Obra.builder().id(1L).titulo("Test").build();
        ObraDetalleResponse detail = mock(ObraDetalleResponse.class);

        when(obraRepository.existsByTituloIgnoreCase("Test")).thenReturn(false);
        when(obraMapper.toEntity(request)).thenReturn(entity);
        when(obraRepository.save(any(Obra.class))).thenReturn(entity);
        when(obraMapper.toDetalleResponse(entity)).thenReturn(detail);

        ObraDetalleResponse result = obraService.crear(request);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("crear - Should throw exception if title exists")
    void crearTituloExists() {
        ObraRequest request = new ObraRequest();
        request.setTitulo("Test");
        when(obraRepository.existsByTituloIgnoreCase("Test")).thenReturn(true);

        assertThatThrownBy(() -> obraService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe una obra con el título: Test");
    }

    @Test
    @DisplayName("actualizar - Should update and return DetalleResponse")
    void actualizar() {
        ObraRequest request = new ObraRequest();
        request.setTitulo("Nuevo");
        Obra existing = Obra.builder().id(1L).titulo("Viejo").build();
        Obra saved = Obra.builder().id(1L).titulo("Nuevo").build();
        ObraDetalleResponse detail = mock(ObraDetalleResponse.class);

        when(obraRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(obraRepository.existsByTituloIgnoreCase("Nuevo")).thenReturn(false);
        when(obraRepository.save(existing)).thenReturn(saved);
        when(obraMapper.toDetalleResponse(saved)).thenReturn(detail);

        ObraDetalleResponse result = obraService.actualizar(1L, request);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("actualizar - Should throw when not found")
    void actualizarNotFound() {
        when(obraRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> obraService.actualizar(1L, new ObraRequest()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Obra con ID: 1");
    }

    @Test
    @DisplayName("actualizar - Should throw when title already exists in another work")
    void actualizarTituloConflict() {
        ObraRequest request = new ObraRequest();
        request.setTitulo("Nuevo");
        Obra existing = Obra.builder().id(1L).titulo("Viejo").build();

        when(obraRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(obraRepository.existsByTituloIgnoreCase("Nuevo")).thenReturn(true);

        assertThatThrownBy(() -> obraService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe otra obra con el título: Nuevo");
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
    @DisplayName("cambiarEstado - Should throw when not found")
    void cambiarEstadoNotFound() {
        when(obraRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> obraService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Obra con ID: 1");
    }
}
