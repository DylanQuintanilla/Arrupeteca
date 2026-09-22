package com.arrupeteca.controller;

import com.arrupeteca.dto.request.TipoUsuarioRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.TipoUsuarioResponse;
import com.arrupeteca.service.TipoUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tipos-usuario")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Tipos de Usuario", description = "Endpoints para la gestión de tipos de usuario")
public class TipoUsuarioController {

    private final TipoUsuarioService tipoUsuarioService;

    @GetMapping
    @Operation(
        summary = "Obtener tipos de usuario activos(as) para dropdowns y autocompletado",
        description = "Retorna una lista no paginada de tipos de usuario activos(as), con soporte opcional para filtrado por nombre."
    )
    public ResponseEntity<ApiResponse<List<TipoUsuarioResponse>>> obtenerTodosActivos(
            @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de tipos de usuario activos(as)", tipoUsuarioService.obtenerTodosActivos(nombre)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Obtener tabla de administración de tipos de usuario",
        description = "Retorna una página de tipos de usuario con soporte para filtros por nombre y estado borradoLogico."
    )
    public ResponseEntity<ApiResponse<Page<TipoUsuarioResponse>>> obtenerTodos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean borradoLogico,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Tabla de tipos de usuario", tipoUsuarioService.obtenerTodos(nombre, borradoLogico, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener tipos de usuario por ID",
        description = "Busca la información de un(a) tipousuario por su ID sin importar su estado lógico."
    )
    public ResponseEntity<ApiResponse<TipoUsuarioResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("TipoUsuario encontrado(a)", tipoUsuarioService.obtenerPorId(id)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nuevo(a) tipousuario",
        description = "Crea un(a) nuevo(a) tipousuario en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<TipoUsuarioResponse>> crear(@Valid @RequestBody TipoUsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("TipoUsuario creado(a) exitosamente", tipoUsuarioService.crear(request)));
    }

    @PutMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar tipousuario",
        description = "Actualiza los campos de un(a) tipousuario existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<TipoUsuarioResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody TipoUsuarioRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("TipoUsuario actualizado(a) correctamente",
                tipoUsuarioService.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar tipousuario (Borrado lógico)",
        description = "Oculta el/la tipousuario en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        tipoUsuarioService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("TipoUsuario desactivado(a) correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar tipousuario",
        description = "Restaura la visibilidad pública de un(a) tipousuario previamente desactivado(a)."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        tipoUsuarioService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("TipoUsuario activado(a) correctamente"));
    }}
