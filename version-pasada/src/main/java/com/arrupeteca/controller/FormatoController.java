package com.arrupeteca.controller;

import com.arrupeteca.dto.request.FormatoRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.FormatoResumen;
import com.arrupeteca.service.FormatoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/formatos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FormatoController {

    private final FormatoService formatoService;

    // GET: Obtener todos los activos
    @GetMapping
    public ResponseEntity<ApiResponse<List<FormatoResumen>>> obtenerTodosActivos() {
        List<FormatoResumen> lista = formatoService.obtenerTodosActivos();
        return ResponseEntity.ok(ApiResponse.exito("Lista de formatos activos", lista));
    }

    // GET: Obtener uno por ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FormatoResumen>> obtenerPorId(@PathVariable Long id) {
        FormatoResumen Formato = formatoService.obtenerPorIdActivo(id);
        return ResponseEntity.ok(ApiResponse.exito("Formato encontrado", Formato));
    }

    // POST: Crear nuevo
    @PostMapping
    public ResponseEntity<ApiResponse<FormatoResumen>> crear(@Valid @RequestBody FormatoRequest request) {
        FormatoResumen nuevo = formatoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Formato creado exitosamente", nuevo));
    }

    // PUT: Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FormatoResumen>> actualizar(@PathVariable Long id,
                                                                 @Valid @RequestBody FormatoRequest request) {
        FormatoResumen actualizado = formatoService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Formato actualizado correctamente", actualizado));
    }

    // DELETE: Desactivar
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        formatoService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Formato desactivado correctamente"));
    }

    // PATCH: activar
    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        formatoService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Formato activado correctamente"));
    }

    // ADMIN

    // GET: Ver TODOS (incluso borrados)
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<FormatoResumen>>> obtenerTodosAdmin() {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de formatos", formatoService.obtenerTodos()));
    }
}