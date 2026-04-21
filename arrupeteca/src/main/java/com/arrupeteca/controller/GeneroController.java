package com.arrupeteca.controller;

import com.arrupeteca.dto.request.GeneroRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.GeneroResumen;
import com.arrupeteca.service.GeneroService;
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
@RequestMapping("/api/v1/generos")
public class GeneroController {

    private final GeneroService generoService;

    // GET: Obtener todos los activos
    @GetMapping
    public ResponseEntity<ApiResponse<Page<GeneroResumen>>> obtenerTodosActivos(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        Page<GeneroResumen> pagina = generoService.obtenerTodosActivos(pageable);
        return ResponseEntity.ok(ApiResponse.exito("Lista de géneros activos", pagina));
    }

    // GET: Obtener uno por ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GeneroResumen>> obtenerPorId(@PathVariable Long id) {
        GeneroResumen genero = generoService.obtenerPorIdActivo(id);
        return ResponseEntity.ok(ApiResponse.exito("Género encontrado", genero));
    }

    // POST: Crear nuevo
    @PostMapping
    public ResponseEntity<ApiResponse<GeneroResumen>> crear(@Valid @RequestBody GeneroRequest request) {
        GeneroResumen nuevo = generoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Género creado exitosamente", nuevo));
    }

    // PUT: Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GeneroResumen>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody GeneroRequest request) {
        GeneroResumen actualizado = generoService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Género actualizado correctamente", actualizado));
    }

    // DELETE: Desactivar
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        generoService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Género desactivado correctamente"));
    }

    // PATCH: activar
    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        generoService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Género activado correctamente"));
    }

    // ADMIN

    // GET: Ver TODOS (incluso borrados)
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<GeneroResumen>>> obtenerTodosAdmin(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.exito("Historial completo de géneros", generoService.obtenerTodos(pageable))
        );
    }
}
