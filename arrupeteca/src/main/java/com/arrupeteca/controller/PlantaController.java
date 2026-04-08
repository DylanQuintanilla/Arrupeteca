package com.arrupeteca.controller;

import com.arrupeteca.dto.request.PlantaRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.PlantaResumen;
import com.arrupeteca.service.PlantaService;
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
@RequestMapping("/api/v1/plantas")
public class PlantaController {

    private final PlantaService plantaService;

    // GET: Obtener todos los activos
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PlantaResumen>>> obtenerTodosActivos(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        Page<PlantaResumen> pagina = plantaService.obtenerTodosActivos(pageable);
        return ResponseEntity.ok(ApiResponse.exito("Lista de plantas activas", pagina));
    }

    // GET: Obtener uno por ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlantaResumen>> obtenerPorId(@PathVariable Long id) {
        PlantaResumen planta = plantaService.obtenerPorIdActivo(id);
        return ResponseEntity.ok(ApiResponse.exito("Planta encontrada", planta));
    }

    // GET: Obtener por edificio
    @GetMapping("/edificio/{idEdificio}")
    public ResponseEntity<ApiResponse<Page<PlantaResumen>>> obtenerPorEdificio(
            @PathVariable Long idEdificio,
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        Page<PlantaResumen> pagina = plantaService.obtenerPorIdEdificio(idEdificio, pageable);
        return ResponseEntity.ok(ApiResponse.exito("Plantas del edificio", pagina));
    }

    // POST: Crear nuevo
    @PostMapping
    public ResponseEntity<ApiResponse<PlantaResumen>> crear(@Valid @RequestBody PlantaRequest request) {
        PlantaResumen nuevo = plantaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Planta creada exitosamente", nuevo));
    }

    // PUT: Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PlantaResumen>> actualizar(@PathVariable Long id,
                                                                 @Valid @RequestBody PlantaRequest request) {
        PlantaResumen actualizado = plantaService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Planta actualizada correctamente", actualizado));
    }

    // DELETE: Desactivar
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        plantaService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Planta desactivada correctamente"));
    }

    // PATCH: activar
    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        plantaService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Planta activada correctamente"));
    }

    // ADMIN

    // GET: Ver TODOS (incluso borrados)
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<PlantaResumen>>> obtenerTodosAdmin(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de plantas", plantaService.obtenerTodos(pageable)));
    }
}
