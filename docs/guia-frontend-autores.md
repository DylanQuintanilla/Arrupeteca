# Guía de Integración Frontend: Módulo de Autores (`/api/v1/autores`)

Documentación técnica y especificación completa del contrato de API para la integración del módulo de **Autores** en el cliente web Frontend (React, Next.js, Shadcn UI, Vue, Angular).

---

## 1. Visión General y Protocolo de Comunicación

* **URL Base del Módulo**: `http://localhost:8080/api/v1/autores`
* **Formato de Peticiones y Respuestas**: `application/json`
* **Cabecera de Autenticación**:
  ```http
  Authorization: Bearer <JWT_TOKEN>
  ```

### Roles y Control de Acceso

* **Público Anónimo (Sin Token)**:
  * `GET /api/v1/autores`: **Búsqueda pública y Autocomplete de Autores** (`busquedaGlobal`). Permite a cualquier visitante explorar o buscar autores activos mediante coincidencias parciales por término en su **nombre** o **seudónimo** (`?termino=...`).
* **Usuario Autenticado (Con Token JWT)**:
  * `GET /api/v1/autores/{id}`: **Consulta de Ficha/Detalle de Autor Activo** (`busquedaGlobalPorId`). Requiere estar registrado e iniciar sesión.
* **Rol `ADMIN` (Requiere Token JWT con Rol de Administrador)**:
  * Todos los endpoints bajo `/admin` (`/api/v1/autores/admin/*`): Creación (`POST`), Actualización parcial (`PATCH`), Desactivación (`DELETE`), Reactivación (`PATCH /activar`), Búsqueda Maestra de Administración (`GET /admin/all`), Detalle Administrativo (`GET /admin/{id}`) y Verificación de Duplicados (`GET /admin/verificar-duplicados`).

---

## 2. Directorio de Endpoints y Utilidad Frontend

Base Path: `/api/v1/autores`

| Método | Endpoint | Rol Requerido | Propósito / Utilidad Frontend |
|---|---|---|---|
| `GET` | `/api/v1/autores` | **Público (Sin Token)** | **Autocomplete y Búsqueda Pública (`busquedaGlobal`)**. Retorna una página de autores activos (`borradoLogico = false`). Diseñado para alimentar buscadores en vivo al escribir (`?termino=...`) o desplegar el catálogo de autores con filtros de país, nacionalidad, género y años. |
| `GET` | `/api/v1/autores/{id}` | Autenticado (JWT) | **Ficha Técnica / Detalle de Autor Activo (`busquedaGlobalPorId`)**. Retorna la información detallada y biografía de un autor activo. |
| `GET` | `/api/v1/autores/admin/all` | `ADMIN` | **Búsqueda de Administración (`busquedaAdmin`)**. Permite consultar autores activos e inactivos mediante múltiples filtros (incluyendo el filtro por `borradoLogico`). |
| `GET` | `/api/v1/autores/admin/{id}` | `ADMIN` | **Detalle Administrativo (`busquedaAdminPorId`)**. Retorna el detalle completo de un autor sin importar su estado de borrado lógico. |
| `GET` | `/api/v1/autores/admin/verificar-duplicados` | `ADMIN` | **Verificación de Duplicados**. Busca coincidencias de nombres o seudónimos similares antes de guardar para prevenir registros duplicados. |
| `POST` | `/api/v1/autores/admin` | `ADMIN` | **Registrar Nuevo Autor (`crear`)**. Valida que el autor contenga al menos Nombre o Seudónimo, y una Biografía no vacía. |
| `PATCH` | `/api/v1/autores/admin/{id}` | `ADMIN` | **Actualizar Autor (`actualizar`)**. Permite edición parcial mediante protocolo `JsonNullable` y control de concurrencia optimista (`version`). |
| `DELETE` | `/api/v1/autores/admin/{id}` | `ADMIN` | **Desactivar Autor (Borrado Lógico)**. Pasa `borradoLogico = true` para ocultar al autor de las búsquedas públicas. |
| `PATCH` | `/api/v1/autores/admin/{id}/activar` | `ADMIN` | **Activar Autor**. Restaura la visibilidad pública de un autor desactivado (`borradoLogico = false`). |

