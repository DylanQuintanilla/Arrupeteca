package com.arrupeteca.controller;

import com.arrupeteca.dto.request.EditorialRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.EditorialResponse;
import com.arrupeteca.service.EditorialService;
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
@RequestMapping("/api/v1/editoriales")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Editoriales", description = "Endpoints para la gestión de editoriales")
public class EditorialController {

    private final EditorialService editorialService;

    @GetMapping
    @Operation(
        summary = "Obtener editoriales activos(as) para dropdowns y autocompletado",
        description = "Retorna una lista no paginada de editoriales activos(as), con soporte opcional para filtrado por nombre."
    )
    public ResponseEntity<ApiResponse<List<EditorialResponse>>> obtenerTodosActivos(
            @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de editoriales activos(as)", editorialService.obtenerTodosActivos(nombre)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Obtener tabla de administración de editoriales",
        description = "Retorna una página de editoriales con soporte para filtros por nombre, correo, personaContacto y estado borradoLogico."
    )
    public ResponseEntity<ApiResponse<Page<EditorialResponse>>> obtenerTodos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String personaContacto,
            @RequestParam(required = false) Boolean borradoLogico,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Tabla de editoriales", editorialService.obtenerTodos(nombre, correo, personaContacto, borradoLogico, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener editoriales por ID",
        description = "Busca la información de un(a) editorial por su ID sin importar su estado lógico."
    )
    public ResponseEntity<ApiResponse<EditorialResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Editorial encontrado(a)", editorialService.obtenerPorId(id)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nuevo(a) editorial",
        description = "Crea un(a) nuevo(a) editorial en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<EditorialResponse>> crear(@Valid @RequestBody EditorialRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Editorial creado(a) exitosamente", editorialService.crear(request)));
    }

    @PutMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar editorial",
        description = "Actualiza los campos de un(a) editorial existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<EditorialResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody EditorialRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Editorial actualizado(a) correctamente",
                editorialService.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar editorial (Borrado lógico)",
        description = "Oculta el/la editorial en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        editorialService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Editorial desactivado(a) correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar editorial",
        description = "Restaura la visibilidad pública de un(a) editorial previamente desactivado(a)."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        editorialService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Editorial activado(a) correctamente"));
    }
}
