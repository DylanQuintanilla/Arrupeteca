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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

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
    @DisplayName("Should throw exception when active plant not found")
    void shouldThrowWhenActiveNotFound() {
        when(repository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe una planta activa con el ID: 1");
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
    @DisplayName("Should throw exception when plant not found by id")
    void shouldThrowWhenNotFoundById() {
        when(repository.findProjectedById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe una planta con el ID: 1");
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
    @DisplayName("Should throw exception when edificio not found in obtenerPorIdEdificio")
    void shouldThrowWhenEdificioNotFoundInObtenerPorId() {
        when(edificioRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> service.obtenerPorIdEdificio(1L, Pageable.unpaged()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe un edificio con ID: 1");
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
    @DisplayName("Should throw exception when name exists in building on create")
    void shouldThrowWhenNameExistsOnCreate() {
        PlantaRequest request = new PlantaRequest();
        request.setNombre("Planta 1");
        request.setIdEdificio(1L);
        when(repository.qExistePorNombreYEdificioId("Planta 1", 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe la planta 'Planta 1' en el edificio indicado");
    }

    @Test
    @DisplayName("Should throw exception when edificio not found on create")
    void shouldThrowWhenEdificioNotFoundOnCreate() {
        PlantaRequest request = new PlantaRequest();
        request.setNombre("Planta 1");
        request.setIdEdificio(1L);
        when(repository.qExistePorNombreYEdificioId("Planta 1", 1L)).thenReturn(false);
        when(edificioRepository.findById(1L)).thenReturn(Optional.empty());
        when(mapper.toEntity(any())).thenReturn(new Planta());

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No existe un edificio con ID: 1");
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
    @DisplayName("Should update plant when existing edificio is null")
    void shouldUpdateWhenExistingEdificioIsNull() {
        PlantaRequest request = new PlantaRequest();
        request.setNombre("Planta 2");
        request.setIdEdificio(1L);

        Planta existing = new Planta();
        existing.setId(1L);
        existing.setNombre("Planta 1");
        existing.setEdificio(null); // Explicitly null

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.qExistePorNombreYEdificioId("Planta 2", 1L)).thenReturn(false);
        when(edificioRepository.findById(1L)).thenReturn(Optional.of(new Edificio()));

        PlantaResumen resumen = mock(PlantaResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        assertThat(service.actualizar(1L, request)).isEqualTo(resumen);
        verify(repository).save(existing);
    }

    @Test
    @DisplayName("Should update plant when name changes but building stays the same")
    void shouldUpdateWhenNameChangesSameBuilding() {
        PlantaRequest request = new PlantaRequest();
        request.setNombre("Planta 2");
        request.setIdEdificio(1L);

        Planta existing = new Planta();
        existing.setId(1L);
        existing.setNombre("Planta 1");
        existing.setEdificio(new Edificio());
        existing.getEdificio().setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.qExistePorNombreYEdificioId("Planta 2", 1L)).thenReturn(false);
        when(edificioRepository.findById(1L)).thenReturn(Optional.of(existing.getEdificio()));

        PlantaResumen resumen = mock(PlantaResumen.class);
        when(repository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        assertThat(service.actualizar(1L, request)).isEqualTo(resumen);
        verify(repository).save(existing);
    }

    @Test
    @DisplayName("Should throw exception when plant not found on update")
    void shouldThrowWhenNotFoundOnUpdate() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.actualizar(1L, new PlantaRequest()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró planta con ID: 1");
    }

    @Test
    @DisplayName("Should throw exception when name already exists in building on update")
    void shouldThrowWhenNameExistsOnUpdate() {
        PlantaRequest request = new PlantaRequest();
        request.setNombre("Planta 2");
        request.setIdEdificio(1L);

        Planta existing = new Planta();
        existing.setId(1L);
        existing.setNombre("Planta 1");
        existing.setEdificio(new Edificio());
        existing.getEdificio().setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.qExistePorNombreYEdificioId("Planta 2", 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe una planta con el nombre 'Planta 2' en este edificio");
    }

    @Test
    @DisplayName("Should throw exception when edificio changes and name already exists in new building")
    void shouldThrowWhenEdificioChangesNameExists() {
        PlantaRequest request = new PlantaRequest();
        request.setNombre("Planta 1");
        request.setIdEdificio(2L); // Changed building

        Planta existing = new Planta();
        existing.setId(1L);
        existing.setNombre("Planta 1"); // Same name
        existing.setEdificio(new Edificio());
        existing.getEdificio().setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.qExistePorNombreYEdificioId("Planta 1", 2L)).thenReturn(true);

        assertThatThrownBy(() -> service.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class);
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

    @Test
    @DisplayName("Should throw exception when plant not found on status change")
    void shouldThrowWhenNotFoundOnStatusChange() {
        when(repository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> service.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró planta con ID: 1");
        verify(repository, never()).activarPlanta(any());
        verify(repository, never()).desactivarPlanta(any());
    }
}

