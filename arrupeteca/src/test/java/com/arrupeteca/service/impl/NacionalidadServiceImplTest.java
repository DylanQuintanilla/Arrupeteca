package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.NacionalidadRequest;
import com.arrupeteca.dto.response.NacionalidadResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.NacionalidadMapper;
import com.arrupeteca.persistence.entity.Nacionalidad;
import com.arrupeteca.persistence.repository.NacionalidadRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NacionalidadServiceImplTest extends BaseServiceTest {

    @Mock
    private NacionalidadRepository nacionalidadRepository;

    @Mock
    private NacionalidadMapper nacionalidadMapper;

    @InjectMocks
    private NacionalidadServiceImpl nacionalidadService;

    @Test
    @DisplayName("obtenerTodosActivos - Should return page of NacionalidadResponse")
    void obtenerTodosActivos() {
        Nacionalidad entity = Nacionalidad.builder().id(1L).nombre("Test").build();
        Page<Nacionalidad> page = new PageImpl<>(List.of(entity));
        NacionalidadResponse response = new NacionalidadResponse(1L, "Test", false);

        when(nacionalidadRepository.buscarActivosPorNombre(any())).thenReturn(List.of(entity));
        when(nacionalidadMapper.toResponse(entity)).thenReturn(response);

        List<NacionalidadResponse> result = nacionalidadService.obtenerTodosActivos(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(response);
    }



    @Test
    @DisplayName("obtenerTodos - Should return page of NacionalidadResponse")
    void obtenerTodos() {
        Nacionalidad entity = Nacionalidad.builder().id(1L).nombre("Test").build();
        Page<Nacionalidad> page = new PageImpl<>(List.of(entity));
        NacionalidadResponse response = new NacionalidadResponse(1L, "Test", false);
        
        when(nacionalidadRepository.busquedaAdmin(any(), any(), any())).thenReturn(page);
        when(nacionalidadMapper.toResponse(entity)).thenReturn(response);

        Page<NacionalidadResponse> result = nacionalidadService.obtenerTodos(null, null, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorId - Should return NacionalidadResponse when found")
    void obtenerPorIdSuccess() {
        Nacionalidad entity = Nacionalidad.builder().id(1L).nombre("Test").build();
        NacionalidadResponse response = new NacionalidadResponse(1L, "Test", false);
        
        when(nacionalidadRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(nacionalidadMapper.toResponse(entity)).thenReturn(response);

        NacionalidadResponse result = nacionalidadService.obtenerPorId(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("obtenerPorId - Should throw exception when not found")
    void obtenerPorIdNotFound() {
        when(nacionalidadRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> nacionalidadService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con ID: 1");
    }

    @Test
    @DisplayName("crear - Should create and return NacionalidadResponse")
    void crearSuccess() {
        NacionalidadRequest request = NacionalidadRequest.builder().nombre("Nuevo").build();
        Nacionalidad entity = Nacionalidad.builder().id(1L).nombre("Nuevo").build();
        Nacionalidad savedEntity = Nacionalidad.builder().id(1L).nombre("Nuevo").build();
        NacionalidadResponse response = new NacionalidadResponse(1L, "Nuevo", false);

        when(nacionalidadRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(false);
        when(nacionalidadMapper.toEntity(request)).thenReturn(entity);
        when(nacionalidadRepository.save(entity)).thenReturn(savedEntity);
        when(nacionalidadMapper.toResponse(savedEntity)).thenReturn(response);

        NacionalidadResponse result = nacionalidadService.crear(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("crear - Should throw exception when name exists")
    void crearDuplicateName() {
        NacionalidadRequest request = NacionalidadRequest.builder().nombre("Existe").build();

        when(nacionalidadRepository.existsByNombreIgnoreCase("Existe")).thenReturn(true);

        assertThatThrownBy(() -> nacionalidadService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con el nombre: Existe");
    }

    @Test
    @DisplayName("actualizar - Should update and return NacionalidadResponse")
    void actualizarSuccess() {
        Long id = 1L;
        NacionalidadRequest request = NacionalidadRequest.builder().nombre("Actualizado").build();
        Nacionalidad existingEntity = Nacionalidad.builder().id(1L).nombre("Original").build();
        NacionalidadResponse response = new NacionalidadResponse(id, "Actualizado", false);

        when(nacionalidadRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(nacionalidadRepository.existsByNombreIgnoreCase("Actualizado")).thenReturn(false);
        when(nacionalidadRepository.save(existingEntity)).thenReturn(existingEntity);
        when(nacionalidadMapper.toResponse(existingEntity)).thenReturn(response);

        NacionalidadResponse result = nacionalidadService.actualizar(id, request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("actualizar - Should throw exception when not found")
    void actualizarNotFound() {
        Long id = 1L;
        NacionalidadRequest request = NacionalidadRequest.builder().nombre("Cualquiera").build();

        when(nacionalidadRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> nacionalidadService.actualizar(id, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con ID: 1");
    }

    @Test
    @DisplayName("actualizar - Should throw exception when updating to existing name")
    void actualizarDuplicateName() {
        Long id = 1L;
        NacionalidadRequest request = NacionalidadRequest.builder().nombre("Existe").build();
        Nacionalidad existingEntity = Nacionalidad.builder().id(1L).nombre("Original").build();

        when(nacionalidadRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(nacionalidadRepository.existsByNombreIgnoreCase("Existe")).thenReturn(true);

        assertThatThrownBy(() -> nacionalidadService.actualizar(id, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con el nombre: Existe");
    }

    @Test
    @DisplayName("cambiarEstado - Should activate when state is true")
    void cambiarEstadoActivar() {
        when(nacionalidadRepository.existsById(1L)).thenReturn(true);

        nacionalidadService.cambiarEstado(1L, true);

        verify(nacionalidadRepository).activar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should deactivate when state is false")
    void cambiarEstadoDesactivar() {
        when(nacionalidadRepository.existsById(1L)).thenReturn(true);

        nacionalidadService.cambiarEstado(1L, false);

        verify(nacionalidadRepository).desactivar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should throw exception when not found")
    void cambiarEstadoNotFound() {
        when(nacionalidadRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> nacionalidadService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con ID: 1");
    }
}
