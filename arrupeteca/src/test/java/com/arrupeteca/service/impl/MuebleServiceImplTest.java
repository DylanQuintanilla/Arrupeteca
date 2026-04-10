package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.MuebleRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.MuebleMapper;
import com.arrupeteca.persistence.entity.Mueble;
import com.arrupeteca.persistence.entity.Salon;
import com.arrupeteca.persistence.projection.MuebleResumen;
import com.arrupeteca.persistence.repository.MuebleRepository;
import com.arrupeteca.persistence.repository.SalonRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

class MuebleServiceImplTest extends BaseServiceTest {
    @Mock private MuebleRepository repository;
    @Mock private MuebleMapper mapper;
    @Mock private SalonRepository salonRepository;
    @InjectMocks private MuebleServiceImpl service;

    @Test
    @DisplayName("Should return paginated active muebles")
    void shouldReturnPaginatedActive() {
        Page<MuebleResumen> page = new PageImpl<>(List.of());
        when(repository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerTodosActivos(Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    @DisplayName("Should return mueble by id when active")
    void shouldReturnByIdActivo() {
        MuebleResumen resumen = mock(MuebleResumen.class);
        when(repository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(resumen));
        assertThat(service.obtenerPorIdActivo(1L)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw exception when active mueble not found")
    void shouldThrowWhenActiveNotFound() {
        when(repository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Mueble activo con ID: 1");
    }

    @Test
    @DisplayName("Should return all muebles")
    void shouldReturnAll() {
        Page<MuebleResumen> page = new PageImpl<>(List.of());
        when(repository.findAllProjectedBy(any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerTodos(Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    @DisplayName("Should return mueble by id")
    void shouldReturnById() {
        MuebleResumen resumen = mock(MuebleResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));
        assertThat(service.obtenerPorId(1L)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw exception when mueble not found by id")
    void shouldThrowWhenNotFoundById() {
        when(repository.findProjectedById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Mueble con ID: 1");
    }

    @Test
    @DisplayName("Should return search results")
    void shouldReturnSearch() {
        Page<MuebleResumen> page = new PageImpl<>(List.of());
        when(repository.busquedaGlobal(anyString(), any(Pageable.class))).thenReturn(page);
        assertThat(service.busquedaGlobal("test", Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    @DisplayName("Should return busqueda maestra admin results")
    void shouldReturnBusquedaMaestraAdmin() {
        Page<MuebleResumen> page = new PageImpl<>(List.of());
        when(repository.busquedaMaestraAdmin(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        assertThat(service.busquedaMaestraAdmin(false, 1L, 1L, 1L, "term", Pageable.unpaged()))
                .isEqualTo(page);
        
        verify(repository).busquedaMaestraAdmin(eq(false), eq(1L), eq(1L), eq(1L), eq("term"), any(Pageable.class));
    }

    @Test
    @DisplayName("Should create mueble")
    void shouldCreate() {
        MuebleRequest request = new MuebleRequest();
        request.setNombre("Mueble 1");
        request.setIdSalon(1L);

        when(repository.existePorNombreYSalonId(anyString(), anyLong())).thenReturn(false);
        when(salonRepository.findById(1L)).thenReturn(Optional.of(new Salon()));

        Mueble entity = new Mueble();
        entity.setId(1L);
        when(mapper.toEntity(any())).thenReturn(entity);
        when(repository.save(any())).thenReturn(entity);

        MuebleResumen resumen = mock(MuebleResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        assertThat(service.crear(request)).isEqualTo(resumen);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("Should throw exception when name exists in salon on create")
    void shouldThrowWhenNameExistsOnCreate() {
        MuebleRequest request = new MuebleRequest();
        request.setNombre("Mueble 1");
        request.setIdSalon(1L);
        when(repository.existePorNombreYSalonId("Mueble 1", 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un mueble con el nombre 'Mueble 1' en ese salón");
    }

    @Test
    @DisplayName("Should throw exception when salon not found on create")
    void shouldThrowWhenSalonNotFoundOnCreate() {
        MuebleRequest request = new MuebleRequest();
        request.setNombre("Mueble 1");
        request.setIdSalon(1L);
        when(repository.existePorNombreYSalonId("Mueble 1", 1L)).thenReturn(false);
        when(salonRepository.findById(1L)).thenReturn(Optional.empty());
        when(mapper.toEntity(any())).thenReturn(new Mueble());

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("El Salón indicado no existe");
    }

    @Test
    @DisplayName("Should update mueble")
    void shouldUpdate() {
        MuebleRequest request = new MuebleRequest();
        request.setNombre("Mueble 2");
        request.setIdSalon(1L);

        Mueble existing = new Mueble();
        existing.setId(1L);
        existing.setNombre("Mueble 1");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existePorNombreYSalonId("Mueble 2", 1L)).thenReturn(false);
        when(salonRepository.findById(1L)).thenReturn(Optional.of(new Salon()));

        MuebleResumen resumen = mock(MuebleResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        assertThat(service.actualizar(1L, request)).isEqualTo(resumen);
        verify(repository).save(existing);
    }

    @Test
    @DisplayName("Should throw exception when mueble not found on update")
    void shouldThrowWhenNotFoundOnUpdate() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.actualizar(1L, new MuebleRequest()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Mueble con ID: 1");
    }

    @Test
    @DisplayName("Should throw exception when name already exists in salon on update")
    void shouldThrowWhenNameExistsOnUpdate() {
        MuebleRequest request = new MuebleRequest();
        request.setNombre("Mueble 2");
        request.setIdSalon(1L);

        Mueble existing = new Mueble();
        existing.setId(1L);
        existing.setNombre("Mueble 1");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existePorNombreYSalonId("Mueble 2", 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un mueble con el nombre 'Mueble 2' en ese salón");
    }

    @Test
    @DisplayName("Should activate mueble")
    void shouldActivate() {
        when(repository.existsById(1L)).thenReturn(true);
        service.cambiarEstado(1L, true);
        verify(repository).activar(1L);
    }

    @Test
    @DisplayName("Should deactivate mueble")
    void shouldDeactivate() {
        when(repository.existsById(1L)).thenReturn(true);
        service.cambiarEstado(1L, false);
        verify(repository).desactivar(1L);
    }

    @Test
    @DisplayName("Should throw exception when mueble not found on status change")
    void shouldThrowWhenNotFoundOnStatusChange() {
        when(repository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> service.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró Mueble con ID: 1");
        verify(repository, never()).activar(any());
        verify(repository, never()).desactivar(any());
    }
}

