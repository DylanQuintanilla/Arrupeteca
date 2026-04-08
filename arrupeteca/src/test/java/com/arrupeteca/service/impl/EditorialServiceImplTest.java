package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EditorialRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EditorialMapper;
import com.arrupeteca.persistence.entity.Editorial;
import com.arrupeteca.persistence.projection.EditorialResumen;
import com.arrupeteca.persistence.repository.EditorialRepository;
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

class EditorialServiceImplTest extends BaseServiceTest {

    @Mock
    private EditorialRepository editorialRepository;

    @Mock
    private EditorialMapper editorialMapper;

    @InjectMocks
    private EditorialServiceImpl editorialService;

    @Test
    @DisplayName("Should return all active editorials paginated")
    void shouldReturnAllActive() {
        Page<EditorialResumen> expectedPage = new PageImpl<>(List.of(mock(EditorialResumen.class)));
        when(editorialRepository.findByBorradoLogicoFalse(any(Pageable.class))).thenReturn(expectedPage);

        Page<EditorialResumen> result = editorialService.obtenerTodosActivos(Pageable.unpaged());

        assertThat(result).isEqualTo(expectedPage);
        verify(editorialRepository).findByBorradoLogicoFalse(any(Pageable.class));
    }

    @Test
    @DisplayName("Should create editorial when name and email are unique")
    void shouldCreateEditorial() {
        EditorialRequest request = new EditorialRequest();
        request.setNombre("Nueva Editorial");
        request.setCorreo("nueva@editorial.com");

        when(editorialRepository.existsByNombreIgnoreCase("Nueva Editorial")).thenReturn(false);
        when(editorialRepository.existsByCorreoIgnoreCase("nueva@editorial.com")).thenReturn(false);
        when(editorialMapper.toEntity(any())).thenReturn(new Editorial());
        
        Editorial saved = new Editorial();
        saved.setId(1L);
        when(editorialRepository.save(any())).thenReturn(saved);
        
        EditorialResumen resumen = mock(EditorialResumen.class);
        when(editorialRepository.findProjectedById(1L)).thenReturn(Optional.of(resumen));

        EditorialResumen result = editorialService.crear(request);

        assertThat(result).isEqualTo(resumen);
        verify(editorialRepository).save(any());
    }

    @Test
    @DisplayName("Should throw exception when email is duplicate")
    void shouldThrowWhenDuplicateEmail() {
        EditorialRequest request = new EditorialRequest();
        request.setNombre("Nueva");
        request.setCorreo("duplicado@e.com");

        when(editorialRepository.existsByNombreIgnoreCase("Nueva")).thenReturn(false);
        when(editorialRepository.existsByCorreoIgnoreCase("duplicado@e.com")).thenReturn(true);

        assertThatThrownBy(() -> editorialService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("ya está registrado");
    }
}
