package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.DisponibilidadRequest;
import com.arrupeteca.dto.response.DisponibilidadResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.DisponibilidadMapper;
import com.arrupeteca.persistence.entity.Disponibilidad;
import com.arrupeteca.persistence.repository.DisponibilidadRepository;
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
class DisponibilidadServiceImplTest {

    @Mock
    private DisponibilidadRepository disponibilidadRepository;

    @Mock
    private DisponibilidadMapper disponibilidadMapper;

    @InjectMocks
    private DisponibilidadServiceImpl disponibilidadService;

    @Test
    @DisplayName("Should return all active disponibilidades list")
    void shouldReturnAllActive() {
        Disponibilidad entity = Disponibilidad.builder().build();
        DisponibilidadResponse response = mock(DisponibilidadResponse.class);

        when(disponibilidadRepository.findByBorradoLogicoFalseOrderByNombreAsc()).thenReturn(List.of(entity));
        when(disponibilidadMapper.toResponse(entity)).thenReturn(response);

        List<DisponibilidadResponse> result = disponibilidadService.obtenerTodosActivos();

        assertThat(result).containsExactly(response);
        verify(disponibilidadRepository).findByBorradoLogicoFalseOrderByNombreAsc();
    }

    @Test
    @DisplayName("Should return all disponibilidades for admin")
    void shouldReturnAllAdmin() {
        Disponibilidad entity = Disponibilidad.builder().build();
        Page<Disponibilidad> expectedPage = new PageImpl<>(List.of(entity));
        DisponibilidadResponse response = mock(DisponibilidadResponse.class);

        when(disponibilidadRepository.busquedaAdmin(isNull(), isNull(), any(Pageable.class))).thenReturn(expectedPage);
        when(disponibilidadMapper.toResponse(entity)).thenReturn(response);

        Page<DisponibilidadResponse> result = disponibilidadService.obtenerTodos(null, null, Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    @DisplayName("Should return disponibilidad by ID")
    void shouldReturnById() {
        Disponibilidad entity = Disponibilidad.builder().build();
        DisponibilidadResponse response = mock(DisponibilidadResponse.class);
        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(disponibilidadMapper.toResponse(entity)).thenReturn(response);

        DisponibilidadResponse result = disponibilidadService.obtenerPorId(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("Should throw exception when disponibilidad not found")
    void shouldThrowWhenNotFound() {
        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> disponibilidadService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró una disponibilidad con ID: 1");
    }

    @Test
    @DisplayName("Should create disponibilidad successfully")
    void shouldCreateDisponibilidad() {
        DisponibilidadRequest request = DisponibilidadRequest.builder().nombre("Disponible").build();
        Disponibilidad entity = Disponibilidad.builder().build();
        Disponibilidad savedEntity = Disponibilidad.builder().build();
        DisponibilidadResponse response = mock(DisponibilidadResponse.class);

        when(disponibilidadRepository.existsByNombreIgnoreCase("Disponible")).thenReturn(false);
        when(disponibilidadMapper.toEntity(request)).thenReturn(entity);
        when(disponibilidadRepository.save(entity)).thenReturn(savedEntity);
        when(disponibilidadMapper.toResponse(savedEntity)).thenReturn(response);

        assertThat(disponibilidadService.crear(request)).isEqualTo(response);
    }

    @Test
    @DisplayName("Should throw exception when creating duplicate disponibilidad")
    void shouldThrowWhenDuplicateNameOnCreate() {
        DisponibilidadRequest request = DisponibilidadRequest.builder().nombre("Disponible").build();
        when(disponibilidadRepository.existsByNombreIgnoreCase("Disponible")).thenReturn(true);

        assertThatThrownBy(() -> disponibilidadService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe una disponibilidad con el nombre: Disponible");
    }

    @Test
    @DisplayName("Should change estado of disponibilidad successfully")
    void shouldCambiarEstado() {
        when(disponibilidadRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);

        disponibilidadService.cambiarEstado(1L, false);
        verify(disponibilidadRepository).desactivar(1L);

        disponibilidadService.cambiarEstado(1L, true);
        verify(disponibilidadRepository).activar(1L);
    }
}
