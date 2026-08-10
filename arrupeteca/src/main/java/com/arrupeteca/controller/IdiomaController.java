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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/idiomas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Idiomas", description = "Endpoints para la gestión de idiomas")
public class IdiomaController {

    private final IdiomaService idiomaService;

    @GetMapping
    @Operation(
        summary = "Obtener idiomas activos(as) para dropdowns y autocompletado",
        description = "Retorna una lista no paginada de idiomas activos(as), con soporte opcional para filtrado por nombre."
    )
    public ResponseEntity<ApiResponse<List<IdiomaResponse>>> obtenerTodosActivos(
            @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de idiomas activos(as)", idiomaService.obtenerTodosActivos(nombre)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Obtener tabla de administración de idiomas",
        description = "Retorna una página de idiomas con soporte para filtros por nombre y estado borradoLogico."
    )
    public ResponseEntity<ApiResponse<Page<IdiomaResponse>>> obtenerTodos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean borradoLogico,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Tabla de idiomas", idiomaService.obtenerTodos(nombre, borradoLogico, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener idiomas por ID",
        description = "Busca la información de un(a) idioma por su ID sin importar su estado lógico."
    )
    public ResponseEntity<ApiResponse<IdiomaResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Idioma encontrado(a)", idiomaService.obtenerPorId(id)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nuevo(a) idioma",
        description = "Crea un(a) nuevo(a) idioma en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<IdiomaResponse>> crear(@Valid @RequestBody IdiomaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Idioma creado(a) exitosamente", idiomaService.crear(request)));
    }

    @PutMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar idioma",
        description = "Actualiza los campos de un(a) idioma existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<IdiomaResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody IdiomaRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Idioma actualizado(a) correctamente",
                idiomaService.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar idioma (Borrado lógico)",
        description = "Oculta el/la idioma en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        idiomaService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Idioma desactivado(a) correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar idioma",
        description = "Restaura la visibilidad pública de un(a) idioma previamente desactivado(a)."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        idiomaService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Idioma activado(a) correctamente"));
    }}
