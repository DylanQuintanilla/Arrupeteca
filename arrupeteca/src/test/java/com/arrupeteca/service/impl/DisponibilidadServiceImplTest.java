package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.DisponibilidadRequest;
import com.arrupeteca.dto.response.DisponibilidadResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.DisponibilidadMapper;
import com.arrupeteca.persistence.entity.Disponibilidad;
import com.arrupeteca.persistence.repository.DisponibilidadRepository;
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

class DisponibilidadServiceImplTest extends BaseServiceTest {

    @Mock
    private DisponibilidadRepository disponibilidadRepository;

    @Mock
    private DisponibilidadMapper disponibilidadMapper;

    @InjectMocks
    private DisponibilidadServiceImpl disponibilidadService;

    @Test
    @DisplayName("Should return all active disponibilidades paginated")
    void shouldReturnAllActive() {
        Disponibilidad entity = Disponibilidad.builder().build();
        Page<Disponibilidad> expectedPage = new PageImpl<>(List.of(entity));
        DisponibilidadResponse response = mock(DisponibilidadResponse.class);

        when(disponibilidadRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(expectedPage);
        when(disponibilidadMapper.toResponse(entity)).thenReturn(response);

        Page<DisponibilidadResponse> result = disponibilidadService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    @DisplayName("Should return disponibilidad by ID and active")
    void shouldReturnByIdAndActive() {
        Disponibilidad entity = Disponibilidad.builder().build();
        DisponibilidadResponse response = mock(DisponibilidadResponse.class);
        when(disponibilidadRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(disponibilidadMapper.toResponse(entity)).thenReturn(response);

        assertThat(disponibilidadService.obtenerPorIdActivo(1L)).isEqualTo(response);

        when(disponibilidadRepository.findProjectedByIdAndBorradoLogicoFalse(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> disponibilidadService.obtenerPorIdActivo(2L)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("Should return all disponibilidades for admin")
    void shouldReturnAllAdmin() {
        Disponibilidad entity = Disponibilidad.builder().build();
        Page<Disponibilidad> expectedPage = new PageImpl<>(List.of(entity));
        DisponibilidadResponse response = mock(DisponibilidadResponse.class);

        when(disponibilidadRepository.findAllProjectedBy(any(Pageable.class))).thenReturn(expectedPage);
        when(disponibilidadMapper.toResponse(entity)).thenReturn(response);

        assertThat(disponibilidadService.obtenerTodos(Pageable.unpaged()).getContent()).containsExactly(response);
    }

    @Test
    @DisplayName("Should return disponibilidad by ID")
    void shouldReturnById() {
        Disponibilidad entity = Disponibilidad.builder().build();
        DisponibilidadResponse response = mock(DisponibilidadResponse.class);
        when(disponibilidadRepository.findProjectedById(1L)).thenReturn(Optional.of(entity));
        when(disponibilidadMapper.toResponse(entity)).thenReturn(response);

        assertThat(disponibilidadService.obtenerPorId(1L)).isEqualTo(response);

        when(disponibilidadRepository.findProjectedById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> disponibilidadService.obtenerPorId(2L)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("Should create disponibilidad when name is unique")
    void shouldCreateDisponibilidad() {
        DisponibilidadRequest request = DisponibilidadRequest.builder().nombre("Nuevo").build();
        Disponibilidad entity = Disponibilidad.builder().build();
        Disponibilidad saved = Disponibilidad.builder().build();
        saved.setId(1L);
        DisponibilidadResponse response = mock(DisponibilidadResponse.class);

        when(disponibilidadRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(false);
        when(disponibilidadMapper.toEntity(any())).thenReturn(entity);
        when(disponibilidadRepository.save(any())).thenReturn(saved);
        when(disponibilidadMapper.toResponse(saved)).thenReturn(response);

        assertThat(disponibilidadService.crear(request)).isEqualTo(response);

        when(disponibilidadRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(true);
        assertThatThrownBy(() -> disponibilidadService.crear(request)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("Should update disponibilidad")
    void shouldUpdateDisponibilidad() {
        DisponibilidadRequest request = DisponibilidadRequest.builder().nombre("Actualizado").build();
        Disponibilidad existing = Disponibilidad.builder().build();
        existing.setId(1L);
        existing.setNombre("Antiguo");
        Disponibilidad saved = Disponibilidad.builder().build();
        saved.setId(1L);
        DisponibilidadResponse response = mock(DisponibilidadResponse.class);

        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(disponibilidadRepository.existsByNombreIgnoreCase("Actualizado")).thenReturn(false);
        when(disponibilidadRepository.save(any())).thenReturn(saved);
        when(disponibilidadMapper.toResponse(saved)).thenReturn(response);

        assertThat(disponibilidadService.actualizar(1L, request)).isEqualTo(response);

        when(disponibilidadRepository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> disponibilidadService.actualizar(2L, request)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("Should change status")
    void shouldChangeStatus() {
        when(disponibilidadRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);
        disponibilidadService.cambiarEstado(1L, true);
        verify(disponibilidadRepository).activar(1L);

        disponibilidadService.cambiarEstado(1L, false);
        verify(disponibilidadRepository).desactivar(1L);

        when(disponibilidadRepository.countByIdIncludeDeleted(2L)).thenReturn(0L);
        assertThatThrownBy(() -> disponibilidadService.cambiarEstado(2L, true)).isInstanceOf(ValidacionNegocioException.class);
    }
}
