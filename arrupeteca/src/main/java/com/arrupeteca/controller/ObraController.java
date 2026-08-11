package com.arrupeteca.controller;

import com.arrupeteca.dto.request.ObraRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.detail.ObraDetalleResponse;
import com.arrupeteca.dto.response.summary.ObraResumen;
import com.arrupeteca.service.ObraService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/obras")
@RequiredArgsConstructor
@Tag(name = "Obras Literarias", description = "Endpoints para la gestión de obras en el catálogo")
public class ObraController {

    private final ObraService obraService;

    @GetMapping("/admin/all")
    @Operation(
        summary = "Búsqueda de administración de obras",
        description = "Búsqueda multi-filtro por estado lógico, género, categoría, etiqueta, rango de años y coincidencia de término (solo admin)."
    )
    public ResponseEntity<ApiResponse<Page<ObraResumen>>> busquedaAdmin(
            @RequestParam(required = false) Boolean borradoLogico,
            @RequestParam(required = false) Long idGenero,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) Long idEtiqueta,
            @RequestParam(required = false) String termino,
            @RequestParam(required = false) Integer anioInicio,
            @RequestParam(required = false) Integer anioFin,
            @ParameterObject @PageableDefault(size = 20, sort = "titulo") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados de búsqueda de administración de obras",
                obraService.busquedaAdmin(borradoLogico, idGenero, idCategoria, idEtiqueta, termino, anioInicio, anioFin, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener detalle de obra por ID (Admin)",
        description = "Busca y retorna el detalle de cualquier obra (activa o borrada) por su identificador (solo admin)."
    )
    public ResponseEntity<ApiResponse<ObraDetalleResponse>> busquedaAdminPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Obra encontrada (admin)", obraService.busquedaAdminPorId(id)));
    }

    @GetMapping("/admin/verificar-duplicados")
    @Operation(
        summary = "Verificar duplicados de obras (Admin)",
        description = "Busca obras con títulos similares en el catálogo (solo admin)."
    )
    public ResponseEntity<ApiResponse<java.util.List<ObraResumen>>> buscarDuplicados(
            @RequestParam String termino,
            @RequestParam(required = false) Long idExcluir) {
        return ResponseEntity.ok(ApiResponse.exito("Verificación completada", obraService.buscarDuplicados(termino, idExcluir)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nueva obra (Admin)",
        description = "Crea una nueva obra en el sistema con sus relaciones asignadas (solo admin)."
    )
    public ResponseEntity<ApiResponse<ObraDetalleResponse>> crear(@Valid @RequestBody ObraRequest request) {
        return new ResponseEntity<>(ApiResponse.exito("Obra creada exitosamente", obraService.crear(request)), HttpStatus.CREATED);
    }

    @PatchMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar obra existente (Admin)",
        description = "Actualiza los datos y relaciones de una obra existente validando el bloqueo optimista (solo admin)."
    )
    public ResponseEntity<ApiResponse<ObraDetalleResponse>> actualizar(@PathVariable Long id, @Valid @RequestBody ObraRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Obra actualizada correctamente", obraService.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar obra (Admin)",
        description = "Desactiva lógicamente una obra en el catálogo público (solo admin)."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        obraService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Obra desactivada correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar obra (Admin)",
        description = "Reactiva una obra previamente borrada lógicamente (solo admin)."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        obraService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Obra activada correctamente"));
    }
}