---

## 3. Especificación del Modelo de Petición (`AutorRequest`)

Para operaciones `POST` (Creación) y `PATCH` (Actualización), el cliente Frontend envía la estructura `AutorRequest`.

### Tabla de Campos, Tipos y Reglas de Validación

| Campo | Tipo JSON | Obligatorio en `POST` | Obligatorio en `PATCH` | Reglas de Validación y Restricciones |
|---|---|---|---|---|
| `nombre` | `String` | **REQUERIDO** *(Si no se envía `seudonimo`)* | Opcional | Máximo 100 caracteres. No puede estar vacío si se envía en el JSON. Debe enviarse al menos `nombre` o `seudonimo`. |
| `seudonimo` | `String` | **REQUERIDO** *(Si no se envía `nombre`)* | Opcional | Máximo 50 caracteres. No puede estar vacío si se envía en el JSON. |
| `biografia` | `String` | **SÍ (Obligatorio)** | Opcional | Texto de la biografía. Máximo 5000 caracteres. No puede estar vacío en creación. |
| `anioNacimiento` | `Integer` / `Year` | NO (Opcional) | Opcional | Año de nacimiento (máx. 4 dígitos, ej. `1927`). No puede ser un año futuro. |
| `anioFallecimiento` | `Integer` / `Year` | NO (Opcional) | Opcional | Año de fallecimiento. No puede ser un año futuro. Debe ser mayor o igual a `anioNacimiento`. |
| `idPaisNacimiento` | `Long` | NO (Opcional) | Opcional | ID del País de nacimiento existente (debe ser $> 0$). Se puede desasociar enviando `null` en `PATCH`. |
| `idNacionalidad` | `Long` | NO (Opcional) | Opcional | ID de la Nacionalidad existente (debe ser $> 0$). Se puede desasociar enviando `null` en `PATCH`. |
| `idGeneroPrincipal` | `Long` | NO (Opcional) | Opcional | ID del Género literario principal (debe ser $> 0$). Se puede desasociar enviando `null` en `PATCH`. |
| `idObraCumbre` | `Long` | NO (Opcional) | Opcional | ID de la Obra literaria cumbre (debe ser $> 0$). Se puede desasociar enviando `null` en `PATCH`. |
| `version` | `Long` | NO | **REQUERIDO** | Número de versión para control de concurrencia optimista. Debe enviarse el valor exacto recibido en la respuesta `GET` previa. |

---

## 4. Mínimos Requeridos para Crear un Autor (`POST /api/v1/autores/admin`)

### 4.1 Ejemplo Mínimo Solo con Nombre
```json
{
  "nombre": "Gabriel García Márquez",
  "biografia": "Escritor y periodista colombiano, uno de los autores más significativos del siglo XX."
}
```

### 4.2 Ejemplo Mínimo Solo con Seudónimo
```json
{
  "seudonimo": "Mark Twain",
  "biografia": "Escritor, humorista y conferenciante estadounidense."
}
```

### 4.3 Ejemplo Completo con Relaciones
```json
{
  "nombre": "Gabriel García Márquez",
  "seudonimo": "Gabo",
  "anioNacimiento": 1927,
  "anioFallecimiento": 2014,
  "biografia": "Premio Nobel de Literatura en 1982. Famoso por su obra Cien años de soledad.",
  "idPaisNacimiento": 1,
  "idNacionalidad": 1,
  "idGeneroPrincipal": 3,
  "idObraCumbre": 10
}
```

---

## 5. Protocolo PATCH (`JsonNullable`) y Bloqueo Optimista (`version`)

El endpoint `PATCH /api/v1/autores/admin/{id}` utiliza la librería `JsonNullable` para permitir actualización parcial segura:

