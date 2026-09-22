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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/plantas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Plantas", description = "Endpoints para la gestión de plantas de edificios")
public class PlantaController {

    private final PlantaService plantaService;

    @GetMapping
    @Operation(
        summary = "Obtener plantas de edificios activos(as) para dropdowns y autocompletado",
        description = "Retorna una lista no paginada de plantas de edificios activos(as), con soporte opcional para filtrado por nombre."
    )
    public ResponseEntity<ApiResponse<List<PlantaResponse>>> obtenerTodosActivos(
            @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de plantas de edificios activos(as)", plantaService.obtenerTodosActivos(nombre)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Obtener tabla de administración de plantas de edificios",
        description = "Retorna una página de plantas de edificios con soporte para filtros por idEdificio, nombre y estado borradoLogico."
    )
    public ResponseEntity<ApiResponse<Page<PlantaResponse>>> obtenerTodos(
            @RequestParam(required = false) Long idEdificio,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean borradoLogico,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Tabla de plantas de edificios", plantaService.obtenerTodos(idEdificio, nombre, borradoLogico, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener plantas de edificios por ID",
        description = "Busca la información de un(a) planta por su ID sin importar su estado lógico."
    )
    public ResponseEntity<ApiResponse<PlantaResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Planta encontrado(a)", plantaService.obtenerPorId(id)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nuevo(a) planta",
        description = "Crea un(a) nuevo(a) planta en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<PlantaResponse>> crear(@Valid @RequestBody PlantaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Planta creado(a) exitosamente", plantaService.crear(request)));
    }

    @PutMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar planta",
        description = "Actualiza los campos de un(a) planta existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<PlantaResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody PlantaRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Planta actualizado(a) correctamente",
                plantaService.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar planta (Borrado lógico)",
        description = "Oculta el/la planta en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        plantaService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Planta desactivado(a) correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar planta",
        description = "Restaura la visibilidad pública de un(a) planta previamente desactivado(a)."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        plantaService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Planta activado(a) correctamente"));
    }
}
