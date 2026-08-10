package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.CicloRequest;
import com.arrupeteca.dto.response.CicloResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.CicloMapper;
import com.arrupeteca.persistence.entity.Ciclo;
import com.arrupeteca.persistence.repository.CicloRepository;
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
class CicloServiceImplTest {

    @Mock
    private CicloRepository cicloRepository;

    @Mock
    private CicloMapper cicloMapper;

    @InjectMocks
    private CicloServiceImpl cicloService;

    @Test
    @DisplayName("Should return all active ciclos list")
    void shouldReturnAllActive() {
        Ciclo entity = Ciclo.builder().build();
        CicloResponse response = mock(CicloResponse.class);

        when(cicloRepository.buscarActivosPorNombre(any())).thenReturn(List.of(entity));
        when(cicloMapper.toResponse(entity)).thenReturn(response);

        List<CicloResponse> result = cicloService.obtenerTodosActivos(null);

        assertThat(result).containsExactly(response);
        verify(cicloRepository).buscarActivosPorNombre(null);
    }

    @Test
    @DisplayName("Should return all ciclos for admin")
    void shouldReturnAllAdmin() {
        Ciclo entity = Ciclo.builder().build();
        Page<Ciclo> expectedPage = new PageImpl<>(List.of(entity));
        CicloResponse response = mock(CicloResponse.class);

        when(cicloRepository.busquedaAdmin(isNull(), isNull(), any(Pageable.class))).thenReturn(expectedPage);
        when(cicloMapper.toResponse(entity)).thenReturn(response);

        Page<CicloResponse> result = cicloService.obtenerTodos(null, null, Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    @DisplayName("Should return ciclo by ID")
    void shouldReturnById() {
        Ciclo entity = Ciclo.builder().build();
        CicloResponse response = mock(CicloResponse.class);
        when(cicloRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(cicloMapper.toResponse(entity)).thenReturn(response);

        CicloResponse result = cicloService.obtenerPorId(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("Should throw exception when ciclo not found")
    void shouldThrowWhenNotFound() {
        when(cicloRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cicloService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con ID: 1");
    }

    @Test
    @DisplayName("Should create ciclo successfully")
    void shouldCreateCiclo() {
        CicloRequest request = CicloRequest.builder().nombre("Primer ciclo").build();
        Ciclo entity = Ciclo.builder().build();
        Ciclo savedEntity = Ciclo.builder().build();
        CicloResponse response = mock(CicloResponse.class);

        when(cicloRepository.existsByNombreIgnoreCase("Primer ciclo")).thenReturn(false);
        when(cicloMapper.toEntity(request)).thenReturn(entity);
        when(cicloRepository.save(entity)).thenReturn(savedEntity);
        when(cicloMapper.toResponse(savedEntity)).thenReturn(response);

        assertThat(cicloService.crear(request)).isEqualTo(response);
    }

    @Test
    @DisplayName("Should throw exception when creating duplicate ciclo")
    void shouldThrowWhenDuplicateNameOnCreate() {
        CicloRequest request = CicloRequest.builder().nombre("Primer ciclo").build();
        when(cicloRepository.existsByNombreIgnoreCase("Primer ciclo")).thenReturn(true);

        assertThatThrownBy(() -> cicloService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con el nombre: Primer ciclo");
    }

    @Test
    @DisplayName("Should change estado of ciclo successfully")
    void shouldCambiarEstado() {
        when(cicloRepository.existsById(1L)).thenReturn(true);

        cicloService.cambiarEstado(1L, false);
        verify(cicloRepository).desactivar(1L);

        cicloService.cambiarEstado(1L, true);
        verify(cicloRepository).activar(1L);
    }
}