### Reglas de Envío de Propiedades en `PATCH`:
1. **Omitir una clave del JSON**: La propiedad conserva su valor actual en la base de datos sin cambios.
2. **Enviar una clave con un nuevo valor**: Actualiza la propiedad en la base de datos.
3. **Enviar una clave con `null` explícito**: Limpia o desasocia la relación en la base de datos.

### 5.1 Ejemplo: Desasociar la Obra Cumbre y Actualizar Seudónimo
```json
{
  "version": 1,
  "seudonimo": "Gabo Márquez",
  "idObraCumbre": null
}
```
> **Nota**: El campo `"version"` es **estrictamente requerido** en cada petición `PATCH`. Si la versión enviada no coincide con la versión actual de la BD, el servidor devolverá HTTP 400 Bad Request: `"El registro fue modificado por otro usuario. Por favor recargue la página."`.

---

## 6. Verificación de Duplicados (`GET /api/v1/autores/admin/verificar-duplicados`)

Para ofrecer una excelente experiencia de usuario (UX) en formularios de creación o edición, el Frontend puede verificar en tiempo real si ya existen autores registrados con un nombre o seudónimo similar.

* **URL**: `GET /api/v1/autores/admin/verificar-duplicados?termino=garcia`
* **Parámetros Query**:
  * `termino` (String, Obligatorio): Texto a buscar en nombres o seudónimos.
  * `idExcluir` (Long, Opcional): ID del autor actual si se está editando, para evitar que el mismo registro cuente como duplicado.
* **Respuesta**: Devuelve una lista plana `ApiResponse<List<AutorResumenResponse>>` con las coincidencias encontradas.

```http
GET /api/v1/autores/admin/verificar-duplicados?termino=garcia&idExcluir=1
```

---

## 7. Modelos de Respuesta de la API (JSON Schemas)

### 7.1 Estructura Estándar de Respuesta (`ApiResponse<T>`)
```json
{
  "exito": true,
  "mensaje": "Mensaje informativo de la operación",
  "datos": { ... }
}
```

### 7.2 Objeto Resumen de Autor (`AutorResumenResponse`)
Se utiliza en los elementos de `content` de las respuestas paginadas (`busquedaGlobal` y `busquedaAdmin`):

```json
{
  "id": 1,
  "nombre": "Gabriel García Márquez",
  "seudonimo": "Gabo",
  "anioNacimiento": 1927,
  "anioFallecimiento": 2014,
  "borradoLogico": false,
  "nombrePais": "Colombia",
  "nombreNacionalidad": "Colombiana",
  "nombreGeneroPrincipal": "Realismo Mágico",
  "nombreObraCumbre": "Cien Años de Soledad",
  "biografiaResumida": "Escritor y periodista colombiano, galardonado con el Premio Nobel de Literatura..."
}
```

### 7.3 Objeto Detalle Completo de Autor (`AutorDetalleResponse`)
Se utiliza en las respuestas por ID (`GET /autores/{id}`), creación (`POST`) y actualización (`PATCH`):

```json
{
  "exito": true,
  "mensaje": "Autor activo encontrado",
  "datos": {
    "id": 1,
    "nombre": "Gabriel García Márquez",
    "seudonimo": "Gabo",
    "anioNacimiento": 1927,
    "anioFallecimiento": 2014,
    "biografia": "Gabriel José de la Concordia García Márquez fue un escritor y periodista colombiano...",
    "version": 1,
    "borradoLogico": false,
    "creadoPor": "admin@arrupeteca.com",
    "fechaCreacion": "2026-08-10T10:00:00",
    "actualizadoPor": "admin@arrupeteca.com",
    "fechaActualizacion": "2026-08-12T15:30:00",
    "paisNacimiento": { "id": 1, "nombre": "Colombia" },
    "nacionalidad": { "id": 1, "nombre": "Colombiana" },
    "generoPrincipal": { "id": 3, "nombre": "Realismo Mágico" },
    "obraCumbre": { "id": 10, "titulo": "Cien Años de Soledad" }
  }
}
```

