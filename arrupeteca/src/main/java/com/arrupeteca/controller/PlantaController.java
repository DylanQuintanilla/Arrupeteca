package com.arrupeteca.controller;

import com.arrupeteca.dto.request.PlantaRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.PlantaResponse;
import com.arrupeteca.service.PlantaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@Tag(name = "Plantas", description = "Endpoints para la gestión del catálogo de plantas de edificios")
public class PlantaController {

    private final PlantaService plantaService;

    @GetMapping
    @Operation(
        summary = "Obtener plantas activas paginadas",
        description = "Retorna una lista paginada de todas las plantas activas en el sistema."
    )
    public ResponseEntity<ApiResponse<Page<PlantaResponse>>> obtenerTodosActivos(
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de plantas activas", plantaService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener planta activa por ID",
        description = "Busca y retorna una planta activa por su identificador único."
    )
    public ResponseEntity<ApiResponse<PlantaResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Planta encontrada", plantaService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/admin")
    @Operation(
        summary = "Obtener todas las plantas (Admin)",
        description = "Retorna el historial completo de plantas (activas e inactivas) para administradores."
    )
    public ResponseEntity<ApiResponse<Page<PlantaResponse>>> obtenerTodos(
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de plantas", plantaService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener planta por ID (Admin)",
        description = "Busca y retorna cualquier planta (activa o inactiva) por su identificador único para administradores."
    )
    public ResponseEntity<ApiResponse<PlantaResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Planta encontrada (admin)", plantaService.obtenerPorId(id)));
    }

    @GetMapping("/edificio/{idEdificio}")
    @Operation(
        summary = "Buscar plantas por ID de edificio",
        description = "Retorna las plantas activas asociadas a un edificio específico."
    )
    public ResponseEntity<ApiResponse<Page<PlantaResponse>>> buscarPorEdificioId(
            @PathVariable Long idEdificio,
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Plantas del edificio", plantaService.buscarPorEdificioId(idEdificio, pageable)));
    }

    @PostMapping
    @Operation(
        summary = "Crear nueva planta",
        description = "Crea una nueva planta dentro de un edificio validando la unicidad del nombre por edificio."
    )
    public ResponseEntity<ApiResponse<PlantaResponse>> crear(@Valid @RequestBody PlantaRequest request) {
        return new ResponseEntity<>(ApiResponse.exito("Planta creada exitosamente", plantaService.crear(request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar planta",
        description = "Actualiza los datos de una planta existente."
    )
    public ResponseEntity<ApiResponse<PlantaResponse>> actualizar(@PathVariable Long id, @Valid @RequestBody PlantaRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Planta actualizada correctamente", plantaService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar planta",
        description = "Realiza el borrado lógico de una planta en el sistema."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        plantaService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Planta desactivada correctamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar planta",
        description = "Reactiva una planta previamente desactivada."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        plantaService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Planta activada correctamente"));
    }
}
