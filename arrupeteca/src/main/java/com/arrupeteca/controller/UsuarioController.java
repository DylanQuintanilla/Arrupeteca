package com.arrupeteca.controller;

import com.arrupeteca.dto.request.ActualizarPerfilRequest;
import com.arrupeteca.dto.request.UsuarioAdminRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.detail.UsuarioDetalleResponse;
import com.arrupeteca.dto.response.summary.UsuarioResumen;
import com.arrupeteca.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Endpoints para la gestión de usuarios del sistema y autogestión de perfiles")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/me")
    @Operation(
        summary = "Obtener mi perfil",
        description = "Retorna el perfil del usuario autenticado actualmente basándose en su token JWT."
    )
    public ResponseEntity<ApiResponse<UsuarioDetalleResponse>> obtenerMiPerfil(Principal principal) {
        return ResponseEntity.ok(ApiResponse.exito("Perfil de usuario obtenido",
                usuarioService.obtenerPerfilActual(principal.getName())));
    }

    @PatchMapping("/me")
    @Operation(
        summary = "Actualizar mi perfil",
        description = "Permite al usuario autenticado modificar sus datos personales o contraseña de forma segura."
    )
    public ResponseEntity<ApiResponse<UsuarioDetalleResponse>> actualizarMiPerfil(
            Principal principal, @Valid @RequestBody ActualizarPerfilRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Perfil actualizado correctamente",
                usuarioService.actualizarPerfilActual(principal.getName(), request)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Búsqueda de administración de usuarios (Admin)",
        description = "Permite a los administradores listar y filtrar todos los usuarios por estado, bloqueo por mora, rol o término."
    )
    public ResponseEntity<ApiResponse<Page<UsuarioResumen>>> busquedaAdmin(
            @RequestParam(required = false) Boolean borradoLogico,
            @RequestParam(required = false) Boolean bloqueadoPorMora,
            @RequestParam(required = false) Long idTipoUsuario,
            @RequestParam(required = false) String termino,
            @ParameterObject @PageableDefault(size = 20, sort = "nombres") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados de usuarios",
                usuarioService.busquedaAdmin(borradoLogico, bloqueadoPorMora, idTipoUsuario, termino, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener usuario por ID (Admin)",
        description = "Retorna el detalle completo de cualquier usuario en el sistema."
    )
    public ResponseEntity<ApiResponse<UsuarioDetalleResponse>> busquedaAdminPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Usuario encontrado", usuarioService.busquedaAdminPorId(id)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Crear nuevo usuario (Admin)",
        description = "Registra un nuevo usuario asignándole su rol correspondiente en el sistema."
    )
    public ResponseEntity<ApiResponse<UsuarioDetalleResponse>> crearAdmin(@Valid @RequestBody UsuarioAdminRequest request) {
        return new ResponseEntity<>(ApiResponse.exito("Usuario creado exitosamente",
                usuarioService.crearAdmin(request)), HttpStatus.CREATED);
    }

    @PatchMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar usuario por ID (Admin)",
        description = "Permite al administrador modificar los datos, correo o rol de cualquier usuario."
    )
    public ResponseEntity<ApiResponse<UsuarioDetalleResponse>> actualizarAdmin(
            @PathVariable Long id, @Valid @RequestBody UsuarioAdminRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Usuario actualizado correctamente",
                usuarioService.actualizarAdmin(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar usuario (Admin)",
        description = "Desactiva lógicamente a un usuario, impidiéndole acceder al sistema."
    )
    public ResponseEntity<ApiResponse<Void>> desactivarAdmin(@PathVariable Long id) {
        usuarioService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Usuario desactivado correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar usuario (Admin)",
        description = "Reactiva a un usuario previamente desactivado."
    )
    public ResponseEntity<ApiResponse<Void>> activarAdmin(@PathVariable Long id) {
        usuarioService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Usuario activado correctamente"));
    }

    @PatchMapping("/admin/{id}/bloquear-mora")
    @Operation(
        summary = "Cambiar bloqueo por mora (Admin)",
        description = "Bloquea o desbloquea a un usuario para la realización de nuevos préstamos."
    )
    public ResponseEntity<ApiResponse<Void>> cambiarBloqueoMora(
            @PathVariable Long id, @RequestParam boolean bloqueado) {
        usuarioService.cambiarBloqueoMora(id, bloqueado);
        String mensaje = bloqueado ? "Usuario bloqueado por mora" : "Bloqueo por mora removido";
        return ResponseEntity.ok(ApiResponse.exito(mensaje));
    }
}