---

## 8. Parámetros de Búsqueda y Paginación

### 8.1 Búsqueda Pública (`GET /api/v1/autores`) - **PÚBLICO**
* `termino` (String): Búsqueda parcial por **nombre** o **seudónimo**.
* `idNacionalidad` (Long): Filtra por ID de Nacionalidad.
* `idPais` (Long): Filtra por ID de País de nacimiento.
* `idGenero` (Long): Filtra por ID de Género principal.
* `idObraCumbre` (Long): Filtra por ID de Obra cumbre.
* `anioInicio` (Year / Integer): Filtra autores que nacieron desde este año.
* `anioFin` (Year / Integer): Filtra autores que fallecieron hasta este año.
* Paginación: `page` (def `0`), `size` (def `20`), `sort` (def `nombre`).

### 8.2 Búsqueda Admin (`GET /api/v1/autores/admin/all`) - **`ADMIN`**
Incluye todos los filtros públicos más:
* `borradoLogico` (Boolean): `true` (solo inactivos), `false` (solo activos), omitido (todos los registros).

---

## 9. Ejemplos de Implementación Frontend (TypeScript / Fetch)

```typescript
// Interfaces TypeScript
export interface AutorRequest {
  nombre?: string;
  seudonimo?: string;
  biografia?: string;
  anioNacimiento?: number;
  anioFallecimiento?: number;
  idPaisNacimiento?: number | null;
  idNacionalidad?: number | null;
  idGeneroPrincipal?: number | null;
  idObraCumbre?: number | null;
  version?: number;
}

export interface Option {
  label: string;
  value: number;
}

// 1. Autocomplete Asíncrono de Autores al escribir (PÚBLICO)
export async function searchAutoresAutocomplete(query: string, limit = 10): Promise<Option[]> {
  const params = new URLSearchParams({
    termino: query.trim(),
    page: '0',
    size: limit.toString()
  });

  const res = await fetch(`/api/v1/autores?${params.toString()}`);
  const json = await res.json();

  if (json.exito && json.datos?.content) {
    return json.datos.content.map((autor: { id: number; nombre: string; seudonimo?: string }) => ({
      label: autor.seudonimo ? `${autor.nombre} ("${autor.seudonimo}")` : autor.nombre,
      value: autor.id
    }));
  }
  return [];
}

// 2. Obtener Detalle de Autor por ID (Requiere JWT)
export async function fetchAutorPorId(id: number, token: string) {
  const res = await fetch(`/api/v1/autores/${id}`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  const json = await res.json();
  if (json.exito) {
    return json.datos;
  }
  throw new Error(json.mensaje || 'No se encontró el autor');
}

// 3. Crear Autor (Admin - Requiere JWT)
export async function crearAutor(data: AutorRequest, token: string) {
  const res = await fetch('/api/v1/autores/admin', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(data)
  });
  return await res.json();
}

// 4. Actualizar Autor con JsonNullable y Version Lock (Admin - Requiere JWT)
export async function actualizarAutor(id: number, patchData: AutorRequest, token: string) {
  const res = await fetch(`/api/v1/autores/admin/${id}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(patchData)
  });
  return await res.json();
}

// 5. Verificar Duplicados al Tipear en Formulario (Admin)
export async function verificarDuplicadosAutor(termino: string, idExcluir?: number, token?: string) {
  const params = new URLSearchParams({ termino });
  if (idExcluir) params.append('idExcluir', idExcluir.toString());

  const res = await fetch(`/api/v1/autores/admin/verificar-duplicados?${params.toString()}`, {
    headers: token ? { 'Authorization': `Bearer ${token}` } : {}
  });
  return await res.json();
}

