package com.arrupeteca.controller;

import com.arrupeteca.dto.request.EdificioRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.EdificioResumen;
import com.arrupeteca.service.EdificioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/edificios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EdificioController {

    private final EdificioService edificioService;

    // GET: Obtener todos los activos
    @GetMapping
    public ResponseEntity<ApiResponse<List<EdificioResumen>>> obtenerTodosActivos() {
        List<EdificioResumen> lista = edificioService.obtenerTodosActivos();
        return ResponseEntity.ok(ApiResponse.exito("Lista de edificios activos", lista));
    }

    // GET: Obtener uno por ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EdificioResumen>> obtenerPorId(@PathVariable Long id) {
        EdificioResumen Edificio = edificioService.obtenerPorIdActivo(id);
        return ResponseEntity.ok(ApiResponse.exito("Edificio encontrado", Edificio));
    }

    // POST: Crear nuevo
    @PostMapping
    public ResponseEntity<ApiResponse<EdificioResumen>> crear(@Valid @RequestBody EdificioRequest request) {
        EdificioResumen nuevo = edificioService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Edificio creado exitosamente", nuevo));
    }

    // PUT: Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EdificioResumen>> actualizar(@PathVariable Long id,
                                                                 @Valid @RequestBody EdificioRequest request) {
        EdificioResumen actualizado = edificioService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Edificio actualizado correctamente", actualizado));
    }

    // DELETE: Desactivar
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        edificioService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Edificio desactivado correctamente"));
    }

    // PATCH: activar
    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        edificioService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Edificio activado correctamente"));
    }

    // ADMIN

    // GET: Ver TODOS (incluso borrados)
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<EdificioResumen>>> obtenerTodosAdmin() {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de edificios", edificioService.obtenerTodos()));
    }
}
