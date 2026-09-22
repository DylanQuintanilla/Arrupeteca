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
@RequestMapping("/api/v1/estados-fisicos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Estados Físicos", description = "Endpoints para la gestión de estados físicos de ejemplares")
public class EstadoFisicoController {

    private final EstadoFisicoService estadoFisicoService;

    @GetMapping
    @Operation(
        summary = "Obtener estados físicos de ejemplares activos(as) para dropdowns y autocompletado",
        description = "Retorna una lista no paginada de estados físicos de ejemplares activos(as), con soporte opcional para filtrado por nombre."
    )
    public ResponseEntity<ApiResponse<List<EstadoFisicoResponse>>> obtenerTodosActivos(
            @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de estados físicos de ejemplares activos(as)", estadoFisicoService.obtenerTodosActivos(nombre)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Obtener tabla de administración de estados físicos de ejemplares",
        description = "Retorna una página de estados físicos de ejemplares con soporte para filtros por nombre y estado borradoLogico."
    )
    public ResponseEntity<ApiResponse<Page<EstadoFisicoResponse>>> obtenerTodos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean borradoLogico,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Tabla de estados físicos de ejemplares", estadoFisicoService.obtenerTodos(nombre, borradoLogico, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener estados físicos de ejemplares por ID",
        description = "Busca la información de un(a) estadofisico por su ID sin importar su estado lógico."
    )
    public ResponseEntity<ApiResponse<EstadoFisicoResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("EstadoFisico encontrado(a)", estadoFisicoService.obtenerPorId(id)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nuevo(a) estadofisico",
        description = "Crea un(a) nuevo(a) estadofisico en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<EstadoFisicoResponse>> crear(@Valid @RequestBody EstadoFisicoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("EstadoFisico creado(a) exitosamente", estadoFisicoService.crear(request)));
    }

    @PutMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar estadofisico",
        description = "Actualiza los campos de un(a) estadofisico existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<EstadoFisicoResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody EstadoFisicoRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("EstadoFisico actualizado(a) correctamente",
                estadoFisicoService.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar estadofisico (Borrado lógico)",
        description = "Oculta el/la estadofisico en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        estadoFisicoService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("EstadoFisico desactivado(a) correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar estadofisico",
        description = "Restaura la visibilidad pública de un(a) estadofisico previamente desactivado(a)."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        estadoFisicoService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("EstadoFisico activado(a) correctamente"));
    }}
