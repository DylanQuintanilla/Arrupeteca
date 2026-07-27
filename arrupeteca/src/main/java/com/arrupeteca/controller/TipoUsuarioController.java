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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tipos-usuario")
@RequiredArgsConstructor
@Tag(name = "Tipos de Usuario", description = "Endpoints para la gestión del catálogo de tipos de usuario")
public class TipoUsuarioController {

    private final TipoUsuarioService tipoUsuarioService;

    @GetMapping
    @Operation(
        summary = "Obtener tipos de usuario activos",
        description = "Retorna una página de tipos de usuario registrados que no están borrados lógicamente."
    )
    public ResponseEntity<ApiResponse<Page<TipoUsuarioResponse>>> obtenerTodosActivos(
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de tipos de usuario activos", tipoUsuarioService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener detalle de tipo de usuario activo",
        description = "Busca la información de un tipo de usuario activo por su ID."
    )
    public ResponseEntity<ApiResponse<TipoUsuarioResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Tipo de usuario encontrado", tipoUsuarioService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/admin")
    @Operation(
        summary = "Obtener todos los tipos de usuario (Admin)",
        description = "Retorna todos los tipos de usuario incluyendo los eliminados lógicamente."
    )
    public ResponseEntity<ApiResponse<Page<TipoUsuarioResponse>>> obtenerTodos(
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de tipos de usuario", tipoUsuarioService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener tipo de usuario por ID (Admin)",
        description = "Busca el detalle de un tipo de usuario por su ID sin importar su estado lógico."
    )
    public ResponseEntity<ApiResponse<TipoUsuarioResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Tipo de usuario encontrado (admin)", tipoUsuarioService.obtenerPorId(id)));
    }

    @PostMapping
    @Operation(
        summary = "Registrar nuevo tipo de usuario",
        description = "Crea un nuevo tipo de usuario validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<TipoUsuarioResponse>> crear(@Valid @RequestBody TipoUsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Tipo de usuario creado exitosamente", tipoUsuarioService.crear(request)));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar tipo de usuario",
        description = "Actualiza los campos de un tipo de usuario existente."
    )
    public ResponseEntity<ApiResponse<TipoUsuarioResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody TipoUsuarioRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Tipo de usuario actualizado correctamente",
                tipoUsuarioService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar tipo de usuario (Borrado lógico)",
        description = "Desactiva el tipo de usuario cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        tipoUsuarioService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Tipo de usuario desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar tipo de usuario",
        description = "Restaura un tipo de usuario previamente desactivado."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        tipoUsuarioService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Tipo de usuario activado correctamente"));
    }

}
