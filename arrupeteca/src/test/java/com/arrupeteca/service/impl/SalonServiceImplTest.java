package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.SalonRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.SalonMapper;
import com.arrupeteca.persistence.entity.Planta;
import com.arrupeteca.persistence.entity.Salon;
import com.arrupeteca.persistence.projection.SalonResumen;
import com.arrupeteca.persistence.repository.PlantaRepository;
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
import static org.mockito.Mockito.*;

class SalonServiceImplTest extends BaseServiceTest {
    @Mock private SalonRepository repository;
    @Mock private SalonMapper mapper;
    @Mock private PlantaRepository plantaRepository;
    @InjectMocks private SalonServiceImpl service;

    @Test
    @DisplayName("Should return paginated active salons")
    void shouldReturnPaginatedActive() {
        Page<SalonResumen> page = new PageImpl<>(List.of());
        when(repository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerTodosActivos(Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    @DisplayName("Should return salon by id when active")
    void shouldReturnByIdActivo() {
        SalonResumen resumen = mock(SalonResumen.class);
        when(repository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(resumen));
        assertThat(service.obtenerPorIdActivo(1L)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw exception when active salon not found")
    void shouldThrowWhenByIdActivoNotFound() {
        when(repository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("Should return all salons")
    void shouldReturnAll() {
        Page<SalonResumen> page = new PageImpl<>(List.of());
        when(repository.findAllProjectedBy(any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerTodos(Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    @DisplayName("Should return salon by id")
    void shouldReturnById() {
        SalonResumen resumen = mock(SalonResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));
        assertThat(service.obtenerPorId(1L)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should return salons by floor")
    void shouldReturnByPlanta() {
        Page<SalonResumen> page = new PageImpl<>(List.of());
        when(plantaRepository.existsById(1L)).thenReturn(true);
        when(repository.qBuscarPorPlantaIdYActivo(eq(1L), any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerPorIdPlanta(1L, Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    @DisplayName("Should create salon")
    void shouldCreate() {
        SalonRequest request = new SalonRequest();
        request.setNombre("Salon A");
        request.setIdPlanta(1L);

        when(repository.qExistePorNombreYPlantaId(anyString(), anyLong())).thenReturn(false);
        when(plantaRepository.findById(1L)).thenReturn(Optional.of(new Planta()));
        
        Salon entity = new Salon();
        entity.setId(1L);
        when(mapper.toEntity(any())).thenReturn(entity);
        when(repository.save(any())).thenReturn(entity);
        
        SalonResumen resumen = mock(SalonResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        assertThat(service.crear(request)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw exception when creating salon with duplicate name in floor")
    void shouldThrowWhenCreateDuplicate() {
        SalonRequest request = new SalonRequest();
        request.setNombre("Salon A");
        request.setIdPlanta(1L);

        when(repository.qExistePorNombreYPlantaId("Salon A", 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    @DisplayName("Should update salon")
    void shouldUpdate() {
        SalonRequest request = new SalonRequest();
        request.setNombre("Salon B");
        request.setIdPlanta(1L);

        Salon existing = new Salon();
        existing.setId(1L);
        existing.setNombre("Salon A");
        
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.qExistePorNombreYPlantaId("Salon B", 1L)).thenReturn(false);
        when(plantaRepository.findById(1L)).thenReturn(Optional.of(new Planta()));
        
        SalonResumen resumen = mock(SalonResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        assertThat(service.actualizar(1L, request)).isEqualTo(resumen);
        verify(repository).save(existing);
    }

    @Test
    @DisplayName("Should activate salon")
    void shouldActivate() {
        when(repository.existsById(1L)).thenReturn(true);
        service.cambiarEstado(1L, true);
        verify(repository).activarSalon(1L);
    }

    @Test
    @DisplayName("Should deactivate salon")
    void shouldDeactivate() {
        when(repository.existsById(1L)).thenReturn(true);
        service.cambiarEstado(1L, false);
        verify(repository).desactivarSalon(1L);
    }
}
