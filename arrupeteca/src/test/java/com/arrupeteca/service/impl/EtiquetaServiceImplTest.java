package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EtiquetaRequest;
import com.arrupeteca.dto.response.EtiquetaResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EtiquetaMapper;
import com.arrupeteca.persistence.entity.Etiqueta;
import com.arrupeteca.persistence.repository.EtiquetaRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EtiquetaServiceImplTest {

    @Mock
    private EtiquetaRepository etiquetaRepository;

    @Mock
    private EtiquetaMapper etiquetaMapper;

    @InjectMocks
    private EtiquetaServiceImpl etiquetaService;

    @Test
    @DisplayName("Should return all active etiquetas list")
    void shouldReturnAllActive() {
        Etiqueta entity = Etiqueta.builder().build();
        EtiquetaResponse response = mock(EtiquetaResponse.class);

        when(etiquetaRepository.findByBorradoLogicoFalseOrderByNombreAsc()).thenReturn(List.of(entity));
        when(etiquetaMapper.toResponse(entity)).thenReturn(response);

        List<EtiquetaResponse> result = etiquetaService.obtenerTodosActivos();

        assertThat(result).containsExactly(response);
        verify(etiquetaRepository).findByBorradoLogicoFalseOrderByNombreAsc();
    }

    @Test
    @DisplayName("Should return all etiquetas for admin")
    void shouldReturnAllAdmin() {
        Etiqueta entity = Etiqueta.builder().build();
        Page<Etiqueta> expectedPage = new PageImpl<>(List.of(entity));
        EtiquetaResponse response = mock(EtiquetaResponse.class);

        when(etiquetaRepository.busquedaAdmin(eq(null), eq(null), any(Pageable.class))).thenReturn(expectedPage);
        when(etiquetaMapper.toResponse(entity)).thenReturn(response);

        Page<EtiquetaResponse> result = etiquetaService.obtenerTodos(null, null, Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    @DisplayName("Should return etiqueta by ID")
    void shouldReturnById() {
        Etiqueta entity = Etiqueta.builder().build();
        EtiquetaResponse response = mock(EtiquetaResponse.class);
        when(etiquetaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(etiquetaMapper.toResponse(entity)).thenReturn(response);

        assertThat(etiquetaService.obtenerPorId(1L)).isEqualTo(response);

        when(etiquetaRepository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> etiquetaService.obtenerPorId(2L)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("Should create etiqueta successfully")
    void shouldCreateEtiqueta() {
        EtiquetaRequest request = EtiquetaRequest.builder().nombre("Tecnología").build();
        Etiqueta entity = Etiqueta.builder().build();
        Etiqueta savedEntity = Etiqueta.builder().build();
        EtiquetaResponse response = mock(EtiquetaResponse.class);

        when(etiquetaRepository.existsByNombreIgnoreCase("Tecnología")).thenReturn(false);
        when(etiquetaMapper.toEntity(request)).thenReturn(entity);
        when(etiquetaRepository.save(entity)).thenReturn(savedEntity);
        when(etiquetaMapper.toResponse(savedEntity)).thenReturn(response);

        assertThat(etiquetaService.crear(request)).isEqualTo(response);
    }

    @Test
    @DisplayName("Should throw exception when creating duplicate etiqueta")
    void shouldThrowWhenDuplicateNameOnCreate() {
        EtiquetaRequest request = EtiquetaRequest.builder().nombre("Tecnología").build();
        when(etiquetaRepository.existsByNombreIgnoreCase("Tecnología")).thenReturn(true);

        assertThatThrownBy(() -> etiquetaService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe una etiqueta con el nombre: Tecnología");
    }

    @Test
    @DisplayName("Should change estado of etiqueta successfully")
    void shouldCambiarEstado() {
        when(etiquetaRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);

        etiquetaService.cambiarEstado(1L, false);
        verify(etiquetaRepository).desactivar(1L);

        etiquetaService.cambiarEstado(1L, true);
        verify(etiquetaRepository).activar(1L);
    }
}
