package com.arrupeteca.prestamo.controller;

import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.prestamo.dto.request.EstadoEjemplarPrestamoRequest;
import com.arrupeteca.prestamo.dto.response.EstadoEjemplarPrestamoResponse;
import com.arrupeteca.prestamo.internal.service.EstadoEjemplarPrestamoService;
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
@RequestMapping("/api/v1/estados-ejemplar-prestamo")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Estados de Ejemplar de Préstamo", description = "Endpoints para la gestión del catálogo de estados por ejemplar prestado")
public class EstadoEjemplarPrestamoController {

    private final EstadoEjemplarPrestamoService estadoEjemplarPrestamoService;

    @GetMapping
    @Operation(
        summary = "Obtener estados de ejemplar de préstamo activos para dropdowns y autocompletado",
        description = "Retorna una lista no paginada de estados de ejemplar de préstamo activos, con soporte opcional para filtrado por nombre."
    )
    public ResponseEntity<ApiResponse<List<EstadoEjemplarPrestamoResponse>>> obtenerTodosActivos(
            @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de estados de ejemplar de préstamo activos", estadoEjemplarPrestamoService.obtenerTodosActivos(nombre)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Obtener tabla de administración de estados de ejemplar de préstamo",
        description = "Retorna una página de estados de ejemplar de préstamo con soporte para filtros por nombre y estado borradoLogico."
    )
    public ResponseEntity<ApiResponse<Page<EstadoEjemplarPrestamoResponse>>> obtenerTodos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean borradoLogico,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Tabla de estados de ejemplar de préstamo", estadoEjemplarPrestamoService.obtenerTodos(nombre, borradoLogico, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener estado de ejemplar de préstamo por ID",
        description = "Busca la información de un estado de ejemplar de préstamo por su ID sin importar su estado lógico."
    )
    public ResponseEntity<ApiResponse<EstadoEjemplarPrestamoResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Estado de ejemplar de préstamo encontrado", estadoEjemplarPrestamoService.obtenerPorId(id)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nuevo estado de ejemplar de préstamo",
        description = "Crea un nuevo estado de ejemplar de préstamo en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<EstadoEjemplarPrestamoResponse>> crear(@Valid @RequestBody EstadoEjemplarPrestamoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Estado de ejemplar de préstamo creado exitosamente", estadoEjemplarPrestamoService.crear(request)));
    }

    @PutMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar estado de ejemplar de préstamo",
        description = "Actualiza los campos de un estado de ejemplar de préstamo existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<EstadoEjemplarPrestamoResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody EstadoEjemplarPrestamoRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Estado de ejemplar de préstamo actualizado correctamente",
                estadoEjemplarPrestamoService.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar estado de ejemplar de préstamo (Borrado lógico)",
        description = "Oculta el estado de ejemplar de préstamo en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        estadoEjemplarPrestamoService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Estado de ejemplar de préstamo desactivado correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar estado de ejemplar de préstamo",
        description = "Restaura la visibilidad pública de un estado de ejemplar de préstamo previamente desactivado."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        estadoEjemplarPrestamoService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Estado de ejemplar de préstamo activado correctamente"));
    }
}

