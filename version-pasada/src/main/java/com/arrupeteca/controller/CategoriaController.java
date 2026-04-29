package com.arrupeteca.controller;

import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.detail.CategoriaDetalleResponse;
import com.arrupeteca.dto.response.summary.CategoriaResumen;
import com.arrupeteca.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CategoriaResumen>>> obtenerTodosActivos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de categorías activas", categoriaService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaDetalleResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Categoría encontrada", categoriaService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<CategoriaResumen>>> obtenerTodos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de categorías", categoriaService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<CategoriaDetalleResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Categoría encontrada (admin)", categoriaService.obtenerPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoriaDetalleResponse>> crear(@Valid @RequestBody CategoriaRequest request) {
        return new ResponseEntity<>(ApiResponse.exito("Categoría creada exitosamente",
                categoriaService.crear(request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaDetalleResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Categoría actualizada correctamente",
                categoriaService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        categoriaService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Categoría desactivada correctamente"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        categoriaService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Categoría activada correctamente"));
    }
}
