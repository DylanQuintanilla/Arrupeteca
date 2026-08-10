package com.arrupeteca.controller;

import com.arrupeteca.dto.request.FormatoRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.FormatoResponse;
import com.arrupeteca.service.FormatoService;
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
@RequestMapping("/api/v1/formatos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Formatos", description = "Endpoints para la gestión de formatos de libros")
public class FormatoController {

    private final FormatoService formatoService;

    @GetMapping
    @Operation(
        summary = "Obtener formatos de libros activos(as) para dropdowns y autocompletado",
        description = "Retorna una lista no paginada de formatos de libros activos(as), con soporte opcional para filtrado por nombre."
    )
    public ResponseEntity<ApiResponse<List<FormatoResponse>>> obtenerTodosActivos(
            @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de formatos de libros activos(as)", formatoService.obtenerTodosActivos(nombre)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Obtener tabla de administración de formatos de libros",
        description = "Retorna una página de formatos de libros con soporte para filtros por nombre y estado borradoLogico."
    )
    public ResponseEntity<ApiResponse<Page<FormatoResponse>>> obtenerTodos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean borradoLogico,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Tabla de formatos de libros", formatoService.obtenerTodos(nombre, borradoLogico, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener formatos de libros por ID",
        description = "Busca la información de un(a) formato por su ID sin importar su estado lógico."
    )
    public ResponseEntity<ApiResponse<FormatoResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Formato encontrado(a)", formatoService.obtenerPorId(id)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nuevo(a) formato",
        description = "Crea un(a) nuevo(a) formato en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<FormatoResponse>> crear(@Valid @RequestBody FormatoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Formato creado(a) exitosamente", formatoService.crear(request)));
    }

    @PutMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar formato",
        description = "Actualiza los campos de un(a) formato existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<FormatoResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody FormatoRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Formato actualizado(a) correctamente",
                formatoService.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar formato (Borrado lógico)",
        description = "Oculta el/la formato en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        formatoService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Formato desactivado(a) correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar formato",
        description = "Restaura la visibilidad pública de un(a) formato previamente desactivado(a)."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        formatoService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Formato activado(a) correctamente"));
    }}
