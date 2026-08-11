# Estándar de Desarrollo: Controladores Spring Boot (Arrupeteca)

Este documento establece las directrices, convenciones y mejores prácticas para el diseño e implementación de la capa de **Controladores (Controladores REST)** en Arrupeteca.

---

## 1. Configuración de Clase

Todo controlador REST de Arrupeteca debe seguir las siguientes reglas de anotación y configuración:

*   **`@RestController`**: Registra la clase como un controlador de tipo REST que retorna respuestas en formato JSON.
*   **`@RequestMapping("/api/v1/recurso")`**: Define el prefijo de la ruta del API. Debe seguir la convención `/api/v1/` seguido del nombre del recurso en plural (ej. `/api/v1/autores`, `/api/v1/obras`, `/api/v1/libros`).
*   **`@RequiredArgsConstructor`**: Inyecta los servicios colaboradores de forma automática por constructor vía Lombok (los colaboradores deben declararse como `private final`).
*   **`@Tag(name = "Recurso", description = "...")`**: Anotación de OpenAPI obligatoria para agrupar y describir los endpoints de este controlador en la UI de Swagger.

---

## 2. Semántica HTTP y Estructura de Endpoints (Estructura con Seguridad `/admin`)

Las clases de negocio (`AutorController`, `ObraController`, `LibroController`, `EjemplarLibroController`) estructuran sus endpoints asegurando que **únicamente las búsquedas públicas permanezcan en las rutas raíz**, mientras que **todas las operaciones de administración incluyen el prefijo `/admin`** para ser protegidas automáticamente por Spring Security:

| Método HTTP | Ruta relativa | Acción REST | Alcance de Permisos | Formato de Retorno |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/` | Búsqueda global pública activa y autocompletes. | Público (`permitAll`) | `ApiResponse<Page<[Entidad]Resumen>>` |
| **GET** | `/{id}` | Obtener detalle público activo. | Público (`permitAll`) | `ApiResponse<[Entidad]DetalleResponse>` |
| **GET** | `/admin/all` | Búsqueda y tabla de administración. | Admin (`hasRole('ADMIN')`) | `ApiResponse<Page<[Entidad]Resumen>>` |
| **GET** | `/admin/{id}` | Obtener detalle por ID (Admin). | Admin (`hasRole('ADMIN')`) | `ApiResponse<[Entidad]DetalleResponse>` |
| **GET** | `/admin/verificar-duplicados` | Verificar registros similares. | Admin (`hasRole('ADMIN')`) | `ApiResponse<List<[Entidad]Resumen>>` |
| **POST** | `/admin` | Crear nuevo registro. | Admin (`hasRole('ADMIN')`) | `ApiResponse<[Entidad]DetalleResponse>` |
| **PATCH** | `/admin/{id}` | Actualizar parcial con `JsonNullable`. | Admin (`hasRole('ADMIN')`) | `ApiResponse<[Entidad]DetalleResponse>` |
| **DELETE** | `/admin/{id}` | Desactivar (Borrado lógico `borradoLogico = true`). | Admin (`hasRole('ADMIN')`) | `ApiResponse<Void>` |
| **PATCH** | `/admin/{id}/activar` | Activar registro (`borradoLogico = false`). | Admin (`hasRole('ADMIN')`) | `ApiResponse<Void>` |

*Notas sobre ordenamiento por defecto (`@PageableDefault`)*:
*   `AutorController`: `@PageableDefault(size = 20, sort = "nombre")` (Alfabético A-Z por nombre).
*   `ObraController`: `@PageableDefault(size = 20, sort = "titulo")` (Alfabético A-Z por título).
*   `LibroController`: `@PageableDefault(size = 20, sort = "obra.titulo")` (Alfabético A-Z por título de la obra asociada).
*   `EjemplarLibroController`: `@PageableDefault(size = 20, sort = {"libro.obra.titulo", "id"})` (Alfabético A-Z por obra y luego por ID correlativo del ejemplar).

---

## 3. Autodocumentación Limpia con Springdoc OpenAPI

Para mantener el código fuente limpio y libre de colisiones de nombres con DTOs locales (como nuestra clase `ApiResponse`):

*   **Uso de `@Operation`**: Cada endpoint debe estar documentado únicamente con `@Operation(summary = "...", description = "...")` para describir de forma clara la acción.
*   **Prohibido**: Utilizar anotaciones `@ApiResponse` personalizadas en cada método.
*   **Autodetección**: Springdoc OpenAPI lee de forma inteligente el tipo de retorno genérico (ej. `ResponseEntity<ApiResponse<DTO>>`) y genera automáticamente en el Swagger UI el esquema JSON y los códigos de éxito.
*   **Paginación con `@ParameterObject`**: Todo endpoint que reciba un parámetro `Pageable` debe anotarlo obligatoriamente con `@ParameterObject` y `@PageableDefault(size = 20, sort = "nombre")`. Esto permite que Springdoc exponga de forma plana los parámetros de consulta (`page`, `size`, `sort`) en la documentación de Swagger UI en lugar de un objeto anidado complejo, además de definir los valores por defecto del sistema.

---

## 4. Plantilla Maestra de Referencia (AutorController)

```java
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/api/v1/autores")
@RequiredArgsConstructor
@Tag(name = "Autores", description = "Endpoints para la gestión de autores de obras literarias")
public class AutorController {

