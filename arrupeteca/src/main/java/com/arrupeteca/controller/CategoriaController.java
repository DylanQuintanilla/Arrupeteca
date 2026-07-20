package com.arrupeteca.controller;

import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.CategoriaResponse;
import com.arrupeteca.service.CategoriaService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Categorías", description = "Endpoints para la gestión de categorías literarias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    @Operation(
        summary = "Obtener categorías activas",
        description = "Retorna una página de categorías registradas que no están borradas lógicamente."
    )
    public ResponseEntity<ApiResponse<Page<CategoriaResponse>>> obtenerTodosActivos(
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de categorías activas", categoriaService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener detalle de categoría activa",
        description = "Busca la información de una categoría activa por su ID."
    )
    public ResponseEntity<ApiResponse<CategoriaResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Categoría encontrada", categoriaService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/admin")
    @Operation(
        summary = "Obtener todas las categorías (Admin)",
        description = "Retorna todas las categorías incluyendo las eliminadas lógicamente (solo admin)."
    )
    public ResponseEntity<ApiResponse<Page<CategoriaResponse>>> obtenerTodos(
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de categorías", categoriaService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener categoría por ID (Admin)",
        description = "Busca el detalle de una categoría por su ID sin importar su estado lógico (solo admin)."
    )
    public ResponseEntity<ApiResponse<CategoriaResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Categoría encontrada (admin)", categoriaService.obtenerPorId(id)));
    }

    @PostMapping
    @Operation(
        summary = "Registrar nueva categoría",
        description = "Crea una nueva categoría en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<CategoriaResponse>> crear(@Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Categoría creada exitosamente", categoriaService.crear(request)));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar categoría",
        description = "Actualiza los campos de una categoría existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<CategoriaResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Categoría actualizada correctamente",
                categoriaService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar categoría (Borrado lógico)",
        description = "Oculta la categoría en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        categoriaService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Categoría desactivada correctamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar categoría",
        description = "Restaura la visibilidad pública de una categoría previamente desactivada."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        categoriaService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Categoría activada correctamente"));
    }
}
