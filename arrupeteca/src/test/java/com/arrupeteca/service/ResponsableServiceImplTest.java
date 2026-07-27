package com.arrupeteca.service;

import java.util.Optional;

import com.arrupeteca.dto.request.ResponsableRequest;
import com.arrupeteca.dto.response.ResponsableResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.ResponsableMapper;
import com.arrupeteca.persistence.entity.Parentesco;
import com.arrupeteca.persistence.entity.Responsable;
import com.arrupeteca.persistence.entity.Usuario;
import com.arrupeteca.persistence.repository.ParentescoRepository;
import com.arrupeteca.persistence.repository.ResponsableRepository;
import com.arrupeteca.persistence.repository.UsuarioRepository;
import com.arrupeteca.service.impl.ResponsableServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ResponsableServiceImplTest {

    @Mock
    private ResponsableRepository responsableRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ParentescoRepository parentescoRepository;

    @Mock
    private ResponsableMapper responsableMapper;

    @InjectMocks
    private ResponsableServiceImpl responsableService;

    @Test
    @DisplayName("Debe crear responsable exitosamente")
    void debeCrearResponsableExitosamente() {
        ResponsableRequest request = ResponsableRequest.builder()
                .idUsuario(1L)
                .nombres("Maria")
                .apellidos("Gómez")
                .correo("maria@gmail.com")
                .idParentesco(2L)
                .build();

        Usuario usuario = Usuario.builder().id(1L).correo("alumno@arrupeteca.com").build();
        Parentesco parentesco = Parentesco.builder().id(2L).nombre("Madre").build();
        Responsable entity = Responsable.builder().nombres("Maria").apellidos("Gómez").correo("maria@gmail.com").build();
        Responsable guardado = Responsable.builder().id(10L).nombres("Maria").apellidos("Gómez").correo("maria@gmail.com").build();
        ResponsableResponse response = ResponsableResponse.builder().id(10L).nombres("Maria").correo("maria@gmail.com").build();

        given(usuarioRepository.findById(1L)).willReturn(Optional.of(usuario));
        given(parentescoRepository.findById(2L)).willReturn(Optional.of(parentesco));
        given(responsableMapper.toEntity(request)).willReturn(entity);
        given(responsableRepository.save(entity)).willReturn(guardado);
        given(responsableMapper.toResponse(guardado)).willReturn(response);

        ResponsableResponse result = responsableService.crear(request);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear con usuario inexistente")
    void debeLanzarExcepcionUsuarioInexistente() {
        ResponsableRequest request = ResponsableRequest.builder()
                .idUsuario(99L)
                .nombres("Maria")
                .apellidos("Gómez")
                .correo("maria@gmail.com")
                .idParentesco(2L)
                .build();

        given(usuarioRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> responsableService.crear(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("No se encontró el usuario con ID: 99");
    }

}
