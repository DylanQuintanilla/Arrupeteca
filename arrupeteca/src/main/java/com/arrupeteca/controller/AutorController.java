package com.arrupeteca.controller;

import com.arrupeteca.dto.request.AutorRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.detail.AutorDetalleResponse;
import com.arrupeteca.dto.response.summary.AutorResumenResponse;
import com.arrupeteca.service.AutorService;
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

import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/api/v1/autores")
@RequiredArgsConstructor
@Tag(name = "Autores", description = "Endpoints para la gestión de autores de obras literarias")
public class AutorController {

    private final AutorService autorService;

    @GetMapping
    @Operation(
        summary = "Búsqueda global de autores activos",
        description = "Retorna una página de autores activos permitiendo filtrar por término (nombre/seudónimo), período de vida, país, nacionalidad, género u obra cumbre."
    )
    public ResponseEntity<ApiResponse<Page<AutorResumenResponse>>> busquedaGlobal(
            @RequestParam(required = false) Long idNacionalidad,
            @RequestParam(required = false) Long idPais,
            @RequestParam(required = false) Long idGenero,
            @RequestParam(required = false) Long idObraCumbre,
            @RequestParam(required = false) Year anioInicio,
            @RequestParam(required = false) Year anioFin,
            @RequestParam(required = false) String termino,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados de búsqueda pública de autores activos",
                autorService.busquedaGlobal(idNacionalidad, idPais, idGenero, idObraCumbre, anioInicio, anioFin, termino, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener detalle de autor activo",
        description = "Busca la información detallada de un autor por su ID únicamente si está activo."
    )
    public ResponseEntity<ApiResponse<AutorDetalleResponse>> busquedaGlobalPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Autor activo encontrado", autorService.busquedaGlobalPorId(id)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Búsqueda de administración de autores",
        description = "Búsqueda completa multi-filtro por borrado lógico, país, nacionalidad, género, obra cumbre, período de vida y término de coincidencia (solo admin)."
    )
    public ResponseEntity<ApiResponse<Page<AutorResumenResponse>>> busquedaAdmin(
            @RequestParam(required = false) Boolean borradoLogico,
            @RequestParam(required = false) Long idNacionalidad,
            @RequestParam(required = false) Long idPais,
            @RequestParam(required = false) Long idGenero,
            @RequestParam(required = false) Long idObraCumbre,
            @RequestParam(required = false) Year anioInicio,
            @RequestParam(required = false) Year anioFin,
            @RequestParam(required = false) String termino,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados de búsqueda de administración de autores",
                autorService.busquedaAdmin(borradoLogico, idNacionalidad, idPais, idGenero, idObraCumbre,
                        anioInicio, anioFin, termino, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener autor por ID (Admin)",
        description = "Busca el detalle de un autor por su ID sin importar su estado de borrado lógico (solo admin)."
    )
    public ResponseEntity<ApiResponse<AutorDetalleResponse>> busquedaAdminPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Autor encontrado (admin)", autorService.busquedaAdminPorId(id)));
    }

    @GetMapping("/admin/verificar-duplicados")
    @Operation(
        summary = "Verificar duplicados (Admin)",
        description = "Busca autores con nombres o seudónimos similares en el sistema (solo admin)."
    )
    public ResponseEntity<ApiResponse<List<AutorResumenResponse>>> buscarDuplicados(
            @RequestParam String termino,
            @RequestParam(required = false) Long idExcluir) {
        return ResponseEntity.ok(ApiResponse.exito("Verificación completada", autorService.buscarDuplicados(termino, idExcluir)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nuevo autor (Admin)",
        description = "Crea un nuevo autor en el sistema validando la unicidad del nombre (solo admin)."
    )
    public ResponseEntity<ApiResponse<AutorDetalleResponse>> crear(@Valid @RequestBody AutorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Autor creado exitosamente", autorService.crear(request)));
    }

    @PatchMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar autor (Admin)",
        description = "Actualiza los campos de un autor existente y valida su versión de bloqueo optimista (solo admin)."
    )
    public ResponseEntity<ApiResponse<AutorDetalleResponse>> actualizar(@PathVariable Long id, @Valid @RequestBody AutorRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Autor actualizado correctamente", autorService.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar autor (Admin)",
        description = "Oculta el autor en el catálogo público cambiando su estado a borrado (solo admin)."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        autorService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Autor desactivado correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar autor (Admin)",
        description = "Restaura la visibilidad pública de un autor previamente desactivado (solo admin)."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        autorService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Autor activado correctamente"));
    }
}
