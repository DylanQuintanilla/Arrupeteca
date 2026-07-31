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
        summary = "Obtener etiquetas activas para dropdowns",
        description = "Retorna una lista simple no paginada de etiquetas activas para selects y filtros."
    )
    public ResponseEntity<ApiResponse<List<EtiquetaResponse>>> obtenerTodosActivos() {
        return ResponseEntity.ok(ApiResponse.exito("Lista de etiquetas activas", etiquetaService.obtenerTodosActivos()));
    }

    @GetMapping("/all")
    @Operation(
        summary = "Obtener tabla de administración de etiquetas",
        description = "Retorna una página de etiquetas con soporte para filtros por nombre y estado borradoLogico."
    )
    public ResponseEntity<ApiResponse<Page<EtiquetaResponse>>> obtenerTodos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean borradoLogico,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Tabla de etiquetas", etiquetaService.obtenerTodos(nombre, borradoLogico, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener etiqueta por ID",
        description = "Busca la información de una etiqueta por su ID sin importar su estado lógico."
    )
    public ResponseEntity<ApiResponse<EtiquetaResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Etiqueta encontrada", etiquetaService.obtenerPorId(id)));
    }

    @PostMapping
    @Operation(
        summary = "Registrar nueva etiqueta",
        description = "Crea una nueva etiqueta en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<EtiquetaResponse>> crear(@Valid @RequestBody EtiquetaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Etiqueta creada exitosamente", etiquetaService.crear(request)));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar etiqueta",
        description = "Actualiza los campos de una etiqueta existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<EtiquetaResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody EtiquetaRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Etiqueta actualizada correctamente",
                etiquetaService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar etiqueta (Borrado lógico)",
        description = "Oculta la etiqueta en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        etiquetaService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Etiqueta desactivada correctamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar etiqueta",
        description = "Restaura la visibilidad pública de una etiqueta previamente desactivada."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        etiquetaService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Etiqueta activada correctamente"));
    }
}
