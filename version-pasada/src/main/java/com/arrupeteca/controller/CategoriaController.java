package com.arrupeteca.controller;

import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.CategoriaResumen;
import com.arrupeteca.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    // GET: Obtener todos los activos
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoriaResumen>>> obtenerTodosActivos() {
        List<CategoriaResumen> lista = categoriaService.obtenerTodosActivos();
        return ResponseEntity.ok(ApiResponse.exito("Lista de Categoria activas", lista));
    }

    // GET: Obtener uno por ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaResumen>> obtenerPorId(@PathVariable Long id) {
        CategoriaResumen Categoria = categoriaService.obtenerPorIdActivo(id);
        return ResponseEntity.ok(ApiResponse.exito("Categoria encontrada", Categoria));
    }

    // POST: Crear nuevo
    @PostMapping
    public ResponseEntity<ApiResponse<CategoriaResumen>> crear(@Valid @RequestBody CategoriaRequest request) {
        CategoriaResumen nuevo = categoriaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Categoria creada exitosamente", nuevo));
    }

    // PUT: Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaResumen>> actualizar(@PathVariable Long id,
                                                                 @Valid @RequestBody CategoriaRequest request) {
        CategoriaResumen actualizado = categoriaService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Categoria actualizada correctamente", actualizado));
    }

    // DELETE: Desactivar
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        categoriaService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Categoria desactivada correctamente"));
    }

    // PATCH: activar
    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        categoriaService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Categoria activada correctamente"));
    }

    // ADMIN

    // GET: Ver TODOS (incluso borrados)
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<CategoriaResumen>>> obtenerTodosAdmin() {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de Categorias", categoriaService.obtenerTodos()));
    }


}
