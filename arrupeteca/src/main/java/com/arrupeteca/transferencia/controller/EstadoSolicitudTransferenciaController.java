package com.arrupeteca.transferencia.controller;

import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.transferencia.dto.request.EstadoSolicitudTransferenciaRequest;
import com.arrupeteca.transferencia.dto.response.EstadoSolicitudTransferenciaResponse;
import com.arrupeteca.transferencia.internal.service.EstadoSolicitudTransferenciaService;
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
@RequestMapping("/api/v1/estados-solicitud-transferencia")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Estados de Solicitud de Transferencia", description = "Endpoints para la gestion de estados de solicitudes de transferencia")
public class EstadoSolicitudTransferenciaController {

    private final EstadoSolicitudTransferenciaService service;

    @GetMapping
    @Operation(
        summary = "Obtener estados de solicitud de transferencia activos para dropdowns",
        description = "Retorna una lista no paginada de estados activos, con soporte opcional para filtrado por nombre."
    )
    public ResponseEntity<ApiResponse<List<EstadoSolicitudTransferenciaResponse>>> obtenerTodosActivos(
            @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de estados de solicitud de transferencia activos", service.obtenerTodosActivos(nombre)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Obtener tabla de administracion de estados de solicitud de transferencia",
        description = "Retorna una pagina de estados con soporte para filtros por nombre y borradoLogico."
    )
    public ResponseEntity<ApiResponse<Page<EstadoSolicitudTransferenciaResponse>>> obtenerTodos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean borradoLogico,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Tabla de estados de solicitud de transferencia", service.obtenerTodos(nombre, borradoLogico, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener estado de solicitud de transferencia por ID",
        description = "Busca la informacion de un estado de solicitud de transferencia por su ID sin importar su estado logico."
    )
    public ResponseEntity<ApiResponse<EstadoSolicitudTransferenciaResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Estado de solicitud de transferencia encontrado", service.obtenerPorId(id)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nuevo estado de solicitud de transferencia",
        description = "Crea un nuevo estado de solicitud de transferencia en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<EstadoSolicitudTransferenciaResponse>> crear(@Valid @RequestBody EstadoSolicitudTransferenciaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Estado de solicitud de transferencia creado exitosamente", service.crear(request)));
    }

    @PutMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar estado de solicitud de transferencia",
        description = "Actualiza los campos de un estado de solicitud de transferencia existente."
    )
    public ResponseEntity<ApiResponse<EstadoSolicitudTransferenciaResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody EstadoSolicitudTransferenciaRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Estado de solicitud de transferencia actualizado correctamente",
                service.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar estado de solicitud de transferencia (Borrado logico)",
        description = "Oculta el estado de solicitud de transferencia cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        service.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Estado de solicitud de transferencia desactivado correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar estado de solicitud de transferencia",
        description = "Restaura la visibilidad publica de un estado previamente desactivado."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        service.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Estado de solicitud de transferencia activado correctamente"));
    }
}
