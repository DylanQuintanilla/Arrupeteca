package com.arrupeteca.controller;

import com.arrupeteca.dto.request.EtiquetaRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.EtiquetaResponse;
import com.arrupeteca.service.EtiquetaService;
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
@RequestMapping("/api/v1/etiquetas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Etiquetas", description = "Endpoints para la gestión de etiquetas de obras literarias")
public class EtiquetaController {

    private final EtiquetaService etiquetaService;

    @GetMapping
    @Operation(
        summary = "Obtener etiquetas de obras literarias activos(as) para dropdowns y autocompletado",
        description = "Retorna una lista no paginada de etiquetas de obras literarias activos(as), con soporte opcional para filtrado por nombre."
    )
    public ResponseEntity<ApiResponse<List<EtiquetaResponse>>> obtenerTodosActivos(
            @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de etiquetas de obras literarias activos(as)", etiquetaService.obtenerTodosActivos(nombre)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Obtener tabla de administración de etiquetas de obras literarias",
        description = "Retorna una página de etiquetas de obras literarias con soporte para filtros por nombre y estado borradoLogico."
    )
    public ResponseEntity<ApiResponse<Page<EtiquetaResponse>>> obtenerTodos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean borradoLogico,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Tabla de etiquetas de obras literarias", etiquetaService.obtenerTodos(nombre, borradoLogico, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener etiquetas de obras literarias por ID",
        description = "Busca la información de un(a) etiqueta por su ID sin importar su estado lógico."
    )
    public ResponseEntity<ApiResponse<EtiquetaResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Etiqueta encontrado(a)", etiquetaService.obtenerPorId(id)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nuevo(a) etiqueta",
        description = "Crea un(a) nuevo(a) etiqueta en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<EtiquetaResponse>> crear(@Valid @RequestBody EtiquetaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Etiqueta creado(a) exitosamente", etiquetaService.crear(request)));
    }

    @PutMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar etiqueta",
        description = "Actualiza los campos de un(a) etiqueta existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<EtiquetaResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody EtiquetaRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Etiqueta actualizado(a) correctamente",
                etiquetaService.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar etiqueta (Borrado lógico)",
        description = "Oculta el/la etiqueta en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        etiquetaService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Etiqueta desactivado(a) correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar etiqueta",
        description = "Restaura la visibilidad pública de un(a) etiqueta previamente desactivado(a)."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        etiquetaService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Etiqueta activado(a) correctamente"));
    }}
