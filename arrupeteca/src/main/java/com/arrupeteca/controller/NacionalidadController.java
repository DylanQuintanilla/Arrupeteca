package com.arrupeteca.controller;

import com.arrupeteca.dto.request.NacionalidadRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.NacionalidadResponse;
import com.arrupeteca.service.NacionalidadService;
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
@RequestMapping("/api/v1/nacionalidades")
@Tag(name = "Nacionalidades", description = "Endpoints para la gestión del catálogo de nacionalidades")
public class NacionalidadController {

    private final NacionalidadService nacionalidadService;

    @GetMapping
    @Operation(
        summary = "Obtener nacionalidades activas paginadas",
        description = "Retorna una lista paginada de todas las nacionalidades activas en el sistema."
    )
    public ResponseEntity<ApiResponse<Page<NacionalidadResponse>>> obtenerTodosActivos(
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        Page<NacionalidadResponse> pagina = nacionalidadService.obtenerTodosActivos(pageable);
        return ResponseEntity.ok(ApiResponse.exito("Lista de nacionalidades activas", pagina));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener nacionalidad activa por ID",
        description = "Busca y retorna una nacionalidad activa por su identificador único."
    )
    public ResponseEntity<ApiResponse<NacionalidadResponse>> obtenerPorId(@PathVariable Long id) {
        NacionalidadResponse nacionalidad = nacionalidadService.obtenerPorIdActivo(id);
        return ResponseEntity.ok(ApiResponse.exito("Nacionalidad encontrada", nacionalidad));
    }

    @PostMapping
    @Operation(
        summary = "Crear nueva nacionalidad",
        description = "Crea una nueva nacionalidad validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<NacionalidadResponse>> crear(@Valid @RequestBody NacionalidadRequest request) {
        NacionalidadResponse nueva = nacionalidadService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Nacionalidad creada exitosamente", nueva));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar nacionalidad",
        description = "Actualiza los datos de una nacionalidad existente."
    )
    public ResponseEntity<ApiResponse<NacionalidadResponse>> actualizar(@PathVariable Long id,
                                                                        @Valid @RequestBody NacionalidadRequest request) {
        NacionalidadResponse actualizada = nacionalidadService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Nacionalidad actualizada correctamente", actualizada));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar nacionalidad",
        description = "Realiza el borrado lógico de una nacionalidad en el sistema."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        nacionalidadService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Nacionalidad desactivada correctamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar nacionalidad",
        description = "Reactiva una nacionalidad previamente desactivada."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        nacionalidadService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Nacionalidad activada correctamente"));
    }

    @GetMapping("/admin")
    @Operation(
        summary = "Obtener todas las nacionalidades (Admin)",
        description = "Retorna el historial completo de nacionalidades (activas e inactivas) para administradores."
    )
    public ResponseEntity<ApiResponse<Page<NacionalidadResponse>>> obtenerTodosAdmin(
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.exito("Historial completo de nacionalidades", nacionalidadService.obtenerTodos(pageable))
        );
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener nacionalidad por ID (Admin)",
        description = "Busca y retorna cualquier nacionalidad (activa o inactiva) por su identificador único para administradores."
    )
    public ResponseEntity<ApiResponse<NacionalidadResponse>> obtenerPorIdAdmin(@PathVariable Long id) {
        NacionalidadResponse nacionalidad = nacionalidadService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.exito("Nacionalidad encontrada (admin)", nacionalidad));
    }
}
