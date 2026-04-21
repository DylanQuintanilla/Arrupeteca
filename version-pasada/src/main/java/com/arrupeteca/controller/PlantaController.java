package com.arrupeteca.controller;

import com.arrupeteca.dto.request.PlantaRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.PlantaResumen;
import com.arrupeteca.service.PlantaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/plantas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlantaController {

    private final PlantaService plantaService;


    // GET: Obtener todas
    @GetMapping
    public ResponseEntity<ApiResponse<List<PlantaResumen>>> obtenerTodosActivos() {
        List<PlantaResumen> lista = plantaService.obtenerTodosActivos();
        return ResponseEntity.ok(ApiResponse.exito("Lista de plantas activas", lista));
    }

    // GET: Obtener  por ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlantaResumen>> obtenerPorId(@PathVariable Long id) {
        PlantaResumen planta = plantaService.obtenerPorIdActivo(id);
        return ResponseEntity.ok(ApiResponse.exito("Planta encontrada", planta));
    }

    // GET: Obtener plantas filtradas por Edificio
    @GetMapping("/por-edificio/{idEdificio}")
    public ResponseEntity<ApiResponse<List<PlantaResumen>>> obtenerPorEdificio(@PathVariable Long idEdificio) {
        List<PlantaResumen> lista = plantaService.obtenerPorIdEdificio(idEdificio);
        return ResponseEntity.ok(ApiResponse.exito("Plantas del edificio seleccionado", lista));
    }

    // POST: Crear
    @PostMapping
    public ResponseEntity<ApiResponse<PlantaResumen>> crear(@Valid @RequestBody PlantaRequest request) {
        PlantaResumen nueva = plantaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Planta creada exitosamente", nueva));
    }

    // PUT: Actualizar planta
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PlantaResumen>> actualizar(@PathVariable Long id,
                                                                 @Valid @RequestBody PlantaRequest request) {
        PlantaResumen actualizada = plantaService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Planta actualizada correctamente", actualizada));
    }

    // DELETE: Desactivar
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        plantaService.cambiarBorradoLogico(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Planta desactivada correctamente"));
    }

    // PATCH: Reactivar
    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        plantaService.cambiarBorradoLogico(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Planta activada correctamente"));
    }


    // GET: Ver TODAS (activas e inactivas)
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<PlantaResumen>>> obtenerTodosAdmin() {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de plantas", plantaService.obtenerTodos()));
    }

    // GET: Ver detalle incluso si está borrada
    @GetMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<PlantaResumen>> obtenerPorIdAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Detalle de planta (Admin)", plantaService.obtenerPorId(id)));
    }
}