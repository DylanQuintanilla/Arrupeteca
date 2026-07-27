package com.arrupeteca.service.impl;

import java.time.LocalDateTime;

import com.arrupeteca.dto.request.LoginRequest;
import com.arrupeteca.dto.request.RegisterRequest;
import com.arrupeteca.dto.response.AuthResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.persistence.entity.TipoUsuario;
import com.arrupeteca.persistence.entity.Usuario;
import com.arrupeteca.persistence.repository.TipoUsuarioRepository;
import com.arrupeteca.persistence.repository.UsuarioRepository;
import com.arrupeteca.security.JwtService;
import com.arrupeteca.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final TipoUsuarioRepository tipoUsuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Procesando registro de usuario con correo: {}", request.correo());

        String correoTrimmed = request.correo().trim().toLowerCase();
        if (usuarioRepository.existsByCorreo(correoTrimmed)) {
            throw new ValidacionNegocioException("Ya existe un usuario registrado con el correo: " + correoTrimmed);
        }

        TipoUsuario tipoUsuario = tipoUsuarioRepository.findByNombreAndBorradoLogicoFalse("alumno")
                .orElseGet(() -> tipoUsuarioRepository.findById(2L)
                        .filter(t -> !t.isBorradoLogico())
                        .orElseThrow(() -> new ValidacionNegocioException("No se encontró el tipo de usuario por defecto (alumno)")));

        Usuario nuevoUsuario = Usuario.builder()
                .nombres(request.nombres().trim())
                .apellidos(request.apellidos().trim())
                .correo(correoTrimmed)
                .contrasena(passwordEncoder.encode(request.contrasena()))
                .carnet(request.carnet() != null ? request.carnet().trim() : null)
                .tipoUsuario(tipoUsuario)
                .bloqueadoPorMora(false)
                .borradoLogico(false)
                .build();

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
        log.info("Usuario registrado exitosamente con ID: {}", usuarioGuardado.getId());

        String token = jwtService.generateToken(usuarioGuardado);

        return AuthResponse.builder()
                .token(token)
                .tipo(tipoUsuario.getNombre())
                .correo(usuarioGuardado.getCorreo())
                .nombreCompleto(usuarioGuardado.getNombres() + " " + usuarioGuardado.getApellidos())
                .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Procesando inicio de sesión para correo: {}", request.correo());

        String correoTrimmed = request.correo().trim().toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(correoTrimmed, request.contrasena())
        );

        Usuario usuario = usuarioRepository.findByCorreoAndBorradoLogicoFalse(correoTrimmed)
                .orElseThrow(() -> new ValidacionNegocioException("Usuario no encontrado o inactivo"));

        usuario.setUltimaFechaActividad(LocalDateTime.now());
        usuarioRepository.save(usuario);

        String token = jwtService.generateToken(usuario);

        return AuthResponse.builder()
                .token(token)
                .tipo(usuario.getTipoUsuario().getNombre())
                .correo(usuario.getCorreo())
                .nombreCompleto(usuario.getNombres() + " " + usuario.getApellidos())
                .build();
    }

}
