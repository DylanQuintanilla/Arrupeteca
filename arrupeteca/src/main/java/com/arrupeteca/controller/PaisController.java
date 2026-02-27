package com.arrupeteca.controller;

import com.arrupeteca.dto.request.PaisRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.PaisResumen;
import com.arrupeteca.service.PaisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/paises")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaisController {

    private final PaisService paisService;

    // GET: Obtener todos los activos
    @GetMapping
    public ResponseEntity<ApiResponse<List<PaisResumen>>> obtenerTodosActivos() {
        List<PaisResumen> lista = paisService.obtenerTodosActivos();
        return ResponseEntity.ok(ApiResponse.exito("Lista de paises activos", lista));
    }

    // GET: Obtener uno por ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaisResumen>> obtenerPorId(@PathVariable Long id) {
        PaisResumen Pais = paisService.obtenerPorIdActivo(id);
        return ResponseEntity.ok(ApiResponse.exito("Pais encontrado", Pais));
    }

    // POST: Crear nuevo
    @PostMapping
    public ResponseEntity<ApiResponse<PaisResumen>> crear(@Valid @RequestBody PaisRequest request) {
        PaisResumen nuevo = paisService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Pais creado exitosamente", nuevo));
    }

    // PUT: Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PaisResumen>> actualizar(@PathVariable Long id,
                                                                   @Valid @RequestBody PaisRequest request) {
        PaisResumen actualizado = paisService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Pais actualizado correctamente", actualizado));
    }

    // DELETE: Desactivar
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        paisService.cambiarEstadoLogico(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Pais desactivado correctamente"));
    }

    // PATCH: activar
    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        paisService.cambiarEstadoLogico(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Pais activado correctamente"));
    }

    // ADMIN

    // GET: Ver TODOS (incluso borrados)
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<PaisResumen>>> obtenerTodosAdmin() {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de paises", paisService.obtenerTodos()));
    }
}