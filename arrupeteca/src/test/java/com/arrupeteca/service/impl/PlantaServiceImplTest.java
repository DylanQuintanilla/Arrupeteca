package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.PlantaRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.PlantaMapper;
import com.arrupeteca.persistence.entity.Edificio;
import com.arrupeteca.persistence.entity.Planta;
import com.arrupeteca.persistence.projection.PlantaResumen;
import com.arrupeteca.persistence.repository.EdificioRepository;
import com.arrupeteca.persistence.repository.PlantaRepository;
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

class PlantaServiceImplTest extends BaseServiceTest {
    @Mock private PlantaRepository repository;
    @Mock private PlantaMapper mapper;
    @Mock private EdificioRepository edificioRepository;
    @InjectMocks private PlantaServiceImpl service;

    @Test
    @DisplayName("Should return paginated active plants")
    void shouldReturnPaginatedActive() {
        Page<PlantaResumen> page = new PageImpl<>(List.of());
        when(repository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerTodosActivos(Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    @DisplayName("Should return plant by id when active")
    void shouldReturnByIdActivo() {
        PlantaResumen resumen = mock(PlantaResumen.class);
        when(repository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(resumen));
        assertThat(service.obtenerPorIdActivo(1L)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should return all plants")
    void shouldReturnAll() {
        Page<PlantaResumen> page = new PageImpl<>(List.of());
        when(repository.findAllProjectedBy(any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerTodos(Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    @DisplayName("Should return plant by id")
    void shouldReturnById() {
        PlantaResumen resumen = mock(PlantaResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));
        assertThat(service.obtenerPorId(1L)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should return plants by building")
    void shouldReturnByEdificio() {
        Page<PlantaResumen> page = new PageImpl<>(List.of());
        when(edificioRepository.existsById(1L)).thenReturn(true);
        when(repository.qBuscarPorEdificioIdYActivo(eq(1L), any(Pageable.class))).thenReturn(page);
        assertThat(service.obtenerPorIdEdificio(1L, Pageable.unpaged())).isEqualTo(page);
    }

    @Test
    @DisplayName("Should create plant")
    void shouldCreate() {
        PlantaRequest request = new PlantaRequest();
        request.setNombre("Planta 1");
        request.setIdEdificio(1L);

        when(repository.qExistePorNombreYEdificioId(anyString(), anyLong())).thenReturn(false);
        when(edificioRepository.findById(1L)).thenReturn(Optional.of(new Edificio()));
        
        Planta entity = new Planta();
        entity.setId(1L);
        when(mapper.toEntity(any())).thenReturn(entity);
        when(repository.save(any())).thenReturn(entity);
        
        PlantaResumen resumen = mock(PlantaResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        assertThat(service.crear(request)).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should update plant")
    void shouldUpdate() {
        PlantaRequest request = new PlantaRequest();
        request.setNombre("Planta 2");
        request.setIdEdificio(1L);

        Planta existing = new Planta();
        existing.setId(1L);
        existing.setNombre("Planta 1");
        
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.qExistePorNombreYEdificioId("Planta 2", 1L)).thenReturn(false);
        when(edificioRepository.findById(1L)).thenReturn(Optional.of(new Edificio()));
        
        PlantaResumen resumen = mock(PlantaResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        assertThat(service.actualizar(1L, request)).isEqualTo(resumen);
        verify(repository).save(existing);
    }

    @Test
    @DisplayName("Should activate plant")
    void shouldActivate() {
        when(repository.existsById(1L)).thenReturn(true);
        service.cambiarEstado(1L, true);
        verify(repository).activarPlanta(1L);
    }

    @Test
    @DisplayName("Should deactivate plant")
    void shouldDeactivate() {
        when(repository.existsById(1L)).thenReturn(true);
        service.cambiarEstado(1L, false);
        verify(repository).desactivarPlanta(1L);
    }
}
