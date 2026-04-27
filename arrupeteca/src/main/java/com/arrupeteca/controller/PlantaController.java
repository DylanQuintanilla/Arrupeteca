package com.arrupeteca.controller;

import com.arrupeteca.dto.request.PlantaRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.detail.PlantaDetalleResponse;
import com.arrupeteca.dto.response.summary.PlantaResumen;
import com.arrupeteca.service.PlantaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/api/v1/plantas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlantaController {

    private final PlantaService plantaService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PlantaResumen>>> obtenerTodosActivos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de plantas activas", plantaService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlantaDetalleResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Planta encontrada", plantaService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<PlantaResumen>>> obtenerTodos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de plantas", plantaService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<PlantaDetalleResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Planta encontrada (admin)", plantaService.obtenerPorId(id)));
    }

    @GetMapping("/edificio/{idEdificio}")
    public ResponseEntity<ApiResponse<Page<PlantaResumen>>> buscarPorEdificioId(@PathVariable Long idEdificio, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Plantas del edificio", plantaService.buscarPorEdificioId(idEdificio, pageable)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PlantaDetalleResponse>> crear(@Valid @RequestBody PlantaRequest request) {
        return new ResponseEntity<>(ApiResponse.exito("Planta creada exitosamente", plantaService.crear(request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PlantaDetalleResponse>> actualizar(@PathVariable Long id, @Valid @RequestBody PlantaRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Planta actualizada correctamente", plantaService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        plantaService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Planta desactivada correctamente"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        plantaService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Planta activada correctamente"));
    }
}
