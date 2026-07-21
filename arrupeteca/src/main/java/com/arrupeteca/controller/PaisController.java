package com.arrupeteca.controller;

import com.arrupeteca.dto.request.PaisRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.PaisResponse;
import com.arrupeteca.service.PaisService;
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
@RequestMapping("/api/v1/paises")
@Tag(name = "Países", description = "Endpoints para la gestión del catálogo de países")
public class PaisController {

    private final PaisService paisService;

    @GetMapping
    @Operation(
        summary = "Obtener países activos paginados",
        description = "Retorna una lista paginada de todos los países activos en el sistema."
    )
    public ResponseEntity<ApiResponse<Page<PaisResponse>>> obtenerTodosActivos(
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        Page<PaisResponse> pagina = paisService.obtenerTodosActivos(pageable);
        return ResponseEntity.ok(ApiResponse.exito("Lista de países activos", pagina));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener país activo por ID",
        description = "Busca y retorna un país activo por su identificador único."
    )
    public ResponseEntity<ApiResponse<PaisResponse>> obtenerPorId(@PathVariable Long id) {
        PaisResponse pais = paisService.obtenerPorIdActivo(id);
        return ResponseEntity.ok(ApiResponse.exito("País encontrado", pais));
    }

    @PostMapping
    @Operation(
        summary = "Crear nuevo país",
        description = "Crea un nuevo país validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<PaisResponse>> crear(@Valid @RequestBody PaisRequest request) {
        PaisResponse nuevo = paisService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("País creado exitosamente", nuevo));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar país",
        description = "Actualiza los datos de un país existente."
    )
    public ResponseEntity<ApiResponse<PaisResponse>> actualizar(@PathVariable Long id,
                                                                @Valid @RequestBody PaisRequest request) {
        PaisResponse actualizado = paisService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("País actualizado correctamente", actualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar país",
        description = "Realiza el borrado lógico de un país en el sistema."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        paisService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("País desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar país",
        description = "Reactiva un país previamente desactivado."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        paisService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("País activado correctamente"));
    }

    @GetMapping("/admin")
    @Operation(
        summary = "Obtener todos los países (Admin)",
        description = "Retorna el historial completo de países (activos e inactivos) para administradores."
    )
    public ResponseEntity<ApiResponse<Page<PaisResponse>>> obtenerTodosAdmin(
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.exito("Historial completo de países", paisService.obtenerTodos(pageable))
        );
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener país por ID (Admin)",
        description = "Busca y retorna cualquier país (activo o inactivo) por su identificador único para administradores."
    )
    public ResponseEntity<ApiResponse<PaisResponse>> obtenerPorIdAdmin(@PathVariable Long id) {
        PaisResponse pais = paisService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.exito("País encontrado (admin)", pais));
    }
}
