package com.arrupeteca.controller;

import com.arrupeteca.dto.request.TipoAutoriaRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.TipoAutoriaResumen;
import com.arrupeteca.service.TipoAutoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tipoAutorias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TipoAutoriaController {

    private final TipoAutoriaService tipoAutoriaService;

    // GET: Obtener todos los activos
    @GetMapping
    public ResponseEntity<ApiResponse<List<TipoAutoriaResumen>>> obtenerTodosActivos() {
        List<TipoAutoriaResumen> lista = tipoAutoriaService.obtenerTodosActivos();
        return ResponseEntity.ok(ApiResponse.exito("Lista de tipo de autorias activos", lista));
    }

    // GET: Obtener uno por ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TipoAutoriaResumen>> obtenerPorId(@PathVariable Long id) {
        TipoAutoriaResumen TipoAutoria = tipoAutoriaService.obtenerPorIdActivo(id);
        return ResponseEntity.ok(ApiResponse.exito("Tipo autoria encontrado", TipoAutoria));
    }

    // POST: Crear nuevo
    @PostMapping
    public ResponseEntity<ApiResponse<TipoAutoriaResumen>> crear(@Valid @RequestBody TipoAutoriaRequest request) {
        TipoAutoriaResumen nuevo = tipoAutoriaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Tipo autoria creado exitosamente", nuevo));
    }

    // PUT: Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TipoAutoriaResumen>> actualizar(@PathVariable Long id,
                                                               @Valid @RequestBody TipoAutoriaRequest request) {
        TipoAutoriaResumen actualizado = tipoAutoriaService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Tipo autoria actualizado correctamente", actualizado));
    }

    // DELETE: Desactivar
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        tipoAutoriaService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Tipo autoria desactivado correctamente"));
    }

    // PATCH: activar
    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        tipoAutoriaService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Tipo autoria activado correctamente"));
    }

    // ADMIN

    // GET: Ver TODOS (incluso borrados)
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<TipoAutoriaResumen>>> obtenerTodosAdmin() {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de Tipos de autorias", tipoAutoriaService.obtenerTodos()));
    }
}
