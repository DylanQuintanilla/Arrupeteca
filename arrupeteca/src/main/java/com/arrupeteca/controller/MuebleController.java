package com.arrupeteca.controller;

import com.arrupeteca.dto.request.MuebleRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.MuebleResponse;
import com.arrupeteca.service.MuebleService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/muebles")
@RequiredArgsConstructor
@Tag(name = "Muebles", description = "Endpoints para la gestión del catálogo de muebles")
public class MuebleController {

    private final MuebleService muebleService;

    @GetMapping
    @Operation(
        summary = "Obtener muebles activos paginados",
        description = "Retorna una lista paginada de todos los muebles activos en el sistema."
    )
    public ResponseEntity<ApiResponse<Page<MuebleResponse>>> obtenerTodosActivos(
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de muebles activos", muebleService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener mueble activo por ID",
        description = "Busca y retorna un mueble activo por su identificador único."
    )
    public ResponseEntity<ApiResponse<MuebleResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Mueble encontrado", muebleService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/global")
    @Operation(
        summary = "Búsqueda global de muebles",
        description = "Busca muebles activos filtrando por palabra clave en su nombre."
    )
    public ResponseEntity<ApiResponse<Page<MuebleResponse>>> busquedaGlobal(
            @RequestParam String palabra,
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda global", muebleService.busquedaGlobal(palabra, pageable)));
    }

    @GetMapping("/admin")
    @Operation(
        summary = "Obtener todos los muebles (Admin)",
        description = "Retorna el historial completo de muebles (activos e inactivos) para administradores."
    )
    public ResponseEntity<ApiResponse<Page<MuebleResponse>>> obtenerTodos(
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de muebles", muebleService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener mueble por ID (Admin)",
        description = "Busca y retorna cualquier mueble (activo o inactivo) por su identificador único para administradores."
    )
    public ResponseEntity<ApiResponse<MuebleResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Mueble encontrado (admin)", muebleService.obtenerPorId(id)));
    }

    @GetMapping("/admin/busqueda-maestra")
    @Operation(
        summary = "Búsqueda maestra de muebles (Admin)",
        description = "Permite filtrar muebles por estado de borrado, salón o término de búsqueda."
    )
    public ResponseEntity<ApiResponse<Page<MuebleResponse>>> busquedaMaestraAdmin(
            @RequestParam(required = false) Boolean esBorrado,
            @RequestParam(required = false) Long idSalon,
            @RequestParam(required = false) String termino,
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda maestra",
                muebleService.busquedaMaestraAdmin(esBorrado, idSalon, termino, pageable)));
    }

    @PostMapping
    @Operation(
        summary = "Crear nuevo mueble",
        description = "Crea un nuevo mueble dentro de un salón validando la unicidad del nombre por salón."
    )
    public ResponseEntity<ApiResponse<MuebleResponse>> crear(@Valid @RequestBody MuebleRequest request) {
        return new ResponseEntity<>(ApiResponse.exito("Mueble creado exitosamente", muebleService.crear(request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar mueble",
        description = "Actualiza los datos de un mueble existente."
    )
    public ResponseEntity<ApiResponse<MuebleResponse>> actualizar(@PathVariable Long id, @Valid @RequestBody MuebleRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Mueble actualizado correctamente", muebleService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar mueble",
        description = "Realiza el borrado lógico de un mueble en el sistema."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        muebleService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Mueble desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar mueble",
        description = "Reactiva un mueble previamente desactivado."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        muebleService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Mueble activado correctamente"));
    }
}
