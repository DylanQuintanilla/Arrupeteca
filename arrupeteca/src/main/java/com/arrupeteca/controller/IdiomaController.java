package com.arrupeteca.controller;

import com.arrupeteca.dto.request.IdiomaRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.IdiomaResponse;
import com.arrupeteca.service.IdiomaService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/idiomas")
@Tag(name = "Idiomas", description = "Endpoints para la gestión del catálogo de idiomas")
public class IdiomaController {

    private final IdiomaService idiomaService;

    @GetMapping
    @Operation(
        summary = "Obtener idiomas activos paginados",
        description = "Retorna una lista paginada de todos los idiomas activos en el sistema."
    )
    public ResponseEntity<ApiResponse<Page<IdiomaResponse>>> obtenerTodosActivos(
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        Page<IdiomaResponse> pagina = idiomaService.obtenerTodosActivos(pageable);
        return ResponseEntity.ok(ApiResponse.exito("Lista de idiomas activos", pagina));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener idioma activo por ID",
        description = "Busca y retorna un idioma activo por su identificador único."
    )
    public ResponseEntity<ApiResponse<IdiomaResponse>> obtenerPorId(@PathVariable Long id) {
        IdiomaResponse idioma = idiomaService.obtenerPorIdActivo(id);
        return ResponseEntity.ok(ApiResponse.exito("Idioma encontrado", idioma));
    }

    @PostMapping
    @Operation(
        summary = "Crear nuevo idioma",
        description = "Crea un nuevo idioma validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<IdiomaResponse>> crear(@Valid @RequestBody IdiomaRequest request) {
        IdiomaResponse nuevo = idiomaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Idioma creado exitosamente", nuevo));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar idioma",
        description = "Actualiza los datos de un idioma existente."
    )
    public ResponseEntity<ApiResponse<IdiomaResponse>> actualizar(@PathVariable Long id,
                                                                  @Valid @RequestBody IdiomaRequest request) {
        IdiomaResponse actualizado = idiomaService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Idioma actualizado correctamente", actualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar idioma",
        description = "Realiza el borrado lógico de un idioma en el sistema."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        idiomaService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Idioma desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar idioma",
        description = "Reactiva un idioma previamente desactivado."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        idiomaService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Idioma activado correctamente"));
    }

    @GetMapping("/admin")
    @Operation(
        summary = "Obtener todos los idiomas (Admin)",
        description = "Retorna el historial completo de idiomas (activos e inactivos) para administradores."
    )
    public ResponseEntity<ApiResponse<Page<IdiomaResponse>>> obtenerTodosAdmin(
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.exito("Historial completo de idiomas", idiomaService.obtenerTodos(pageable))
        );
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener idioma por ID (Admin)",
        description = "Busca y retorna cualquier idioma (activo o inactivo) por su identificador único para administradores."
    )
    public ResponseEntity<ApiResponse<IdiomaResponse>> obtenerPorIdAdmin(@PathVariable Long id) {
        IdiomaResponse idioma = idiomaService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.exito("Idioma encontrado (admin)", idioma));
    }
}