// 6. Desactivar Autor (Borrado Lógico - Admin)
export async function desactivarAutor(id: number, token: string) {
  const res = await fetch(`/api/v1/autores/admin/${id}`, {
    method: 'DELETE',
    headers: { 'Authorization': `Bearer ${token}` }
  });
  return await res.json();
}

// 7. Activar Autor (Admin)
export async function activarAutor(id: number, token: string) {
  const res = await fetch(`/api/v1/autores/admin/${id}/activar`, {
    method: 'PATCH',
    headers: { 'Authorization': `Bearer ${token}` }
  });
  return await res.json();
}
```

---

## 10. Endpoints de Catálogos para Comboboxes (Países, Nacionalidades, Géneros y Obras)

Para alimentar los elementos de selección dinámica (Comboboxes, Selects y Autocomplete) dentro del formulario de registro y edición de Autores, el backend expone los siguientes endpoints:

### Tabla de Endpoints de Catálogos para Formulario de Autor

| Catálogo / Entidad | Endpoint `GET` | Estructura de Respuesta | Rol Requerido | Campo en `AutorRequest` |
|---|---|---|---|---|
| **Países** | `GET /api/v1/paises` | `ApiResponse<List<PaisResponse>>` *(Lista plana)* | Autenticado *(Requiere JWT)* | `idPaisNacimiento` |
| **Nacionalidades** | `GET /api/v1/nacionalidades` | `ApiResponse<List<NacionalidadResponse>>` *(Lista plana)* | Autenticado *(Requiere JWT)* | `idNacionalidad` |
| **Géneros Literarios** | `GET /api/v1/generos` | `ApiResponse<List<GeneroResponse>>` *(Lista plana)* | **Público (Sin Token)** | `idGeneroPrincipal` |
| **Obras Literarias** | `GET /api/v1/obras/admin/all` | `ApiResponse<Page<ObraResumen>>` *(Paginado)* | `ADMIN` *(Requiere JWT)* | `idObraCumbre` |

---

### Ejemplos de Carga de Comboboxes en TypeScript / React

```typescript
// A. Cargar Países para Combobox (idPaisNacimiento) - Requiere JWT
export async function fetchPaisesOptions(token: string): Promise<Option[]> {
  const res = await fetch('/api/v1/paises', {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  const json = await res.json();
  if (json.exito && Array.isArray(json.datos)) {
    return json.datos.map((pais: { id: number; nombre: string }) => ({
      label: pais.nombre,
      value: pais.id
    }));
  }
  return [];
}

// B. Cargar Nacionalidades para Combobox (idNacionalidad) - Requiere JWT
export async function fetchNacionalidadesOptions(token: string): Promise<Option[]> {
  const res = await fetch('/api/v1/nacionalidades', {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  const json = await res.json();
  if (json.exito && Array.isArray(json.datos)) {
    return json.datos.map((nac: { id: number; nombre: string }) => ({
      label: nac.nombre,
      value: nac.id
    }));
  }
  return [];
}

// C. Cargar Géneros Principales para Combobox (idGeneroPrincipal) - PÚBLICO
export async function fetchGenerosOptions(): Promise<Option[]> {
  const res = await fetch('/api/v1/generos');
  const json = await res.json();
  if (json.exito && Array.isArray(json.datos)) {
    return json.datos.map((gen: { id: number; nombre: string }) => ({
      label: gen.nombre,
      value: gen.id
    }));
  }
  return [];
}

// D. Autocomplete Asíncrono de Obras para Obra Cumbre (idObraCumbre) - Admin (Requiere JWT)
export async function searchObrasAsync(query: string, token: string, limit = 10): Promise<Option[]> {
  const params = new URLSearchParams({
    termino: query.trim(),
    page: '0',
    size: limit.toString()
  });

  const res = await fetch(`/api/v1/obras/admin/all?${params.toString()}`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  const json = await res.json();

  if (json.exito && json.datos?.content) {
    return json.datos.content.map((obra: { id: number; titulo: string }) => ({
      label: obra.titulo,
      value: obra.id
    }));
  }
  return [];
}
```
