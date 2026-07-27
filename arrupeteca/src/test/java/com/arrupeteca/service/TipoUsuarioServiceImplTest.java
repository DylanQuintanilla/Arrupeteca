package com.arrupeteca.service;

import java.util.List;

import com.arrupeteca.dto.request.TipoUsuarioRequest;
import com.arrupeteca.dto.response.TipoUsuarioResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.TipoUsuarioMapper;
import com.arrupeteca.persistence.entity.TipoUsuario;
import com.arrupeteca.persistence.repository.TipoUsuarioRepository;
import com.arrupeteca.service.impl.TipoUsuarioServiceImpl;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TipoUsuarioServiceImplTest {

    @Mock
    private TipoUsuarioRepository tipoUsuarioRepository;

    @Mock
    private TipoUsuarioMapper tipoUsuarioMapper;

    @InjectMocks
    private TipoUsuarioServiceImpl tipoUsuarioService;

    @Test
    @DisplayName("Debe obtener todos los tipos de usuario activos paginados")
    void debeObtenerTodosActivos() {
        Pageable pageable = PageRequest.of(0, 10);
        TipoUsuario entity = TipoUsuario.builder().id(1L).nombre("admin").build();
        TipoUsuarioResponse responseDto = TipoUsuarioResponse.builder().id(1L).nombre("admin").build();

        given(tipoUsuarioRepository.findByBorradoLogicoFalse(pageable))
                .willReturn(new PageImpl<>(List.of(entity)));
        given(tipoUsuarioMapper.toResponse(entity)).willReturn(responseDto);

        Page<TipoUsuarioResponse> result = tipoUsuarioService.obtenerTodosActivos(pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).nombre()).isEqualTo("admin");
    }

    @Test
    @DisplayName("Debe crear tipo de usuario exitosamente")
    void debeCrearTipoUsuarioExitosamente() {
        TipoUsuarioRequest request = TipoUsuarioRequest.builder().nombre("maestro").build();
        TipoUsuario entity = TipoUsuario.builder().nombre("maestro").build();
        TipoUsuario entityGuardada = TipoUsuario.builder().id(2L).nombre("maestro").build();
        TipoUsuarioResponse response = TipoUsuarioResponse.builder().id(2L).nombre("maestro").build();

        given(tipoUsuarioRepository.existsByNombreIgnoreCase("maestro")).willReturn(false);
        given(tipoUsuarioMapper.toEntity(request)).willReturn(entity);
        given(tipoUsuarioRepository.save(entity)).willReturn(entityGuardada);
        given(tipoUsuarioMapper.toResponse(entityGuardada)).willReturn(response);

        TipoUsuarioResponse result = tipoUsuarioService.crear(request);

        assertThat(result).isNotNull();
        assertThat(result.nombre()).isEqualTo("maestro");
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear con nombre duplicado")
    void debeLanzarExcepcionCrearDuplicado() {
        TipoUsuarioRequest request = TipoUsuarioRequest.builder().nombre("admin").build();
        given(tipoUsuarioRepository.existsByNombreIgnoreCase("admin")).willReturn(true);

        assertThatThrownBy(() -> tipoUsuarioService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un tipo de usuario con el nombre");
    }

    @Test
    @DisplayName("Debe desactivar tipo de usuario")
    void debeDesactivarTipoUsuario() {
        given(tipoUsuarioRepository.countByIdIncludeDeleted(1L)).willReturn(1L);

        tipoUsuarioService.cambiarEstado(1L, false);

        verify(tipoUsuarioRepository).desactivar(1L);
    }

}
