package com.arrupeteca.controller;

import com.arrupeteca.dto.request.GeneroRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.GeneroResponse;
import com.arrupeteca.service.GeneroService;
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
@RequestMapping("/api/v1/generos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Géneros", description = "Endpoints para la gestión de géneros literarios")
public class GeneroController {

    private final GeneroService generoService;

    @GetMapping
    @Operation(
        summary = "Obtener géneros literarios activos(as) para dropdowns y autocompletado",
        description = "Retorna una lista no paginada de géneros literarios activos(as), con soporte opcional para filtrado por nombre."
    )
    public ResponseEntity<ApiResponse<List<GeneroResponse>>> obtenerTodosActivos(
            @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de géneros literarios activos(as)", generoService.obtenerTodosActivos(nombre)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Obtener tabla de administración de géneros literarios",
        description = "Retorna una página de géneros literarios con soporte para filtros por nombre y estado borradoLogico."
    )
    public ResponseEntity<ApiResponse<Page<GeneroResponse>>> obtenerTodos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean borradoLogico,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Tabla de géneros literarios", generoService.obtenerTodos(nombre, borradoLogico, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener géneros literarios por ID",
        description = "Busca la información de un(a) genero por su ID sin importar su estado lógico."
    )
    public ResponseEntity<ApiResponse<GeneroResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Genero encontrado(a)", generoService.obtenerPorId(id)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nuevo(a) genero",
        description = "Crea un(a) nuevo(a) genero en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<GeneroResponse>> crear(@Valid @RequestBody GeneroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Genero creado(a) exitosamente", generoService.crear(request)));
    }

    @PutMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar genero",
        description = "Actualiza los campos de un(a) genero existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<GeneroResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody GeneroRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Genero actualizado(a) correctamente",
                generoService.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar genero (Borrado lógico)",
        description = "Oculta el/la genero en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        generoService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Genero desactivado(a) correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar genero",
        description = "Restaura la visibilidad pública de un(a) genero previamente desactivado(a)."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        generoService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Genero activado(a) correctamente"));
    }}
