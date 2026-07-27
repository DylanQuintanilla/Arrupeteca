package com.arrupeteca.controller;

import com.arrupeteca.dto.request.EdificioRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.EdificioResponse;
import com.arrupeteca.service.EdificioService;
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
@RequestMapping("/api/v1/edificios")
@Tag(name = "Edificios", description = "Endpoints para la gestión del catálogo de edificios")
public class EdificioController {

    private final EdificioService edificioService;

    @GetMapping
    @Operation(
        summary = "Obtener edificios activos paginados",
        description = "Retorna una lista paginada de todos los edificios activos en el sistema."
    )
    public ResponseEntity<ApiResponse<Page<EdificioResponse>>> obtenerTodosActivos(
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        Page<EdificioResponse> pagina = edificioService.obtenerTodosActivos(pageable);
        return ResponseEntity.ok(ApiResponse.exito("Lista de edificios activos", pagina));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener edificio activo por ID",
        description = "Busca y retorna un edificio activo por su identificador único."
    )
    public ResponseEntity<ApiResponse<EdificioResponse>> obtenerPorId(@PathVariable Long id) {
        EdificioResponse edificio = edificioService.obtenerPorIdActivo(id);
        return ResponseEntity.ok(ApiResponse.exito("Edificio encontrado", edificio));
    }

    @PostMapping
    @Operation(
        summary = "Crear nuevo edificio",
        description = "Crea un nuevo edificio validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<EdificioResponse>> crear(@Valid @RequestBody EdificioRequest request) {
        EdificioResponse nuevo = edificioService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Edificio creado exitosamente", nuevo));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar edificio",
        description = "Actualiza los datos de un edificio existente."
    )
    public ResponseEntity<ApiResponse<EdificioResponse>> actualizar(@PathVariable Long id,
                                                                    @Valid @RequestBody EdificioRequest request) {
        EdificioResponse actualizado = edificioService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Edificio actualizado correctamente", actualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar edificio",
        description = "Realiza el borrado lógico de un edificio en el sistema."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        edificioService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Edificio desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar edificio",
        description = "Reactiva un edificio previamente desactivado."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        edificioService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Edificio activado correctamente"));
    }

    @GetMapping("/admin")
    @Operation(
        summary = "Obtener todos los edificios (Admin)",
        description = "Retorna el historial completo de edificios (activos e inactivos) para administradores."
    )
    public ResponseEntity<ApiResponse<Page<EdificioResponse>>> obtenerTodosAdmin(
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.exito("Historial completo de edificios", edificioService.obtenerTodos(pageable))
        );
    }
}
