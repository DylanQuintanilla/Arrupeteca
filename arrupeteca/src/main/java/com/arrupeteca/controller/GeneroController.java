package com.arrupeteca.controller;

import com.arrupeteca.dto.request.GeneroRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.GeneroResponse;
import com.arrupeteca.service.GeneroService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/generos")
@Tag(name = "Géneros", description = "Endpoints para la gestión del catálogo de géneros literarios")
public class GeneroController {

    private final GeneroService generoService;

    @GetMapping
    @Operation(
        summary = "Obtener géneros activos paginados",
        description = "Retorna una lista paginada de todos los géneros literarios activos en el sistema."
    )
    public ResponseEntity<ApiResponse<Page<GeneroResponse>>> obtenerTodosActivos(
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        Page<GeneroResponse> pagina = generoService.obtenerTodosActivos(pageable);
        return ResponseEntity.ok(ApiResponse.exito("Lista de géneros activos", pagina));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener género activo por ID",
        description = "Busca y retorna un género literario activo por su identificador único."
    )
    public ResponseEntity<ApiResponse<GeneroResponse>> obtenerPorId(@PathVariable Long id) {
        GeneroResponse genero = generoService.obtenerPorIdActivo(id);
        return ResponseEntity.ok(ApiResponse.exito("Género encontrado", genero));
    }

    @PostMapping
    @Operation(
        summary = "Crear nuevo género",
        description = "Crea un nuevo género literario validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<GeneroResponse>> crear(@Valid @RequestBody GeneroRequest request) {
        GeneroResponse nuevo = generoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Género creado exitosamente", nuevo));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar género",
        description = "Actualiza los datos de un género literario existente."
    )
    public ResponseEntity<ApiResponse<GeneroResponse>> actualizar(@PathVariable Long id,
                                                                  @Valid @RequestBody GeneroRequest request) {
        GeneroResponse actualizado = generoService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Género actualizado correctamente", actualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar género",
        description = "Realiza el borrado lógico de un género literario en el sistema."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        generoService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Género desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar género",
        description = "Reactiva un género literario previamente desactivado."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        generoService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Género activado correctamente"));
    }

    @GetMapping("/admin")
    @Operation(
        summary = "Obtener todos los géneros (Admin)",
        description = "Retorna el historial completo de géneros literarios (activos e inactivos) para administradores."
    )
    public ResponseEntity<ApiResponse<Page<GeneroResponse>>> obtenerTodosAdmin(
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.exito("Historial completo de géneros", generoService.obtenerTodos(pageable))
        );
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener género por ID (Admin)",
        description = "Busca y retorna cualquier género (activo o inactivo) por su identificador único para administradores."
    )
    public ResponseEntity<ApiResponse<GeneroResponse>> obtenerPorIdAdmin(@PathVariable Long id) {
        GeneroResponse genero = generoService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.exito("Género encontrado (admin)", genero));
    }
}
