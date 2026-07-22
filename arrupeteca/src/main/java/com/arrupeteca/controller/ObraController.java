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

@RestController
@RequestMapping("/api/v1/obras")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Obras Literarias", description = "Endpoints para la gestión de obras en el catálogo")
public class ObraController {

    private final ObraService obraService;

    @GetMapping
    @Operation(
        summary = "Obtener todas las obras activas",
        description = "Retorna una página de obras que no están borradas lógicamente."
    )
    public ResponseEntity<ApiResponse<Page<ObraResumen>>> obtenerTodosActivos(
            @ParameterObject @PageableDefault(size = 20, sort = "titulo") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de obras activas", obraService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener detalle de obra activa por ID",
        description = "Retorna el detalle completo de una obra activa (incluyendo géneros, categorías y autores)."
    )
    public ResponseEntity<ApiResponse<ObraDetalleResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Obra encontrada", obraService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/global")
    @Operation(
        summary = "Búsqueda global de obras",
        description = "Realiza una búsqueda global por palabra clave coincidente en título o resumen de obras activas."
    )
    public ResponseEntity<ApiResponse<Page<ObraResumen>>> busquedaGlobal(
            @RequestParam String palabra,
            @ParameterObject @PageableDefault(size = 20, sort = "titulo") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda global", obraService.busquedaGlobal(palabra, pageable)));
    }

    @GetMapping("/admin")
    @Operation(
        summary = "Obtener historial completo de obras (Admin)",
        description = "Retorna todas las obras registradas, incluyendo las eliminadas lógicamente (solo para administradores)."
    )
    public ResponseEntity<ApiResponse<Page<ObraResumen>>> obtenerTodos(
            @ParameterObject @PageableDefault(size = 20, sort = "titulo") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de obras", obraService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener detalle de obra por ID (Admin)",
        description = "Busca y retorna el detalle de cualquier obra (activa o borrada) por su identificador (solo para administradores)."
    )
    public ResponseEntity<ApiResponse<ObraDetalleResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Obra encontrada (admin)", obraService.obtenerPorId(id)));
    }

    @GetMapping("/admin/busqueda-maestra")
    @Operation(
        summary = "Búsqueda maestra multi-filtro (Admin)",
        description = "Búsqueda avanzada de obras con filtros de estado lógico, género, categoría, rango de años y coincidencia de término (solo para administradores)."
    )
    public ResponseEntity<ApiResponse<Page<ObraResumen>>> busquedaMaestraAdmin(
            @RequestParam(required = false) Boolean esBorrado,
            @RequestParam(required = false) Long idGenero,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) String termino,
            @RequestParam(required = false) Integer anioInicio,
            @RequestParam(required = false) Integer anioFin,
            @ParameterObject @PageableDefault(size = 20, sort = "titulo") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda maestra",
                obraService.busquedaMaestraAdmin(esBorrado, idGenero, idCategoria, termino, anioInicio, anioFin, pageable)));
    }

    @PostMapping
    @Operation(
        summary = "Registrar nueva obra",
        description = "Crea una nueva obra en el sistema con sus relaciones de géneros, categorías y autores asignados."
    )
    public ResponseEntity<ApiResponse<ObraDetalleResponse>> crear(@Valid @RequestBody ObraRequest request) {
        return new ResponseEntity<>(ApiResponse.exito("Obra creada exitosamente", obraService.crear(request)), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @Operation(
        summary = "Actualizar obra existente",
        description = "Actualiza los datos y relaciones de una obra existente validando el bloqueo optimista."
    )
    public ResponseEntity<ApiResponse<ObraDetalleResponse>> actualizar(@PathVariable Long id, @Valid @RequestBody ObraRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Obra actualizada correctamente", obraService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar obra (Borrado lógico)",
        description = "Desactiva lógicamente una obra en el catálogo público."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        obraService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Obra desactivada correctamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar obra",
        description = "Reactiva una obra previamente borrada lógicamente."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        obraService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Obra activada correctamente"));
    }
}
