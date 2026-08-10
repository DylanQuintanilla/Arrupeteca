package com.arrupeteca.controller;

import com.arrupeteca.dto.request.EdificioRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.EdificioResponse;
import com.arrupeteca.service.EdificioService;
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
@RequestMapping("/api/v1/edificios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Edificios", description = "Endpoints para la gestión de edificios")
public class EdificioController {

    private final EdificioService edificioService;

    @GetMapping
    @Operation(
        summary = "Obtener edificios activos(as) para dropdowns y autocompletado",
        description = "Retorna una lista no paginada de edificios activos(as), con soporte opcional para filtrado por nombre."
    )
    public ResponseEntity<ApiResponse<List<EdificioResponse>>> obtenerTodosActivos(
            @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de edificios activos(as)", edificioService.obtenerTodosActivos(nombre)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Obtener tabla de administración de edificios",
        description = "Retorna una página de edificios con soporte para filtros por nombre y estado borradoLogico."
    )
    public ResponseEntity<ApiResponse<Page<EdificioResponse>>> obtenerTodos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean borradoLogico,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Tabla de edificios", edificioService.obtenerTodos(nombre, borradoLogico, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener edificios por ID",
        description = "Busca la información de un(a) edificio por su ID sin importar su estado lógico."
    )
    public ResponseEntity<ApiResponse<EdificioResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Edificio encontrado(a)", edificioService.obtenerPorId(id)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nuevo(a) edificio",
        description = "Crea un(a) nuevo(a) edificio en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<EdificioResponse>> crear(@Valid @RequestBody EdificioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Edificio creado(a) exitosamente", edificioService.crear(request)));
    }

    @PutMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar edificio",
        description = "Actualiza los campos de un(a) edificio existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<EdificioResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody EdificioRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Edificio actualizado(a) correctamente",
                edificioService.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar edificio (Borrado lógico)",
        description = "Oculta el/la edificio en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        edificioService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Edificio desactivado(a) correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar edificio",
        description = "Restaura la visibilidad pública de un(a) edificio previamente desactivado(a)."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        edificioService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Edificio activado(a) correctamente"));
    }}
