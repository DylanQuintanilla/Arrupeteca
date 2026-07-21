package com.arrupeteca.controller;

import com.arrupeteca.dto.request.SalonRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.SalonResponse;
import com.arrupeteca.service.SalonService;
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
@RequestMapping("/api/v1/salones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Salones", description = "Endpoints para la gestión del catálogo de salones")
public class SalonController {

    private final SalonService salonService;

    @GetMapping
    @Operation(
        summary = "Obtener salones activos paginados",
        description = "Retorna una lista paginada de todos los salones activos en el sistema."
    )
    public ResponseEntity<ApiResponse<Page<SalonResponse>>> obtenerTodosActivos(
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de salones activos", salonService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener salón activo por ID",
        description = "Busca y retorna un salón activo por su identificador único."
    )
    public ResponseEntity<ApiResponse<SalonResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Salón encontrado", salonService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/admin")
    @Operation(
        summary = "Obtener todos los salones (Admin)",
        description = "Retorna el historial completo de salones (activos e inactivas) para administradores."
    )
    public ResponseEntity<ApiResponse<Page<SalonResponse>>> obtenerTodos(
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de salones", salonService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener salón por ID (Admin)",
        description = "Busca y retorna cualquier salón (activo o inactivo) por su identificador único para administradores."
    )
    public ResponseEntity<ApiResponse<SalonResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Salón encontrado (admin)", salonService.obtenerPorId(id)));
    }

    @GetMapping("/planta/{idPlanta}")
    @Operation(
        summary = "Buscar salones por ID de planta",
        description = "Retorna los salones activos asociados a una planta específica."
    )
    public ResponseEntity<ApiResponse<Page<SalonResponse>>> buscarPorPlantaId(
            @PathVariable Long idPlanta,
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Salones de la planta", salonService.buscarPorPlantaId(idPlanta, pageable)));
    }

    @PostMapping
    @Operation(
        summary = "Crear nuevo salón",
        description = "Crea un nuevo salón dentro de una planta validando la unicidad del nombre por planta."
    )
    public ResponseEntity<ApiResponse<SalonResponse>> crear(@Valid @RequestBody SalonRequest request) {
        return new ResponseEntity<>(ApiResponse.exito("Salón creado exitosamente", salonService.crear(request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar salón",
        description = "Actualiza los datos de un salón existente."
    )
    public ResponseEntity<ApiResponse<SalonResponse>> actualizar(@PathVariable Long id, @Valid @RequestBody SalonRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Salón actualizado correctamente", salonService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar salón",
        description = "Realiza el borrado lógico de un salón en el sistema."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        salonService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Salón desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar salón",
        description = "Reactiva un salón previamente desactivado."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        salonService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Salón activado correctamente"));
    }
}
