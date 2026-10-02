package com.arrupeteca.transferencia.controller;

import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.transferencia.dto.request.EstadoTransferenciaRequest;
import com.arrupeteca.transferencia.dto.response.EstadoTransferenciaResponse;
import com.arrupeteca.transferencia.internal.service.EstadoTransferenciaService;
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
@RequestMapping("/api/v1/estados-transferencia")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Estados de Transferencia", description = "Endpoints para la gestion de estados de la ejecucion de transferencias")
public class EstadoTransferenciaController {

    private final EstadoTransferenciaService service;

    @GetMapping
    @Operation(
        summary = "Obtener estados de transferencia activos para dropdowns",
        description = "Retorna una lista no paginada de estados activos, con soporte opcional para filtrado por nombre."
    )
    public ResponseEntity<ApiResponse<List<EstadoTransferenciaResponse>>> obtenerTodosActivos(
            @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de estados de transferencia activos", service.obtenerTodosActivos(nombre)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Obtener tabla de administracion de estados de transferencia",
        description = "Retorna una pagina de estados con soporte para filtros por nombre y borradoLogico."
    )
    public ResponseEntity<ApiResponse<Page<EstadoTransferenciaResponse>>> obtenerTodos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean borradoLogico,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Tabla de estados de transferencia", service.obtenerTodos(nombre, borradoLogico, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener estado de transferencia por ID",
        description = "Busca la informacion de un estado de transferencia por su ID sin importar su estado logico."
    )
    public ResponseEntity<ApiResponse<EstadoTransferenciaResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Estado de transferencia encontrado", service.obtenerPorId(id)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nuevo estado de transferencia",
        description = "Crea un nuevo estado de transferencia en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<EstadoTransferenciaResponse>> crear(@Valid @RequestBody EstadoTransferenciaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Estado de transferencia creado exitosamente", service.crear(request)));
    }

    @PutMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar estado de transferencia",
        description = "Actualiza los campos de un estado de transferencia existente."
    )
    public ResponseEntity<ApiResponse<EstadoTransferenciaResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody EstadoTransferenciaRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Estado de transferencia actualizado correctamente",
                service.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar estado de transferencia (Borrado logico)",
        description = "Oculta el estado de transferencia cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        service.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Estado de transferencia desactivado correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar estado de transferencia",
        description = "Restaura la visibilidad publica de un estado previamente desactivado."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        service.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Estado de transferencia activado correctamente"));
    }
}
