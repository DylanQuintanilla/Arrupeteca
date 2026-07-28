package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.CicloRequest;
import com.arrupeteca.dto.response.CicloResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.CicloMapper;
import com.arrupeteca.persistence.entity.Ciclo;
import com.arrupeteca.persistence.repository.CicloRepository;
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

class CicloServiceImplTest extends BaseServiceTest {

    @Mock
    private CicloRepository cicloRepository;

    @Mock
    private CicloMapper cicloMapper;

    @InjectMocks
    private CicloServiceImpl cicloService;

    @Test
    @DisplayName("Should return all active ciclos paginated")
    void shouldReturnAllActive() {
        Ciclo entity = Ciclo.builder().build();
        Page<Ciclo> expectedPage = new PageImpl<>(List.of(entity));
        CicloResponse response = mock(CicloResponse.class);

        when(cicloRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(expectedPage);
        when(cicloMapper.toResponse(entity)).thenReturn(response);

        Page<CicloResponse> result = cicloService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    @DisplayName("Should return ciclo by ID and active")
    void shouldReturnByIdAndActive() {
        Ciclo entity = Ciclo.builder().build();
        CicloResponse response = mock(CicloResponse.class);
        when(cicloRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(cicloMapper.toResponse(entity)).thenReturn(response);

        assertThat(cicloService.obtenerPorIdActivo(1L)).isEqualTo(response);

        when(cicloRepository.findProjectedByIdAndBorradoLogicoFalse(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cicloService.obtenerPorIdActivo(2L)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("Should return all ciclos for admin")
    void shouldReturnAllAdmin() {
        Ciclo entity = Ciclo.builder().build();
        Page<Ciclo> expectedPage = new PageImpl<>(List.of(entity));
        CicloResponse response = mock(CicloResponse.class);

        when(cicloRepository.findAllProjectedBy(any(Pageable.class))).thenReturn(expectedPage);
        when(cicloMapper.toResponse(entity)).thenReturn(response);

        assertThat(cicloService.obtenerTodos(Pageable.unpaged()).getContent()).containsExactly(response);
    }

    @Test
    @DisplayName("Should return ciclo by ID")
    void shouldReturnById() {
        Ciclo entity = Ciclo.builder().build();
        CicloResponse response = mock(CicloResponse.class);
        when(cicloRepository.findProjectedById(1L)).thenReturn(Optional.of(entity));
        when(cicloMapper.toResponse(entity)).thenReturn(response);

        assertThat(cicloService.obtenerPorId(1L)).isEqualTo(response);

        when(cicloRepository.findProjectedById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cicloService.obtenerPorId(2L)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("Should create ciclo when name is unique")
    void shouldCreateCiclo() {
        CicloRequest request = CicloRequest.builder().nombre("Nuevo").build();
        Ciclo entity = Ciclo.builder().build();
        Ciclo saved = Ciclo.builder().build();
        saved.setId(1L);
        CicloResponse response = mock(CicloResponse.class);

        when(cicloRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(false);
        when(cicloMapper.toEntity(any())).thenReturn(entity);
        when(cicloRepository.save(any())).thenReturn(saved);
        when(cicloMapper.toResponse(saved)).thenReturn(response);

        assertThat(cicloService.crear(request)).isEqualTo(response);

        when(cicloRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(true);
        assertThatThrownBy(() -> cicloService.crear(request)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("Should update ciclo")
    void shouldUpdateCiclo() {
        CicloRequest request = CicloRequest.builder().nombre("Actualizado").build();
        Ciclo existing = Ciclo.builder().build();
        existing.setId(1L);
        existing.setNombre("Antiguo");
        Ciclo saved = Ciclo.builder().build();
        saved.setId(1L);
        CicloResponse response = mock(CicloResponse.class);

        when(cicloRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(cicloRepository.existsByNombreIgnoreCase("Actualizado")).thenReturn(false);
        when(cicloRepository.save(any())).thenReturn(saved);
        when(cicloMapper.toResponse(saved)).thenReturn(response);

        assertThat(cicloService.actualizar(1L, request)).isEqualTo(response);

        when(cicloRepository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cicloService.actualizar(2L, request)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("Should change status")
    void shouldChangeStatus() {
        when(cicloRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);
        cicloService.cambiarEstado(1L, true);
        verify(cicloRepository).activar(1L);

        cicloService.cambiarEstado(1L, false);
        verify(cicloRepository).desactivar(1L);

        when(cicloRepository.countByIdIncludeDeleted(2L)).thenReturn(0L);
        assertThatThrownBy(() -> cicloService.cambiarEstado(2L, true)).isInstanceOf(ValidacionNegocioException.class);
    }
}
