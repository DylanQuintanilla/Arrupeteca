package com.arrupeteca.service;

import java.util.List;

import com.arrupeteca.dto.request.ParentescoRequest;
import com.arrupeteca.dto.response.ParentescoResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.ParentescoMapper;
import com.arrupeteca.persistence.entity.Parentesco;
import com.arrupeteca.persistence.repository.ParentescoRepository;
import com.arrupeteca.service.impl.ParentescoServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ParentescoServiceImplTest {

    @Mock
    private ParentescoRepository parentescoRepository;

    @Mock
    private ParentescoMapper parentescoMapper;

    @InjectMocks
    private ParentescoServiceImpl parentescoService;

    @Test
    @DisplayName("Debe obtener todos los parentescos activos paginados")
    void debeObtenerTodosActivos() {
        Pageable pageable = PageRequest.of(0, 10);
        Parentesco entity = Parentesco.builder().id(1L).nombre("Padre").build();
        ParentescoResponse responseDto = ParentescoResponse.builder().id(1L).nombre("Padre").build();

        given(parentescoRepository.buscarActivosPorNombre(any()))
                .willReturn(List.of(entity));
        given(parentescoMapper.toResponse(entity)).willReturn(responseDto);

        List<ParentescoResponse> result = parentescoService.obtenerTodosActivos(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nombre()).isEqualTo("Padre");
    }

    @Test
    @DisplayName("Debe crear parentesco exitosamente")
    void debeCrearParentescoExitosamente() {
        ParentescoRequest request = ParentescoRequest.builder().nombre("Madre").build();
        Parentesco entity = Parentesco.builder().nombre("Madre").build();
        Parentesco entityGuardada = Parentesco.builder().id(2L).nombre("Madre").build();
        ParentescoResponse response = ParentescoResponse.builder().id(2L).nombre("Madre").build();

        given(parentescoRepository.existsByNombreIgnoreCase("Madre")).willReturn(false);
        given(parentescoMapper.toEntity(request)).willReturn(entity);
        given(parentescoRepository.save(entity)).willReturn(entityGuardada);
        given(parentescoMapper.toResponse(entityGuardada)).willReturn(response);

        ParentescoResponse result = parentescoService.crear(request);

        assertThat(result).isNotNull();
        assertThat(result.nombre()).isEqualTo("Madre");
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear con nombre duplicado")
    void debeLanzarExcepcionCrearDuplicado() {
        ParentescoRequest request = ParentescoRequest.builder().nombre("Padre").build();
        given(parentescoRepository.existsByNombreIgnoreCase("Padre")).willReturn(true);

        assertThatThrownBy(() -> parentescoService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("con el nombre:");
    }

}
