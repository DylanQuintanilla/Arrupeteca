package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.GradoRequest;
import com.arrupeteca.dto.response.GradoResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.GradoMapper;
import com.arrupeteca.persistence.entity.Grado;
import com.arrupeteca.persistence.repository.GradoRepository;
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
class GradoServiceImplTest {

    @Mock
    private GradoRepository gradoRepository;

    @Mock
    private GradoMapper gradoMapper;

    @InjectMocks
    private GradoServiceImpl gradoService;

    @Test
    @DisplayName("Should return all active grados list")
    void shouldReturnAllActive() {
        Grado entity = Grado.builder().build();
        GradoResponse response = mock(GradoResponse.class);

        when(gradoRepository.buscarActivosPorNombre(any())).thenReturn(List.of(entity));
        when(gradoMapper.toResponse(entity)).thenReturn(response);

        List<GradoResponse> result = gradoService.obtenerTodosActivos(null);

        assertThat(result).containsExactly(response);
        verify(gradoRepository).buscarActivosPorNombre(null);
    }

    @Test
    @DisplayName("Should return all grados for admin")
    void shouldReturnAllAdmin() {
        Grado entity = Grado.builder().build();
        Page<Grado> expectedPage = new PageImpl<>(List.of(entity));
        GradoResponse response = mock(GradoResponse.class);

        when(gradoRepository.busquedaAdmin(isNull(), isNull(), any(Pageable.class))).thenReturn(expectedPage);
        when(gradoMapper.toResponse(entity)).thenReturn(response);

        Page<GradoResponse> result = gradoService.obtenerTodos(null, null, Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    @DisplayName("Should return grado by ID")
    void shouldReturnById() {
        Grado entity = Grado.builder().build();
        GradoResponse response = mock(GradoResponse.class);
        when(gradoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(gradoMapper.toResponse(entity)).thenReturn(response);

        GradoResponse result = gradoService.obtenerPorId(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("Should throw exception when grado not found")
    void shouldThrowWhenNotFound() {
        when(gradoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gradoService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con ID: 1");
    }

    @Test
    @DisplayName("Should create grado successfully")
    void shouldCreateGrado() {
        GradoRequest request = GradoRequest.builder().nombre("Bachillerato General").build();
        Grado entity = Grado.builder().build();
        Grado savedEntity = Grado.builder().build();
        GradoResponse response = mock(GradoResponse.class);

        when(gradoRepository.existsByNombreIgnoreCase("Bachillerato General")).thenReturn(false);
        when(gradoMapper.toEntity(request)).thenReturn(entity);
        when(gradoRepository.save(entity)).thenReturn(savedEntity);
        when(gradoMapper.toResponse(savedEntity)).thenReturn(response);

        assertThat(gradoService.crear(request)).isEqualTo(response);
    }

    @Test
    @DisplayName("Should throw exception when creating duplicate grado")
    void shouldThrowWhenDuplicateNameOnCreate() {
        GradoRequest request = GradoRequest.builder().nombre("Bachillerato General").build();
        when(gradoRepository.existsByNombreIgnoreCase("Bachillerato General")).thenReturn(true);

        assertThatThrownBy(() -> gradoService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con el nombre: Bachillerato General");
    }

    @Test
    @DisplayName("Should change estado of grado successfully")
    void shouldCambiarEstado() {
        when(gradoRepository.existsById(1L)).thenReturn(true);

        gradoService.cambiarEstado(1L, false);
        verify(gradoRepository).desactivar(1L);

        gradoService.cambiarEstado(1L, true);
        verify(gradoRepository).activar(1L);
    }
}
