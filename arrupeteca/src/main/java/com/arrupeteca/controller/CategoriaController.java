package com.arrupeteca.controller;

import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.CategoriaResumen;
import com.arrupeteca.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    // GET: Obtener todos los activos
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CategoriaResumen>>> obtenerTodosActivos(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        Page<CategoriaResumen> pagina = categoriaService.obtenerTodosActivos(pageable);
        return ResponseEntity.ok(ApiResponse.exito("Lista de categorías activas", pagina));
    }

    // GET: Obtener uno por ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaResumen>> obtenerPorId(@PathVariable Long id) {
        CategoriaResumen categoria = categoriaService.obtenerPorIdActivo(id);
        return ResponseEntity.ok(ApiResponse.exito("Categoría encontrada", categoria));
    }

    // POST: Crear nuevo
    @PostMapping
    public ResponseEntity<ApiResponse<CategoriaResumen>> crear(@Valid @RequestBody CategoriaRequest request) {
        CategoriaResumen nuevo = categoriaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Categoría creada exitosamente", nuevo));
    }

    // PUT: Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaResumen>> actualizar(@PathVariable Long id,
                                                                    @Valid @RequestBody CategoriaRequest request) {
        CategoriaResumen actualizado = categoriaService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Categoría actualizada correctamente", actualizado));
    }

    // DELETE: Desactivar
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        categoriaService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Categoría desactivada correctamente"));
    }

    // PATCH: activar
    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        categoriaService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Categoría activada correctamente"));
    }

    // ADMIN

    // GET: Ver TODOS (incluso borrados)
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<CategoriaResumen>>> obtenerTodosAdmin(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.exito("Historial completo de categorías", categoriaService.obtenerTodos(pageable))
        );
    }
}
