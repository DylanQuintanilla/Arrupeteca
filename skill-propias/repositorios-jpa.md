# Estándar de Desarrollo: Repositorios y Especificaciones JPA (Arrupeteca)

Este documento establece las reglas para la capa de acceso a datos, enfocándose en la eficiencia, el filtrado dinámico y el manejo de borrado lógico.

## 1. Repositorios (AutorRepository Pattern)

### 1.1. Estructura y Herencia
Todo repositorio debe extender de las siguientes interfaces:
*   `JpaRepository<Entity, ID>`: Para operaciones CRUD estándar.
*   `JpaSpecificationExecutor<Entity>`: Para permitir el uso de filtros dinámicos (Specifications).

### 1.2. Optimización con EntityGraph
Para evitar el problema de N+1 consultas, es obligatorio definir la carga de relaciones en los métodos de lectura:
*   Usar `@EntityGraph(attributePaths = {"relacion1", "relacion2"})`.
*   Aplicar tanto en `findById` como en los métodos de búsqueda personalizados.

### 1.3. Métodos de Búsqueda y Consultas (@Query)
*   **Optional**: Siempre usar `Optional<Entity>` para métodos que devuelven un solo resultado por ID o atributo único.
*   **JPQL**: Preferido para lógica compleja que involucre múltiples campos o condiciones de negocio.
*   **Native Query**: Reservado para operaciones de alto rendimiento o que dependan de funciones específicas de MySQL (ej. actualizaciones masivas o conteos complejos).
*   **Ordenamiento**: Toda consulta que devuelva listas o páginas debe incluir un `ORDER BY` explícito para garantizar resultados predecibles.
*   **Parámetros**: Usar `@Param("nombre")` para claridad, especialmente en consultas multilínea.

### 1.4. Interfaz Limpia
*   No declarar métodos redundantes. Por ejemplo, no incluir `findAll(Pageable)` si ya existe `findAll(Specification, Pageable)`, ya que pasar `null` como especificación tiene el mismo efecto y mantiene el uso del `@EntityGraph` definido en el método de Specification.

---

## 2. Especificaciones (AutorSpecification Pattern)

Las `Specifications` permiten construir consultas dinámicas de forma tipada y reutilizable.

### 2.1. Estructura de Clase
*   Debe ser una clase de utilidad en el paquete `.persistence.specification`.
*   Nombre: `[Entidad]Specification`.
*   Constructor privado para evitar instanciación.

### 2.2. Patrones de Filtrado Comunes
Cada método debe ser estático y devolver un `Specification<Entity>`:

*   **Borrado Lógico**: `esBorrado(Boolean)`. Si es `null`, filtrar por `false` por defecto.
*   **Relaciones (Joins)**: `tiene[Entidad](Long id)`. Usar `JoinType.LEFT` para evitar excluir registros si la relación es opcional.
*   **Búsqueda por Término**: `contieneTermino(String)`. Realizar `like` sobre los campos descriptivos (nombre, código, etc.).
*   **Rangos**: `[campo]Entre(Tipo inicio, Tipo fin)`. Manejar nulabilidad de ambos extremos.

### 2.3. Orquestadores (Filtros Maestros)
Es una buena práctica crear métodos que combinen múltiples filtros para casos de uso específicos:
*   `filtroMaestroAdmin`: Combina filtros de estado, relaciones y términos.
*   `filtroBusquedaPublica`: Fuerza filtros de seguridad (ej. solo no borrados).

---

## 3. Manejo de Borrado Lógico en el Repositorio

A falta de `@SQLRestriction` global, el repositorio debe gestionar el estado de forma explícita cuando sea necesario:
*   **Desactivar/Activar**: Usar métodos con `@Modifying` y `@Query` nativo para actualizar el campo `borrado_logico`.
*   **Existencia**: Crear `existsByIdIncludeDeleted` mediante consulta nativa si se requiere verificar la existencia física de un registro ignorando el filtro de borrado lógico.

---
*Este estándar garantiza que la lógica de filtrado sea desacoplada de la base de datos y altamente reutilizable.*
