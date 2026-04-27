package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.GeneroRequest;
import com.arrupeteca.dto.response.detail.GeneroDetalleResponse;
import com.arrupeteca.dto.response.summary.GeneroResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.GeneroMapper;
import com.arrupeteca.persistence.entity.Genero;
import com.arrupeteca.persistence.repository.GeneroRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeneroServiceImplTest {

    @Mock
    private GeneroRepository generoRepository;

    @Mock
    private GeneroMapper generoMapper;

    @InjectMocks
    private GeneroServiceImpl generoService;

    @Test
    @DisplayName("obtenerTodosActivos - Should return page of Resumen")
    void obtenerTodosActivos() {
        Genero entity = Genero.builder().id(1L).nombre("Test").build();
        Page<Genero> page = new PageImpl<>(List.of(entity));
        GeneroResumen resumen = new GeneroResumen(1L, "Test", false);

        when(generoRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        when(generoMapper.toResumen(entity)).thenReturn(resumen);

        Page<GeneroResumen> result = generoService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should return DetalleResponse")
    void obtenerPorIdActivo() {
        Genero entity = Genero.builder().id(1L).nombre("Test").build();
        GeneroDetalleResponse detail = new GeneroDetalleResponse(
                1L, "Test", false, "admin", null, "admin", null, Collections.emptySet());

        when(generoRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(entity));
        when(generoMapper.toDetalleResponse(entity)).thenReturn(detail);

        GeneroDetalleResponse result = generoService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("obtenerPorIdActivo - Should throw exception when not found")
    void obtenerPorIdActivoNotFound() {
        when(generoRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> generoService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe un género con el ID: 1");
    }

    @Test
    @DisplayName("obtenerTodos - Should return all genres")
    void obtenerTodos() {
        Genero entity = Genero.builder().id(1L).nombre("Test").build();
        Page<Genero> page = new PageImpl<>(List.of(entity));
        GeneroResumen resumen = new GeneroResumen(1L, "Test", false);

        when(generoRepository.findAllProjectedBy(any(Pageable.class))).thenReturn(page);
        when(generoMapper.toResumen(entity)).thenReturn(resumen);

        Page<GeneroResumen> result = generoService.obtenerTodos(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("obtenerPorId - Should return DetalleResponse")
    void obtenerPorId() {
        Genero entity = Genero.builder().id(1L).nombre("Test").build();
        GeneroDetalleResponse detail = mock(GeneroDetalleResponse.class);
        when(generoRepository.findProjectedById(1L)).thenReturn(Optional.of(entity));
        when(generoMapper.toDetalleResponse(entity)).thenReturn(detail);

        GeneroDetalleResponse result = generoService.obtenerPorId(1L);

        assertThat(result).isEqualTo(detail);
    }

    @Test
    @DisplayName("obtenerPorId - Should throw exception when not found")
    void obtenerPorIdNotFound() {
        when(generoRepository.findProjectedById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> generoService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe un género con el ID: 1");
    }

    @Test
    @DisplayName("crear - Should save and return DetalleResponse")
    void crear() {
        GeneroRequest request = new GeneroRequest();
        request.setNombre("Nuevo");
        Genero entity = Genero.builder().id(1L).nombre("Nuevo").build();
        GeneroDetalleResponse detail = new GeneroDetalleResponse(1L, "Nuevo", false, "admin", null, "admin", null, Collections.emptySet());

        when(generoRepository.existsByNombreIgnoreCase("Nuevo")).thenReturn(false);
        when(generoMapper.toEntity(request)).thenReturn(entity);
        when(generoRepository.save(any(Genero.class))).thenReturn(entity);
        when(generoMapper.toDetalleResponse(entity)).thenReturn(detail);

        GeneroDetalleResponse result = generoService.crear(request);

        assertThat(result).isEqualTo(detail);
        verify(generoRepository).save(any(Genero.class));
    }

    @Test
    @DisplayName("crear - Should throw when name exists")
    void crearDuplicate() {
        GeneroRequest request = new GeneroRequest();
        request.setNombre("Existe");
        when(generoRepository.existsByNombreIgnoreCase("Existe")).thenReturn(true);

        assertThatThrownBy(() -> generoService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un género con el nombre: Existe");
    }

    @Test
    @DisplayName("actualizar - Should update and return DetalleResponse")
    void actualizar() {
        GeneroRequest request = new GeneroRequest();
        request.setNombre("Actualizado");
        Genero existing = Genero.builder().id(1L).nombre("Test").build();
        GeneroDetalleResponse detail = new GeneroDetalleResponse(
                1L, "Actualizado", false, "admin", null, "admin", null, Collections.emptySet());

        when(generoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(generoRepository.existsByNombreIgnoreCase("Actualizado")).thenReturn(false);
        when(generoRepository.save(existing)).thenReturn(existing);
        when(generoMapper.toDetalleResponse(existing)).thenReturn(detail);

        GeneroDetalleResponse result = generoService.actualizar(1L, request);

        assertThat(result).isEqualTo(detail);
        verify(generoMapper).updateEntity(request, existing);
    }

    @Test
    @DisplayName("actualizar - Should update with same name")
    void actualizarSameName() {
        GeneroRequest request = new GeneroRequest();
        request.setNombre("Test");
        Genero existing = Genero.builder().id(1L).nombre("Test").build();
        GeneroDetalleResponse detail = mock(GeneroDetalleResponse.class);

        when(generoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(generoRepository.save(existing)).thenReturn(existing);
        when(generoMapper.toDetalleResponse(existing)).thenReturn(detail);

        generoService.actualizar(1L, request);

        verify(generoRepository, never()).existsByNombreIgnoreCase(any());
    }

    @Test
    @DisplayName("actualizar - Should throw when new name duplicate")
    void actualizarDuplicate() {
        GeneroRequest request = new GeneroRequest();
        request.setNombre("Ocupado");
        Genero existing = Genero.builder().id(1L).nombre("Test").build();

        when(generoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(generoRepository.existsByNombreIgnoreCase("Ocupado")).thenReturn(true);

        assertThatThrownBy(() -> generoService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe otro género con el nombre: Ocupado");
    }

    @Test
    @DisplayName("actualizar - Should throw when not found")
    void actualizarNotFound() {
        when(generoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> generoService.actualizar(1L, new GeneroRequest()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe un género con el ID: 1");
    }

    @Test
    @DisplayName("cambiarEstado - Should activate")
    void activate() {
        when(generoRepository.existsById(1L)).thenReturn(true);
        generoService.cambiarEstado(1L, true);
        verify(generoRepository).activar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should deactivate")
    void deactivate() {
        when(generoRepository.existsById(1L)).thenReturn(true);
        generoService.cambiarEstado(1L, false);
        verify(generoRepository).desactivar(1L);
    }

    @Test
    @DisplayName("cambiarEstado - Should throw when not found")
    void statusNotFound() {
        when(generoRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> generoService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe un género con el ID: 1");
    }
}
