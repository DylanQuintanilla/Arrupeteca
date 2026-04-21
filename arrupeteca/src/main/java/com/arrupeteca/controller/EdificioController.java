package com.arrupeteca.controller;

import com.arrupeteca.dto.request.EdificioRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.EdificioResumen;
import com.arrupeteca.service.EdificioService;
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
@RequestMapping("/api/v1/edificios")
public class EdificioController {

    private final EdificioService edificioService;

    // GET: Obtener todos los activos
    @GetMapping
    public ResponseEntity<ApiResponse<Page<EdificioResumen>>> obtenerTodosActivos(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        Page<EdificioResumen> pagina = edificioService.obtenerTodosActivos(pageable);
        return ResponseEntity.ok(ApiResponse.exito("Lista de edificios activos", pagina));
    }

    // GET: Obtener uno por ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EdificioResumen>> obtenerPorId(@PathVariable Long id) {
        EdificioResumen edificio = edificioService.obtenerPorIdActivo(id);
        return ResponseEntity.ok(ApiResponse.exito("Edificio encontrado", edificio));
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
    public ResponseEntity<ApiResponse<Page<EdificioResumen>>> obtenerTodosAdmin(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.exito("Historial completo de edificios", edificioService.obtenerTodos(pageable))
        );
    }
}
