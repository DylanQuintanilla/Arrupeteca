package com.arrupeteca.controller;

import com.arrupeteca.dto.request.DisponibilidadRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.DisponibilidadResponse;
import com.arrupeteca.service.DisponibilidadService;
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
@RequestMapping("/api/v1/disponibilidades")
@RequiredArgsConstructor
@Tag(name = "Disponibilidades", description = "Endpoints para la gestión de estados de disponibilidad")
public class DisponibilidadController {

    private final DisponibilidadService disponibilidadService;

    @GetMapping
    @Operation(
        summary = "Obtener disponibilidades activas",
        description = "Retorna una página de disponibilidades registradas que no están borradas lógicamente."
    )
    public ResponseEntity<ApiResponse<Page<DisponibilidadResponse>>> obtenerTodosActivos(
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de disponibilidades activas", disponibilidadService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener detalle de disponibilidad activa",
        description = "Busca la información de una disponibilidad activa por su ID."
    )
    public ResponseEntity<ApiResponse<DisponibilidadResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Disponibilidad encontrada", disponibilidadService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/admin")
    @Operation(
        summary = "Obtener todas las disponibilidades (Admin)",
        description = "Retorna todas las disponibilidades incluyendo las eliminadas lógicamente (solo admin)."
    )
    public ResponseEntity<ApiResponse<Page<DisponibilidadResponse>>> obtenerTodos(
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de disponibilidades", disponibilidadService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener disponibilidad por ID (Admin)",
        description = "Busca el detalle de una disponibilidad por su ID sin importar su estado lógico (solo admin)."
    )
    public ResponseEntity<ApiResponse<DisponibilidadResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Disponibilidad encontrada (admin)", disponibilidadService.obtenerPorId(id)));
    }

    @PostMapping
    @Operation(
        summary = "Registrar nueva disponibilidad",
        description = "Crea una nueva disponibilidad en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<DisponibilidadResponse>> crear(@Valid @RequestBody DisponibilidadRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Disponibilidad creada exitosamente", disponibilidadService.crear(request)));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar disponibilidad",
        description = "Actualiza los campos de una disponibilidad existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<DisponibilidadResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody DisponibilidadRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Disponibilidad actualizada correctamente",
                disponibilidadService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar disponibilidad (Borrado lógico)",
        description = "Oculta la disponibilidad en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        disponibilidadService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Disponibilidad desactivada correctamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar disponibilidad",
        description = "Restaura la visibilidad pública de una disponibilidad previamente desactivada."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        disponibilidadService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Disponibilidad activada correctamente"));
    }
}
