package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.NacionalidadRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.NacionalidadMapper;
import com.arrupeteca.persistence.entity.Nacionalidad;
import com.arrupeteca.persistence.projection.NacionalidadResumen;
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
import static org.mockito.Mockito.mock;
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
    @DisplayName("Should return all active nationalities paginated")
    void shouldReturnAllActive() {
        Page<NacionalidadResumen> expectedPage = new PageImpl<>(List.of(mock(NacionalidadResumen.class)));
        when(nacionalidadRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(expectedPage);

        Page<NacionalidadResumen> result = nacionalidadService.obtenerTodasActivas(Pageable.unpaged());

        assertThat(result).isEqualTo(expectedPage);
        verify(nacionalidadRepository).findByBorradoLogicoFalse(any(Pageable.class));
    }

    @Test
    @DisplayName("Should return nationality by ID and active")
    void shouldReturnByIdAndActive() {
        NacionalidadResumen resumen = mock(NacionalidadResumen.class);
        when(nacionalidadRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.of(resumen));

        NacionalidadResumen result = nacionalidadService.obtenerPorIdActivo(1L);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw exception when nationality not found or inactive")
    void shouldThrowWhenNotFoundOrInactive() {
        when(nacionalidadRepository.findProjectedByIdAndBorradoLogicoFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> nacionalidadService.obtenerPorIdActivo(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("La nacionalidad con ID 1 no existe, o no está activa");
    }

    @Test
    @DisplayName("Should return all nationalities for admin")
    void shouldReturnAllAdmin() {
        Page<NacionalidadResumen> expectedPage = new PageImpl<>(List.of(mock(NacionalidadResumen.class)));
        when(nacionalidadRepository.findAllProjectedBy(any(Pageable.class))).thenReturn(expectedPage);

        Page<NacionalidadResumen> result = nacionalidadService.obtenerTodas(Pageable.unpaged());

        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    @DisplayName("Should return nationality by ID")
    void shouldReturnById() {
        NacionalidadResumen resumen = mock(NacionalidadResumen.class);
        when(nacionalidadRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        NacionalidadResumen result = nacionalidadService.obtenerPorId(1L);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw exception when nationality not found")
    void shouldThrowWhenNotFound() {
        when(nacionalidadRepository.findProjectedById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> nacionalidadService.obtenerPorId(1L))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("La nacionalidad con ID 1 no existe");
    }

    @Test
    @DisplayName("Should create nationality when name is unique")
    void shouldCreateNationality() {
        NacionalidadRequest request = new NacionalidadRequest();
        request.setNombre("Nueva");

        when(nacionalidadRepository.existsByNombreIgnoreCase("Nueva")).thenReturn(false);
        when(nacionalidadMapper.toEntity(any())).thenReturn(new Nacionalidad());
        Nacionalidad saved = new Nacionalidad();
        saved.setId(1L);
        when(nacionalidadRepository.save(any())).thenReturn(saved);
        
        NacionalidadResumen resumen = mock(NacionalidadResumen.class);
        when(nacionalidadRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        NacionalidadResumen result = nacionalidadService.crear(request);

        assertThat(result).isEqualTo(resumen);
        verify(nacionalidadRepository).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating nationality with duplicate name")
    void shouldThrowWhenDuplicateName() {
        NacionalidadRequest request = new NacionalidadRequest();
        request.setNombre("Duplicada");

        when(nacionalidadRepository.existsByNombreIgnoreCase("Duplicada")).thenReturn(true);

        assertThatThrownBy(() -> nacionalidadService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe una nacionalidad con el nombre: Duplicada");
    }

    @Test
    @DisplayName("Should update nationality when name is unique or same")
    void shouldUpdateNationality() {
        NacionalidadRequest request = new NacionalidadRequest();
        request.setNombre("Actualizada");

        Nacionalidad existing = new Nacionalidad();
        existing.setId(1L);
        existing.setNombre("Antigua");

        when(nacionalidadRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(nacionalidadRepository.existsByNombreIgnoreCase("Actualizada")).thenReturn(false);
        
        NacionalidadResumen resumen = mock(NacionalidadResumen.class);
        when(nacionalidadRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        NacionalidadResumen result = nacionalidadService.actualizar(1L, request);

        assertThat(result).isEqualTo(resumen);
        verify(nacionalidadMapper).updateEntity(request, existing);
        verify(nacionalidadRepository).save(existing);
    }

    @Test
    @DisplayName("Should throw exception when updating nationality and new name exists")
    void shouldThrowWhenUpdatingToExistingName() {
        NacionalidadRequest request = new NacionalidadRequest();
        request.setNombre("Existente");

        Nacionalidad existing = new Nacionalidad();
        existing.setId(1L);
        existing.setNombre("Antigua");

        when(nacionalidadRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(nacionalidadRepository.existsByNombreIgnoreCase("Existente")).thenReturn(true);

        assertThatThrownBy(() -> nacionalidadService.actualizar(1L, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe una nacionalidad con el nombre: Existente");
    }

    @Test
    @DisplayName("Should update nationality when name is same (ignore case)")
    void shouldUpdateWhenNameSame() {
        NacionalidadRequest request = new NacionalidadRequest();
        request.setNombre("NACIONALIDAD");

        Nacionalidad existing = new Nacionalidad();
        existing.setId(1L);
        existing.setNombre("nacionalidad");

        when(nacionalidadRepository.findById(1L)).thenReturn(Optional.of(existing));
        
        NacionalidadResumen resumen = mock(NacionalidadResumen.class);
        when(nacionalidadRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        NacionalidadResumen result = nacionalidadService.actualizar(1L, request);

        assertThat(result).isEqualTo(resumen);
    }

    @Test
    @DisplayName("Should throw when updating non-existent nationality")
    void shouldThrowWhenUpdatingNonExistent() {
        when(nacionalidadRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> nacionalidadService.actualizar(1L, new NacionalidadRequest()))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("La nacionalidad con ID 1 no existe");
    }

    @Test
    @DisplayName("Should deactivate nationality")
    void shouldDeactivate() {
        when(nacionalidadRepository.existsById(1L)).thenReturn(true);

        nacionalidadService.cambiarEstado(1L, false);

        verify(nacionalidadRepository).desactivar(1L);
    }

    @Test
    @DisplayName("Should activate nationality")
    void shouldActivate() {
        when(nacionalidadRepository.existsById(1L)).thenReturn(true);

        nacionalidadService.cambiarEstado(1L, true);

        verify(nacionalidadRepository).activar(1L);
    }

    @Test
    @DisplayName("Should throw when changing state of non-existent nationality")
    void shouldThrowWhenChangingStateNonExistent() {
        when(nacionalidadRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> nacionalidadService.cambiarEstado(1L, true))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("La nacionalidad con ID 1 no existe");
    }
}

