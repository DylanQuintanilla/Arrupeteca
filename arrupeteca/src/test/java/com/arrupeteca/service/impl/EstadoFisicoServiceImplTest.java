package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EstadoFisicoRequest;
import com.arrupeteca.dto.response.EstadoFisicoResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EstadoFisicoMapper;
import com.arrupeteca.persistence.entity.EstadoFisico;
import com.arrupeteca.persistence.repository.EstadoFisicoRepository;
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

class EstadoFisicoServiceImplTest extends BaseServiceTest {

    @Mock
    private EstadoFisicoRepository estadoFisicoRepository;

    @Mock
    private EstadoFisicoMapper estadoFisicoMapper;

    @InjectMocks
    private EstadoFisicoServiceImpl estadoFisicoService;

    @Test
    @DisplayName("Should return all active estados fisicos paginated")
    void shouldReturnAllActive() {
        EstadoFisico entity = EstadoFisico.builder().build();
        Page<EstadoFisico> expectedPage = new PageImpl<>(List.of(entity));
        EstadoFisicoResponse response = mock(EstadoFisicoResponse.class);

        when(estadoFisicoRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(expectedPage);
        when(estadoFisicoMapper.toResponse(entity)).thenReturn(response);

        Page<EstadoFisicoResponse> result = estadoFisicoService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    @DisplayName("Should return estado fisico by ID and active")
    void shouldReturnByIdAndActive() {
        EstadoFisico entity = EstadoFisico.builder().build();
        EstadoFisicoResponse response = mock(EstadoFisicoResponse.class);
        when(estadoFisicoRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(estadoFisicoMapper.toResponse(entity)).thenReturn(response);

        assertThat(estadoFisicoService.obtenerPorIdActivo(1L)).isEqualTo(response);

        when(estadoFisicoRepository.findProjectedByIdAndBorradoLogicoFalse(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> estadoFisicoService.obtenerPorIdActivo(2L)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("Should return all estados fisicos for admin")
    void shouldReturnAllAdmin() {
        EstadoFisico entity = EstadoFisico.builder().build();
        Page<EstadoFisico> expectedPage = new PageImpl<>(List.of(entity));
        EstadoFisicoResponse response = mock(EstadoFisicoResponse.class);

        when(estadoFisicoRepository.findAllProjectedBy(any(Pageable.class))).thenReturn(expectedPage);
        when(estadoFisicoMapper.toResponse(entity)).thenReturn(response);

        assertThat(estadoFisicoService.obtenerTodos(Pageable.unpaged()).getContent()).containsExactly(response);
    }

    @Test
    @DisplayName("Should return estado fisico by ID")
    void shouldReturnById() {
        EstadoFisico entity = EstadoFisico.builder().build();
        EstadoFisicoResponse response = mock(EstadoFisicoResponse.class);
        when(estadoFisicoRepository.findProjectedById(1L)).thenReturn(Optional.of(entity));
        when(estadoFisicoMapper.toResponse(entity)).thenReturn(response);

        assertThat(estadoFisicoService.obtenerPorId(1L)).isEqualTo(response);

        when(estadoFisicoRepository.findProjectedById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> estadoFisicoService.obtenerPorId(2L)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("Should create estado fisico when name is unique")
    void shouldCreateEstadoFisico() {
        EstadoFisicoRequest request = EstadoFisicoRequest.builder().nombre("Nuevo").build();
        EstadoFisico entity = EstadoFisico.builder().build();
        EstadoFisico saved = EstadoFisico.builder().build();
        saved.setId(1L);
        EstadoFisicoResponse response = mock(EstadoFisicoResponse.class);

        when(estadoFisicoRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(false);
        when(estadoFisicoMapper.toEntity(any())).thenReturn(entity);
        when(estadoFisicoRepository.save(any())).thenReturn(saved);
        when(estadoFisicoMapper.toResponse(saved)).thenReturn(response);

        assertThat(estadoFisicoService.crear(request)).isEqualTo(response);

        when(estadoFisicoRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(true);
        assertThatThrownBy(() -> estadoFisicoService.crear(request)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("Should update estado fisico")
    void shouldUpdateEstadoFisico() {
        EstadoFisicoRequest request = EstadoFisicoRequest.builder().nombre("Actualizado").build();
        EstadoFisico existing = EstadoFisico.builder().build();
        existing.setId(1L);
        existing.setNombre("Antiguo");
        EstadoFisico saved = EstadoFisico.builder().build();
        saved.setId(1L);
        EstadoFisicoResponse response = mock(EstadoFisicoResponse.class);

        when(estadoFisicoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(estadoFisicoRepository.existsByNombreIgnoreCase("Actualizado")).thenReturn(false);
        when(estadoFisicoRepository.save(any())).thenReturn(saved);
        when(estadoFisicoMapper.toResponse(saved)).thenReturn(response);

        assertThat(estadoFisicoService.actualizar(1L, request)).isEqualTo(response);

        when(estadoFisicoRepository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> estadoFisicoService.actualizar(2L, request)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("Should change status")
    void shouldChangeStatus() {
        when(estadoFisicoRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);
        estadoFisicoService.cambiarEstado(1L, true);
        verify(estadoFisicoRepository).activar(1L);

        estadoFisicoService.cambiarEstado(1L, false);
        verify(estadoFisicoRepository).desactivar(1L);

        when(estadoFisicoRepository.countByIdIncludeDeleted(2L)).thenReturn(0L);
        assertThatThrownBy(() -> estadoFisicoService.cambiarEstado(2L, true)).isInstanceOf(ValidacionNegocioException.class);
    }
}
