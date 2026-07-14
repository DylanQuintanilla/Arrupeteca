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

import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/api/v1/autores")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Autores", description = "Endpoints para la gestión de autores literarios")
public class AutorController {

    private final AutorService autorService;

    @GetMapping
    @Operation(
        summary = "Obtener autores activos",
        description = "Retorna una página de autores registrados que no están borrados lógicamente."
    )
    public ResponseEntity<ApiResponse<Page<AutorResumenResponse>>> obtenerTodosActivos(
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de autores activos", autorService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener detalle de autor activo",
        description = "Busca la información detallada de un autor por su ID si está activo."
    )
    public ResponseEntity<ApiResponse<AutorDetalleResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Autor encontrado", autorService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/admin")
    @Operation(
        summary = "Obtener historial de autores (Admin)",
        description = "Retorna todos los autores incluyendo eliminados lógicamente (solo admin)."
    )
    public ResponseEntity<ApiResponse<Page<AutorResumenResponse>>> obtenerTodos(
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo", autorService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener autor por ID (Admin)",
        description = "Busca el detalle de un autor por su ID sin importar su estado lógico (solo admin)."
    )
    public ResponseEntity<ApiResponse<AutorDetalleResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Autor encontrado (admin)", autorService.obtenerPorId(id)));
    }

    @GetMapping("/busqueda")
    @Operation(
        summary = "Búsqueda global de autores",
        description = "Busca autores por coincidencia parcial de texto y rango de año de nacimiento."
    )
    public ResponseEntity<ApiResponse<Page<AutorResumenResponse>>> busquedaGlobal(
            @RequestParam(required = false) String palabra,
            @RequestParam(required = false) Year inicio,
            @RequestParam(required = false) Year fin,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados de búsqueda", autorService.busquedaGlobal(palabra, inicio, fin, pageable)));
    }

    @GetMapping("/admin/busqueda-maestra")
    @Operation(
        summary = "Búsqueda avanzada (Admin)",
        description = "Búsqueda avanzada multi-filtro por estado, género, país y coincidencia de término (solo admin)."
    )
    public ResponseEntity<ApiResponse<Page<AutorResumenResponse>>> busquedaMaestraAdmin(
            @RequestParam(required = false) Boolean esBorrado,
            @RequestParam(required = false) Long idNacionalidad,
            @RequestParam(required = false) Long idPais,
            @RequestParam(required = false) Long idGenero,
            @RequestParam(required = false) Year anioInicio,
            @RequestParam(required = false) Year anioFin,
            @RequestParam(required = false) String termino,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda maestra",
                autorService.busquedaMaestraAdmin(esBorrado, idNacionalidad, idPais, idGenero,
                        anioInicio, anioFin, termino, pageable)));
    }

    @GetMapping("/verificar-duplicados")
    @Operation(
        summary = "Verificar duplicados",
        description = "Busca autores con nombres o seudónimos similares en el sistema."
    )
    public ResponseEntity<ApiResponse<List<AutorResumenResponse>>> buscarDuplicados(
            @RequestParam String termino,
            @RequestParam(required = false) Long idExcluir) {
        return ResponseEntity.ok(ApiResponse.exito("Verificación completada", autorService.buscarDuplicados(termino, idExcluir)));
    }

    @PostMapping
    @Operation(
        summary = "Registrar nuevo autor",
        description = "Crea un nuevo autor en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<AutorDetalleResponse>> crear(@Valid @RequestBody AutorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Autor creado exitosamente", autorService.crear(request)));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar autor",
        description = "Actualiza los campos de un autor existente y valida su versión de bloqueo optimista."
    )
    public ResponseEntity<ApiResponse<AutorDetalleResponse>> actualizar(@PathVariable Long id, @Valid @RequestBody AutorRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Autor actualizado correctamente", autorService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar autor (Borrado lógico)",
        description = "Oculta el autor en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        autorService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Autor desactivado"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar autor",
        description = "Restaura la visibilidad pública de un autor previamente desactivado."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        autorService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Autor activado"));
    }
}
