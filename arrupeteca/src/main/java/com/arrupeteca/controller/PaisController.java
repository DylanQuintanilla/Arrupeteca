package com.arrupeteca.controller;

import com.arrupeteca.dto.request.PaisRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.PaisResumen;
import com.arrupeteca.service.PaisService;
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
@RequestMapping("/api/v1/paises")
public class PaisController {

    private final PaisService paisService;

    // GET: Obtener todos los activos
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PaisResumen>>> obtenerTodosActivos(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        Page<PaisResumen> pagina = paisService.obtenerTodosActivos(pageable);
        return ResponseEntity.ok(ApiResponse.exito("Lista de países activos", pagina));
    }

    // GET: Obtener uno por ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaisResumen>> obtenerPorId(@PathVariable Long id) {
        PaisResumen pais = paisService.obtenerPorIdActivo(id);
        return ResponseEntity.ok(ApiResponse.exito("País encontrado", pais));
    }

    // POST: Crear nuevo
    @PostMapping
    public ResponseEntity<ApiResponse<PaisResumen>> crear(@Valid @RequestBody PaisRequest request) {
        PaisResumen nuevo = paisService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("País creado exitosamente", nuevo));
    }

    // PUT: Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PaisResumen>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PaisRequest request) {
        PaisResumen actualizado = paisService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("País actualizado correctamente", actualizado));
    }

    // DELETE: Desactivar
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        paisService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("País desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        paisService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("País activado correctamente"));
    }

    // ADMIN

    // GET: Ver TODOS (incluso borrados)
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<PaisResumen>>> obtenerTodosAdmin(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de países", paisService.obtenerTodos(pageable)));
    }
}
