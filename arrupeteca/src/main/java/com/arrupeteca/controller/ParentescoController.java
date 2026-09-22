package com.arrupeteca.controller;

import com.arrupeteca.dto.request.ParentescoRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.ParentescoResponse;
import com.arrupeteca.service.ParentescoService;
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
@RequestMapping("/api/v1/parentescos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Parentescos", description = "Endpoints para la gestión de parentescos")
public class ParentescoController {

    private final ParentescoService parentescoService;

    @GetMapping
    @Operation(
        summary = "Obtener parentescos activos(as) para dropdowns y autocompletado",
        description = "Retorna una lista no paginada de parentescos activos(as), con soporte opcional para filtrado por nombre."
    )
    public ResponseEntity<ApiResponse<List<ParentescoResponse>>> obtenerTodosActivos(
            @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de parentescos activos(as)", parentescoService.obtenerTodosActivos(nombre)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Obtener tabla de administración de parentescos",
        description = "Retorna una página de parentescos con soporte para filtros por nombre y estado borradoLogico."
    )
    public ResponseEntity<ApiResponse<Page<ParentescoResponse>>> obtenerTodos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean borradoLogico,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Tabla de parentescos", parentescoService.obtenerTodos(nombre, borradoLogico, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener parentescos por ID",
        description = "Busca la información de un(a) parentesco por su ID sin importar su estado lógico."
    )
    public ResponseEntity<ApiResponse<ParentescoResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Parentesco encontrado(a)", parentescoService.obtenerPorId(id)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nuevo(a) parentesco",
        description = "Crea un(a) nuevo(a) parentesco en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<ParentescoResponse>> crear(@Valid @RequestBody ParentescoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Parentesco creado(a) exitosamente", parentescoService.crear(request)));
    }

    @PutMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar parentesco",
        description = "Actualiza los campos de un(a) parentesco existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<ParentescoResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody ParentescoRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Parentesco actualizado(a) correctamente",
                parentescoService.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar parentesco (Borrado lógico)",
        description = "Oculta el/la parentesco en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        parentescoService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Parentesco desactivado(a) correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar parentesco",
        description = "Restaura la visibilidad pública de un(a) parentesco previamente desactivado(a)."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        parentescoService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Parentesco activado(a) correctamente"));
    }}
