package com.arrupeteca.controller;

import com.arrupeteca.dto.request.EstadoFisicoRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.EstadoFisicoResponse;
import com.arrupeteca.service.EstadoFisicoService;
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
@RequestMapping("/api/v1/estados-fisicos")
@RequiredArgsConstructor
@Tag(name = "Estados Físicos", description = "Endpoints para la gestión de estados físicos de ejemplares")
public class EstadoFisicoController {

    private final EstadoFisicoService estadoFisicoService;

    @GetMapping
    @Operation(
        summary = "Obtener estados físicos activos",
        description = "Retorna una página de estados físicos registrados que no están borrados lógicamente."
    )
    public ResponseEntity<ApiResponse<Page<EstadoFisicoResponse>>> obtenerTodosActivos(
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de estados físicos activos", estadoFisicoService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener detalle de estado físico activo",
        description = "Busca la información de un estado físico activo por su ID."
    )
    public ResponseEntity<ApiResponse<EstadoFisicoResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Estado físico encontrado", estadoFisicoService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/admin")
    @Operation(
        summary = "Obtener todos los estados físicos (Admin)",
        description = "Retorna todos los estados físicos incluyendo los eliminados lógicamente (solo admin)."
    )
    public ResponseEntity<ApiResponse<Page<EstadoFisicoResponse>>> obtenerTodos(
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de estados físicos", estadoFisicoService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener estado físico por ID (Admin)",
        description = "Busca el detalle de un estado físico por su ID sin importar su estado lógico (solo admin)."
    )
    public ResponseEntity<ApiResponse<EstadoFisicoResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Estado físico encontrado (admin)", estadoFisicoService.obtenerPorId(id)));
    }

    @PostMapping
    @Operation(
        summary = "Registrar nuevo estado físico",
        description = "Crea un nuevo estado físico en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<EstadoFisicoResponse>> crear(@Valid @RequestBody EstadoFisicoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Estado físico creado exitosamente", estadoFisicoService.crear(request)));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar estado físico",
        description = "Actualiza los campos de un estado físico existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<EstadoFisicoResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody EstadoFisicoRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Estado físico actualizado correctamente",
                estadoFisicoService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar estado físico (Borrado lógico)",
        description = "Oculta el estado físico en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        estadoFisicoService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Estado físico desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar estado físico",
        description = "Restaura la visibilidad pública de un estado físico previamente desactivado."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        estadoFisicoService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Estado físico activado correctamente"));
    }
}
