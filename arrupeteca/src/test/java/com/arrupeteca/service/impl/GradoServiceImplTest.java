package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.GradoRequest;
import com.arrupeteca.dto.response.GradoResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.GradoMapper;
import com.arrupeteca.persistence.entity.Grado;
import com.arrupeteca.persistence.repository.GradoRepository;
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

class GradoServiceImplTest extends BaseServiceTest {

    @Mock
    private GradoRepository gradoRepository;

    @Mock
    private GradoMapper gradoMapper;

    @InjectMocks
    private GradoServiceImpl gradoService;

    @Test
    @DisplayName("Should return all active grados paginated")
    void shouldReturnAllActive() {
        Grado entity = Grado.builder().build();
        Page<Grado> expectedPage = new PageImpl<>(List.of(entity));
        GradoResponse response = mock(GradoResponse.class);

        when(gradoRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(expectedPage);
        when(gradoMapper.toResponse(entity)).thenReturn(response);

        Page<GradoResponse> result = gradoService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    @DisplayName("Should return grado by ID and active")
    void shouldReturnByIdAndActive() {
        Grado entity = Grado.builder().build();
        GradoResponse response = mock(GradoResponse.class);
        when(gradoRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(gradoMapper.toResponse(entity)).thenReturn(response);

        assertThat(gradoService.obtenerPorIdActivo(1L)).isEqualTo(response);

        when(gradoRepository.findProjectedByIdAndBorradoLogicoFalse(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> gradoService.obtenerPorIdActivo(2L)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("Should return all grados for admin")
    void shouldReturnAllAdmin() {
        Grado entity = Grado.builder().build();
        Page<Grado> expectedPage = new PageImpl<>(List.of(entity));
        GradoResponse response = mock(GradoResponse.class);

        when(gradoRepository.findAllProjectedBy(any(Pageable.class))).thenReturn(expectedPage);
        when(gradoMapper.toResponse(entity)).thenReturn(response);

        assertThat(gradoService.obtenerTodos(Pageable.unpaged()).getContent()).containsExactly(response);
    }

    @Test
    @DisplayName("Should return grado by ID")
    void shouldReturnById() {
        Grado entity = Grado.builder().build();
        GradoResponse response = mock(GradoResponse.class);
        when(gradoRepository.findProjectedById(1L)).thenReturn(Optional.of(entity));
        when(gradoMapper.toResponse(entity)).thenReturn(response);

        assertThat(gradoService.obtenerPorId(1L)).isEqualTo(response);

        when(gradoRepository.findProjectedById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> gradoService.obtenerPorId(2L)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("Should create grado when name is unique")
    void shouldCreateGrado() {
        GradoRequest request = GradoRequest.builder().nombre("Nuevo").build();
        Grado entity = Grado.builder().build();
        Grado saved = Grado.builder().build();
        saved.setId(1L);
        GradoResponse response = mock(GradoResponse.class);

        when(gradoRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(false);
        when(gradoMapper.toEntity(any())).thenReturn(entity);
        when(gradoRepository.save(any())).thenReturn(saved);
        when(gradoMapper.toResponse(saved)).thenReturn(response);

        assertThat(gradoService.crear(request)).isEqualTo(response);

        when(gradoRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(true);
        assertThatThrownBy(() -> gradoService.crear(request)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("Should update grado")
    void shouldUpdateGrado() {
        GradoRequest request = GradoRequest.builder().nombre("Actualizado").build();
        Grado existing = Grado.builder().build();
        existing.setId(1L);
        existing.setNombre("Antiguo");
        Grado saved = Grado.builder().build();
        saved.setId(1L);
        GradoResponse response = mock(GradoResponse.class);

        when(gradoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(gradoRepository.existsByNombreIgnoreCase("Actualizado")).thenReturn(false);
        when(gradoRepository.save(any())).thenReturn(saved);
        when(gradoMapper.toResponse(saved)).thenReturn(response);

        assertThat(gradoService.actualizar(1L, request)).isEqualTo(response);

        when(gradoRepository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> gradoService.actualizar(2L, request)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("Should change status")
    void shouldChangeStatus() {
        when(gradoRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);
        gradoService.cambiarEstado(1L, true);
        verify(gradoRepository).activar(1L);

        gradoService.cambiarEstado(1L, false);
        verify(gradoRepository).desactivar(1L);

        when(gradoRepository.countByIdIncludeDeleted(2L)).thenReturn(0L);
        assertThatThrownBy(() -> gradoService.cambiarEstado(2L, true)).isInstanceOf(ValidacionNegocioException.class);
    }
}