    private final AutorService autorService;

    @GetMapping
    @Operation(
        summary = "Búsqueda global de autores activos",
        description = "Retorna una página de autores activos permitiendo filtrar por término (nombre/seudónimo), período de vida, país, nacionalidad, género u obra cumbre."
    )
    public ResponseEntity<ApiResponse<Page<AutorResumenResponse>>> busquedaGlobal(
            @RequestParam(required = false) Long idNacionalidad,
            @RequestParam(required = false) Long idPais,
            @RequestParam(required = false) Long idGenero,
            @RequestParam(required = false) Long idObraCumbre,
            @RequestParam(required = false) Year anioInicio,
            @RequestParam(required = false) Year anioFin,
            @RequestParam(required = false) String termino,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados de búsqueda pública de autores activos",
                autorService.busquedaGlobal(idNacionalidad, idPais, idGenero, idObraCumbre, anioInicio, anioFin, termino, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener detalle de autor activo",
        description = "Busca la información detallada de un autor por su ID únicamente si está activo."
    )
    public ResponseEntity<ApiResponse<AutorDetalleResponse>> busquedaGlobalPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Autor activo encontrado", autorService.busquedaGlobalPorId(id)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Búsqueda de administración de autores",
        description = "Búsqueda completa multi-filtro por borrado lógico, país, nacionalidad, género, obra cumbre, período de vida y término de coincidencia (solo admin)."
    )
    public ResponseEntity<ApiResponse<Page<AutorResumenResponse>>> busquedaAdmin(
            @RequestParam(required = false) Boolean borradoLogico,
            @RequestParam(required = false) Long idNacionalidad,
            @RequestParam(required = false) Long idPais,
            @RequestParam(required = false) Long idGenero,
            @RequestParam(required = false) Long idObraCumbre,
            @RequestParam(required = false) Year anioInicio,
            @RequestParam(required = false) Year anioFin,
            @RequestParam(required = false) String termino,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados de búsqueda de administración de autores",
                autorService.busquedaAdmin(borradoLogico, idNacionalidad, idPais, idGenero, idObraCumbre,
                        anioInicio, anioFin, termino, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener autor por ID (Admin)",
        description = "Busca el detalle de un autor por su ID sin importar su estado de borrado lógico (solo admin)."
    )
    public ResponseEntity<ApiResponse<AutorDetalleResponse>> busquedaAdminPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Autor encontrado (admin)", autorService.busquedaAdminPorId(id)));
    }

    @GetMapping("/admin/verificar-duplicados")
    @Operation(
        summary = "Verificar duplicados (Admin)",
        description = "Busca autores con nombres o seudónimos similares en el sistema (solo admin)."
    )
    public ResponseEntity<ApiResponse<List<AutorResumenResponse>>> buscarDuplicados(
            @RequestParam String termino,
            @RequestParam(required = false) Long idExcluir) {
        return ResponseEntity.ok(ApiResponse.exito("Verificación completada", autorService.buscarDuplicados(termino, idExcluir)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nuevo autor (Admin)",
        description = "Crea un nuevo autor en el sistema validando la unicidad del nombre (solo admin)."
    )
    public ResponseEntity<ApiResponse<AutorDetalleResponse>> crear(@Valid @RequestBody AutorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Autor creado exitosamente", autorService.crear(request)));
    }

    @PatchMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar autor (Admin)",
        description = "Actualiza los campos de un autor existente y valida su versión de bloqueo optimista (solo admin)."
    )
    public ResponseEntity<ApiResponse<AutorDetalleResponse>> actualizar(@PathVariable Long id, @Valid @RequestBody AutorRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Autor actualizado correctamente", autorService.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar autor (Admin)",
        description = "Oculta el autor en el catálogo público cambiando su estado a borrado (solo admin)."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        autorService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Autor desactivado correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar autor (Admin)",
        description = "Restaura la visibilidad pública de un autor previamente desactivado (solo admin)."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        autorService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Autor activado correctamente"));
    }
}
```

---

## 5. Lista de Autoverificación (Checklist del Desarrollador)

1. **¿El mapeo de rutas es correcto?**
   * ¿Únicamente los 2 primeros endpoints son públicos (`GET /` y `GET /{id}`)?
   * ¿Todos los demás endpoints administrativos tienen el prefijo `/admin` para alinearse con `SecurityConfig`?
2. **¿Se inyectan las dependencias por constructor?**
   * ¿Están todos los servicios declarados como `private final` y la clase anotada con `@RequiredArgsConstructor`?
3. **¿Se validan las peticiones de entrada?**
   * ¿Tienen los parámetros `@RequestBody` anotado `@Valid` al lado para habilitar las validaciones del DTO?
4. **¿Usa la sintaxis fluida de ResponseEntity en la creación?**
   * En el método POST, ¿se retorna `ResponseEntity.status(HttpStatus.CREATED).body(...)`?
5. **¿Autodocumentación limpia y libre de colisiones?**
   * ¿Se incluyó `@Tag` a nivel de clase y `@Operation` en cada método?
   * ¿Se evitaron anotaciones `@ApiResponse` redundantes para dejar que Springdoc infiera el esquema automáticamente?
   * ¿Los parámetros de tipo `Pageable` tienen anotados `@ParameterObject` y `@PageableDefault`?
