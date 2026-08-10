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
@RequestMapping("/api/v1/disponibilidades")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Disponibilidades", description = "Endpoints para la gestión de estados de disponibilidad de ejemplares")
public class DisponibilidadController {

    private final DisponibilidadService disponibilidadService;

    @GetMapping
    @Operation(
        summary = "Obtener estados de disponibilidad de ejemplares activos(as) para dropdowns y autocompletado",
        description = "Retorna una lista no paginada de estados de disponibilidad de ejemplares activos(as), con soporte opcional para filtrado por nombre."
    )
    public ResponseEntity<ApiResponse<List<DisponibilidadResponse>>> obtenerTodosActivos(
            @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de estados de disponibilidad de ejemplares activos(as)", disponibilidadService.obtenerTodosActivos(nombre)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Obtener tabla de administración de estados de disponibilidad de ejemplares",
        description = "Retorna una página de estados de disponibilidad de ejemplares con soporte para filtros por nombre y estado borradoLogico."
    )
    public ResponseEntity<ApiResponse<Page<DisponibilidadResponse>>> obtenerTodos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean borradoLogico,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Tabla de estados de disponibilidad de ejemplares", disponibilidadService.obtenerTodos(nombre, borradoLogico, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener estados de disponibilidad de ejemplares por ID",
        description = "Busca la información de un(a) disponibilidad por su ID sin importar su estado lógico."
    )
    public ResponseEntity<ApiResponse<DisponibilidadResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Disponibilidad encontrado(a)", disponibilidadService.obtenerPorId(id)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nuevo(a) disponibilidad",
        description = "Crea un(a) nuevo(a) disponibilidad en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<DisponibilidadResponse>> crear(@Valid @RequestBody DisponibilidadRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Disponibilidad creado(a) exitosamente", disponibilidadService.crear(request)));
    }

    @PutMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar disponibilidad",
        description = "Actualiza los campos de un(a) disponibilidad existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<DisponibilidadResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody DisponibilidadRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Disponibilidad actualizado(a) correctamente",
                disponibilidadService.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar disponibilidad (Borrado lógico)",
        description = "Oculta el/la disponibilidad en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        disponibilidadService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Disponibilidad desactivado(a) correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar disponibilidad",
        description = "Restaura la visibilidad pública de un(a) disponibilidad previamente desactivado(a)."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        disponibilidadService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Disponibilidad activado(a) correctamente"));
    }}
