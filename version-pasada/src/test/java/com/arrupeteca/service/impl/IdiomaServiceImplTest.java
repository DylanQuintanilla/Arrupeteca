package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.IdiomaRequest;
import com.arrupeteca.dto.response.detail.IdiomaDetalleResponse;
import com.arrupeteca.dto.response.summary.IdiomaResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.IdiomaMapper;
import com.arrupeteca.persistence.entity.Idioma;
import com.arrupeteca.persistence.repository.IdiomaRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdiomaServiceImplTest {

    @Mock
    private IdiomaRepository idiomaRepository;

    @Mock
    private IdiomaMapper idiomaMapper;

    @InjectMocks
    private IdiomaServiceImpl idiomaService;

    @Test
    @DisplayName("obtenerTodosActivos - Should return page of Resumen")
    void obtenerTodosActivos() {
        Idioma entity = Idioma.builder().id(1L).nombre("Español").build();
        Page<Idioma> page = new PageImpl<>(List.of(entity));
        IdiomaResumen resumen = new IdiomaResumen(1L, "Español", false);

        when(idiomaRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        when(idiomaMapper.toResumen(entity)).thenReturn(resumen);

        Page<IdiomaResumen> result = idiomaService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(resumen);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should return DetalleResponse")
    void obtenerPorIdActivo() {
        Idioma entity = Idioma.builder().id(1L).nombre("Español").build();
        IdiomaDetalleResponse detail = new IdiomaDetalleResponse(
                1L, "Español", false, "admin", null, "admin", null, Collections.emptySet());

        when(idiomaRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(idiomaMapper.toDetalleResponse(entity)).thenReturn(detail);

        IdiomaDetalleResponse result = idiomaService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should throw exception when not found")
    void obtenerPorIdActivoNotFound() {
        when(idiomaRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> idiomaService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un Idioma activo con ID: 1");
    }

    @Test
    @DisplayName("obtenerTodos - Should return page of Resumen")
    void obtenerTodos() {
        Idioma entity = Idioma.builder().id(1L).nombre("Español").build();
        Page<Idioma> page = new PageImpl<>(List.of(entity));
        IdiomaResumen resumen = new IdiomaResumen(1L, "Español", false);
        
        when(idiomaRepository.findAllProjectedBy(any(Pageable.class))).thenReturn(page);
        when(idiomaMapper.toResumen(entity)).thenReturn(resumen);

        Page<IdiomaResumen> result = idiomaService.obtenerTodos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorId - Should return DetalleResponse")
    void obtenerPorId() {
        Idioma entity = Idioma.builder().id(1L).nombre("Español").build();
        IdiomaDetalleResponse detail = mock(IdiomaDetalleResponse.class);
        when(idiomaRepository.findProjectedById(1L)).thenReturn(Optional.of(entity));
        when(idiomaMapper.toDetalleResponse(entity)).thenReturn(detail);

        IdiomaDetalleResponse result = idiomaService.obtenerPorId(1L);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("obtenerPorId - Should throw exception when not found")
    void obtenerPorIdNotFound() {
        when(idiomaRepository.findProjectedById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> idiomaService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un Idioma con ID: 1");
    }

    @Test
    @DisplayName("crear - Should create and return DetalleResponse")
    void crearSuccess() {
        IdiomaRequest request = new IdiomaRequest();
        request.setNombre("Nuevo");
        Idioma entity = new Idioma();
        Idioma saved = Idioma.builder().id(1L).nombre("Nuevo").build();
        IdiomaDetalleResponse detail = mock(IdiomaDetalleResponse.class);

        when(idiomaRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(false);
        when(idiomaMapper.toEntity(request)).thenReturn(entity);
        when(idiomaRepository.save(entity)).thenReturn(saved);
        when(idiomaMapper.toDetalleResponse(saved)).thenReturn(detail);

        IdiomaDetalleResponse result = idiomaService.crear(request);

        assertThat(result).isEqualTo(detail);
        verify(idiomaRepository).save(entity);
    }

    @Test
    @DisplayName("crear - Should throw when name duplicate")
    void crearDuplicate() {
        IdiomaRequest request = new IdiomaRequest();
        request.setNombre("Existe");
        when(idiomaRepository.existsByNombreIgnoreCase("Existe")).thenReturn(true);

        assertThatThrownBy(() -> idiomaService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un idioma con el nombre: Existe");
    }

    @Test
    @DisplayName("actualizar - Should update and return DetalleResponse")
    void actualizar() {
        IdiomaRequest request = new IdiomaRequest();
        request.setNombre("Actualizado");
        Idioma existing = Idioma.builder().id(1L).nombre("Español").build();
        IdiomaDetalleResponse detail = mock(IdiomaDetalleResponse.class);

        when(idiomaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(idiomaRepository.existsByNombreIgnoreCase("Actualizado")).thenReturn(false);
        when(idiomaRepository.save(existing)).thenReturn(existing);
        when(idiomaMapper.toDetalleResponse(existing)).thenReturn(detail);

        IdiomaDetalleResponse result = idiomaService.actualizar(1L, request);

        assertThat(result).isEqualTo(detail);
        verify(idiomaMapper).updateEntity(request, existing);
    }

    @Test
    @DisplayName("actualizar - Should update with same name")
    void actualizarSameName() {
        IdiomaRequest request = new IdiomaRequest();
        request.setNombre("Español");
        Idioma existing = Idioma.builder().id(1L).nombre("Español").build();
        IdiomaDetalleResponse detail = mock(IdiomaDetalleResponse.class);

        when(idiomaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(idiomaRepository.save(existing)).thenReturn(existing);
        when(idiomaMapper.toDetalleResponse(existing)).thenReturn(detail);

        idiomaService.actualizar(1L, request);

        verify(idiomaRepository, never()).existsByNombreIgnoreCase(any());
    }

    @Test
    @DisplayName("actualizar - Should throw when new name duplicate")
    void actualizarDuplicate() {
        IdiomaRequest request = new IdiomaRequest();
        request.setNombre("Ocupado");
        Idioma existing = Idioma.builder().id(1L).nombre("Español").build();

        when(idiomaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(idiomaRepository.existsByNombreIgnoreCase("Ocupado")).thenReturn(true);

        assertThatThrownBy(() -> idiomaService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe otro idioma con el nombre: Ocupado");
    }

    @Test
    @DisplayName("actualizar - Should throw when not found")
    void actualizarNotFound() {
        when(idiomaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> idiomaService.actualizar(1L, new IdiomaRequest()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un Idioma con ID: 1");
    }

    @Test
    @DisplayName("cambiarEstado - Should activate")
    void activate() {
        when(idiomaRepository.existsById(1L)).thenReturn(true);
        idiomaService.cambiarEstado(1L, true);
        verify(idiomaRepository).activar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should deactivate")
    void deactivate() {
        when(idiomaRepository.existsById(1L)).thenReturn(true);
        idiomaService.cambiarEstado(1L, false);
        verify(idiomaRepository).desactivar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should throw when not found")
    void statusNotFound() {
        when(idiomaRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> idiomaService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un Idioma con ID: 1");
    }
}
