package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EstadoFisicoRequest;
import com.arrupeteca.dto.response.EstadoFisicoResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EstadoFisicoMapper;
import com.arrupeteca.persistence.entity.EstadoFisico;
import com.arrupeteca.persistence.repository.EstadoFisicoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstadoFisicoServiceImplTest {

    @Mock
    private EstadoFisicoRepository estadoFisicoRepository;

    @Mock
    private EstadoFisicoMapper estadoFisicoMapper;

    @InjectMocks
    private EstadoFisicoServiceImpl estadoFisicoService;

    @Test
    @DisplayName("Should return all active estados fisicos list")
    void shouldReturnAllActive() {
        EstadoFisico entity = EstadoFisico.builder().build();
        EstadoFisicoResponse response = mock(EstadoFisicoResponse.class);

        when(estadoFisicoRepository.findByBorradoLogicoFalseOrderByNombreAsc()).thenReturn(List.of(entity));
        when(estadoFisicoMapper.toResponse(entity)).thenReturn(response);

        List<EstadoFisicoResponse> result = estadoFisicoService.obtenerTodosActivos();

        assertThat(result).containsExactly(response);
        verify(estadoFisicoRepository).findByBorradoLogicoFalseOrderByNombreAsc();
    }

    @Test
    @DisplayName("Should return all estados fisicos for admin")
    void shouldReturnAllAdmin() {
        EstadoFisico entity = EstadoFisico.builder().build();
        Page<EstadoFisico> expectedPage = new PageImpl<>(List.of(entity));
        EstadoFisicoResponse response = mock(EstadoFisicoResponse.class);

        when(estadoFisicoRepository.busquedaAdmin(isNull(), isNull(), any(Pageable.class))).thenReturn(expectedPage);
        when(estadoFisicoMapper.toResponse(entity)).thenReturn(response);

        Page<EstadoFisicoResponse> result = estadoFisicoService.obtenerTodos(null, null, Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    @DisplayName("Should return estado fisico by ID")
    void shouldReturnById() {
        EstadoFisico entity = EstadoFisico.builder().build();
        EstadoFisicoResponse response = mock(EstadoFisicoResponse.class);
        when(estadoFisicoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(estadoFisicoMapper.toResponse(entity)).thenReturn(response);

        EstadoFisicoResponse result = estadoFisicoService.obtenerPorId(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("Should throw exception when estado fisico not found")
    void shouldThrowWhenNotFound() {
        when(estadoFisicoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estadoFisicoService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró un estado físico con ID: 1");
    }

    @Test
    @DisplayName("Should create estado fisico successfully")
    void shouldCreateEstadoFisico() {
        EstadoFisicoRequest request = EstadoFisicoRequest.builder().nombre("Nuevo").build();
        EstadoFisico entity = EstadoFisico.builder().build();
        EstadoFisico savedEntity = EstadoFisico.builder().build();
        EstadoFisicoResponse response = mock(EstadoFisicoResponse.class);

        when(estadoFisicoRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(false);
        when(estadoFisicoMapper.toEntity(request)).thenReturn(entity);
        when(estadoFisicoRepository.save(entity)).thenReturn(savedEntity);
        when(estadoFisicoMapper.toResponse(savedEntity)).thenReturn(response);

        assertThat(estadoFisicoService.crear(request)).isEqualTo(response);
    }

    @Test
    @DisplayName("Should throw exception when creating duplicate estado fisico")
    void shouldThrowWhenDuplicateNameOnCreate() {
        EstadoFisicoRequest request = EstadoFisicoRequest.builder().nombre("Nuevo").build();
        when(estadoFisicoRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(true);

        assertThatThrownBy(() -> estadoFisicoService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un estado físico con el nombre: Nuevo");
    }

    @Test
    @DisplayName("Should change estado of estado fisico successfully")
    void shouldCambiarEstado() {
        when(estadoFisicoRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);

        estadoFisicoService.cambiarEstado(1L, false);
        verify(estadoFisicoRepository).desactivar(1L);

        estadoFisicoService.cambiarEstado(1L, true);
        verify(estadoFisicoRepository).activar(1L);
    }
}
