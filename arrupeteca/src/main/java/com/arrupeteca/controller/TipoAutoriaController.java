package com.arrupeteca.controller;

import com.arrupeteca.dto.request.TipoAutoriaRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.TipoAutoriaResponse;
import com.arrupeteca.service.TipoAutoriaService;
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
@RequestMapping("/api/v1/tipo-autorias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Tipos de Autoría", description = "Endpoints para la gestión del catálogo de tipos de autoría")
public class TipoAutoriaController {

    private final TipoAutoriaService tipoAutoriaService;

    @GetMapping
    @Operation(
        summary = "Obtener tipos de autoría activos paginados",
        description = "Retorna una lista paginada de todos los tipos de autoría activos en el sistema."
    )
    public ResponseEntity<ApiResponse<Page<TipoAutoriaResponse>>> obtenerTodosActivos(
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de tipos de autoría activos", tipoAutoriaService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener tipo de autoría activo por ID",
        description = "Busca y retorna un tipo de autoría activo por su identificador único."
    )
    public ResponseEntity<ApiResponse<TipoAutoriaResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Tipo de autoría encontrado", tipoAutoriaService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/admin")
    @Operation(
        summary = "Obtener todos los tipos de autoría (Admin)",
        description = "Retorna el historial completo de tipos de autoría (activos e inactivos) para administradores."
    )
    public ResponseEntity<ApiResponse<Page<TipoAutoriaResponse>>> obtenerTodos(
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de tipos de autoría", tipoAutoriaService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener tipo de autoría por ID (Admin)",
        description = "Busca y retorna cualquier tipo de autoría (activo o inactivo) por su identificador único para administradores."
    )
    public ResponseEntity<ApiResponse<TipoAutoriaResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Tipo de autoría encontrado (admin)", tipoAutoriaService.obtenerPorId(id)));
    }

    @PostMapping
    @Operation(
        summary = "Crear nuevo tipo de autoría",
        description = "Crea un nuevo tipo de autoría validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<TipoAutoriaResponse>> crear(@Valid @RequestBody TipoAutoriaRequest request) {
        return new ResponseEntity<>(ApiResponse.exito("Tipo de autoría creado exitosamente",
                tipoAutoriaService.crear(request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar tipo de autoría",
        description = "Actualiza los datos de un tipo de autoría existente."
    )
    public ResponseEntity<ApiResponse<TipoAutoriaResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody TipoAutoriaRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Tipo de autoría actualizado correctamente",
                tipoAutoriaService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar tipo de autoría",
        description = "Realiza el borrado lógico de un tipo de autoría en el sistema."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        tipoAutoriaService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Tipo de autoría desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar tipo de autoría",
        description = "Reactiva un tipo de autoría previamente desactivado."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        tipoAutoriaService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Tipo de autoría activado correctamente"));
    }
}
