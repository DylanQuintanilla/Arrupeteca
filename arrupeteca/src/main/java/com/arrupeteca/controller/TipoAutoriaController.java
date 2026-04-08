package com.arrupeteca.controller;

import com.arrupeteca.dto.request.TipoAutoriaRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.TipoAutoriaResumen;
import com.arrupeteca.service.TipoAutoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/tipos-autoria")
public class TipoAutoriaController {

    private final TipoAutoriaService tipoAutoriaService;

    // GET: Obtener todos los activos
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TipoAutoriaResumen>>> obtenerTodosActivos(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        Page<TipoAutoriaResumen> pagina = tipoAutoriaService.obtenerTodosActivos(pageable);
        return ResponseEntity.ok(ApiResponse.exito("Lista de tipos de autoría activos", pagina));
    }

    // GET: Obtener uno por ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TipoAutoriaResumen>> obtenerPorId(@PathVariable Long id) {
        TipoAutoriaResumen tipo = tipoAutoriaService.obtenerPorIdActivo(id);
        return ResponseEntity.ok(ApiResponse.exito("Tipo de autoría encontrado", tipo));
    }

    // POST: Crear nuevo
    @PostMapping
    public ResponseEntity<ApiResponse<TipoAutoriaResumen>> crear(@Valid @RequestBody TipoAutoriaRequest request) {
        TipoAutoriaResumen nuevo = tipoAutoriaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Tipo de autoría creado exitosamente", nuevo));
    }

    // PUT: Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TipoAutoriaResumen>> actualizar(@PathVariable Long id,
                                                                      @Valid @RequestBody TipoAutoriaRequest request) {
        TipoAutoriaResumen actualizado = tipoAutoriaService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Tipo de autoría actualizado correctamente", actualizado));
    }

    // DELETE: Desactivar
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        tipoAutoriaService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Tipo de autoría desactivado correctamente"));
    }

    // PATCH: activar
    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        tipoAutoriaService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Tipo de autoría activado correctamente"));
    }

    // ADMIN

    // GET: Ver TODOS (incluso borrados)
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<TipoAutoriaResumen>>> obtenerTodosAdmin(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de tipos de autoría", tipoAutoriaService.obtenerTodos(pageable)));
    }
}
