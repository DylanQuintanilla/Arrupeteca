# Estándar de Desarrollo: Entidades JPA (Arrupeteca)

Este documento establece las directrices y buenas prácticas definitivas para la capa de persistencia en Arrupeteca. Su objetivo es garantizar la consistencia entre el código Java y el motor de base de datos MySQL, optimizar el rendimiento y proteger la integridad del dominio de datos en entornos concurrentes.

---

## 1. Clasificación de Entidades

Para evitar el sobrediseño y optimizar el almacenamiento, dividimos las entidades en dos categorías:

| Tipo de Entidad | Características | Herencia | Bloqueo Optimista (`@Version`) | Ejemplo |
| :--- | :--- | :--- | :--- | :--- |
| **Transaccionales / Negocio** | Datos que sufren cambios frecuentes, inserciones constantes y concurrencia multi-usuario. | Extiende de `Auditable` | **Sí** (Campo `Long version`) | `Autor`, `Libro`, `EjemplarLibro` |
| **Catálogos / Maestros** | Datos de referencia estáticos o con cambios extremadamente poco frecuentes. | **No** (Sin auditoría) | **Opcional** (Solo si se requiere evitar sobrescrituras/Lost Updates) | `Pais` (Sin versión), `Edificio` (Con versión) |

---

## 2. Separación de Responsabilidades (Cero Validaciones Java)

Las entidades JPA actúan únicamente como un espejo de la estructura física de la base de datos (DDL).

*   **Prohibido**: Usar anotaciones de la especificación Jakarta Bean Validation (tales como `@NotBlank`, `@NotNull`, `@Size`, `@AssertTrue`, `@Pattern`, etc.) dentro de las clases de Entidad JPA.
*   **Enfoque Correcto**:
    *   La validación de formato, restricciones de longitud y reglas de lógica de negocio se realizan **exclusivamente a nivel de API** en los Request DTOs (ej. `AutorRequest.java`).
    *   La base de datos se protege mediante propiedades de columna físicas en JPA (ej. `@Column(nullable = false, length = 100)`).
*   **Ventaja**: Evita ejecuciones redundantes de validaciones en cada ciclo de persistencia de Hibernate, optimizando la CPU y flexibilizando las inserciones parciales en las pruebas de integración.

---

## 3. Lombok y Constructores Seguros

El uso de Lombok debe ser riguroso para evitar efectos secundarios indeseados en Hibernate:

*   **Getter y Setter**: Usar `@Getter` y `@Setter` a nivel de clase.
*   **Prohibición de `@Data`**: NUNCA usar `@Data` a nivel de entidad. Genera implementaciones de `equals()`, `hashCode()` y `toString()` implícitas que rompen el comportamiento Lazy de JPA y cargan relaciones no deseadas.
*   **Constructores para Builder**:
    *   `@NoArgsConstructor(access = AccessLevel.PROTECTED)`: Permite a Hibernate instanciar la entidad mediante reflexión, pero prohíbe que el desarrollador instancie entidades vacías e inconsistentes en la lógica de negocio.
    *   `@AllArgsConstructor(access = AccessLevel.PRIVATE)`: Restringe el constructor de todos los argumentos para forzar el uso exclusivo de `@Builder`.
    *   `@Builder`: Habilita la construcción fluida de objetos.
*   **`@EqualsAndHashCode` Segura**:
    *   Usar `@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)`.
    *   Anotar únicamente el campo `@Id` con `@EqualsAndHashCode.Include`.
    *   **Propósito**: Prevenir ciclos infinitos de mapeo y desbordamientos de pila (`StackOverflowError`) con entidades relacionadas en relaciones bidireccionales.
*   **`@ToString` Exclusivo**:
    *   Usar `@ToString(onlyExplicitlyIncluded = true)`.
    *   Anotar solo la clave primaria y atributos inmutables de identidad natural (ej. `nombre`, `seudonimo`).
    *   NUNCA incluir relaciones lazy ni campos de versión para evitar consultas adicionales accidentales a la base de datos.

---

## 4. Mapeo de Atributos y Relaciones

*   **Identificadores (`@Id`)**: Usar siempre `GenerationType.IDENTITY` para MySQL. Configurar `@Column(name = "id_entidad", updatable = false)`.
*   **Carga Perezosa (Lazy Loading)**: Todas las relaciones `@ManyToOne`, `@OneToOne`, `@OneToMany` y `@ManyToMany` deben configurarse explícitamente con `FetchType.LAZY` (ej. `@ManyToOne(fetch = FetchType.LAZY)`).
*   **Uso de Colecciones**: Usar la interfaz `Set` en lugar de `List` en relaciones ToMany para prevenir excepciones de carga múltiple (`MultipleBagFetchException`). Inicializar siempre la colección en el constructor o mediante `@Builder.Default`:
    ```java
    @Builder.Default
    @ManyToMany(mappedBy = "categorias", fetch = FetchType.LAZY)
    private Set<Obra> obras = new HashSet<>();
    ```
*   **Tipos de Datos**: Usar tipos nativos modernos de `java.time` (como `Year`, `LocalDate`, `LocalDateTime`).

---

## 6. Borrado Lógico (Soft Delete)

Para preservar la integridad histórica ante eliminaciones:
*   Anotar la entidad con `@SQLDelete(sql = "UPDATE tabla SET borrado_logico = 1 WHERE id_tabla = ?")`.
*   Definir el atributo `private boolean borradoLogico = false;` mapeado como `@Column(name = "borrado_logico", nullable = false, columnDefinition = "TINYINT DEFAULT 0")`.
*   Asegurar que el builder inicialice el estado por defecto anotándolo con `@Builder.Default`.

---

## 7. Configuración de Índices en la Base de Datos (`@Table(indexes = ...)`)

Para garantizar el rendimiento óptimo de las consultas sin depender de scripts SQL externos que se pueden perder durante `create-drop` o migraciones, **los índices deben definirse directamente en la entidad JPA**:

*   **Uso de `@Table(indexes = { @Index(...) })`**: Declarar la lista de índices en la anotación `@Table`.
*   **Índices Compuestos (Filtros Múltiples)**: Agrupar columnas que frecuentemente se consultan juntas en las cláusulas `WHERE` (ej. `borrado_logico` junto con claves foráneas o campos de estado).
    *   *Regla del Prefijo Izquierdo*: La columna más filtrada y constante (como `borrado_logico`) debe colocarse en la primera posición del índice compuesto.
*   **Índices Individuales (Búsquedas Textuales y Únicas)**: Crear índices individuales para columnas de texto buscadas por coincidencia parcial (`nombre`, `titulo`), ordenaciones (`ORDER BY`) o identificadores únicos (`isbn`).
*   **Límite Recomendado**: Definir entre **2 y 4 índices por entidad transaccional** para equilibrar la velocidad de lectura (`SELECT`) sin penalizar el rendimiento de inserción (`INSERT` / `UPDATE`).
*   *(Nota: Las tablas intermedias `@ManyToMany` son indexadas automáticamente por JPA en sus claves foráneas)*.

---

## 8. Plantilla Base Recomendada (Ejemplo de Autor)

```java
package com.arrupeteca.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Year;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Builder
@Table(name = "autor", indexes = {
    @Index(name = "idx_autor_busqueda", columnList = "borrado_logico, id_nacionalidad, id_pais_nacimiento, id_genero_principal, id_obra"),
    @Index(name = "idx_autor_nombre", columnList = "nombre, seudonimo")
})
@SQLDelete(sql = "UPDATE autor SET borrado_logico = 1 WHERE id_autor = ?")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Autor extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_autor", updatable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "nombre", length = 100)
    @ToString.Include
    private String nombre;

    @Column(name = "seudonimo", length = 50)
    @ToString.Include
    private String seudonimo;

    @Column(name = "anio_nacimiento")
    @ToString.Include
    private Year anioNacimiento;

    @Column(name = "anio_fallecimiento")
    @ToString.Include
    private Year anioFallecimiento;

    @Lob
    @Column(name = "biografia", nullable = false, columnDefinition = "TEXT")
    private String biografia;

    @Column(name = "borrado_logico", nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    @Builder.Default
    private boolean borradoLogico = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pais_nacimiento")
    private Pais paisNacimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nacionalidad")
    private Nacionalidad nacionalidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_genero_principal")
    private Genero generoPrincipal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_obra")
    private Obra obraCumbre;

}
```

---

## 9. Lista de Autoverificación (Checklist del Desarrollador)

Antes de dar por terminada la refactorización o creación de una nueva entidad, hágase las siguientes preguntas:

1. **¿Qué tipo de entidad es?**
   * *Si es Transaccional/Negocio*: ¿Extiende de `Auditable` y tiene el campo `@Version private Long version`?
   * *Si es Catálogo*: ¿Evita heredar de `Auditable`? Si requiere control de concurrencia, ¿tiene `@Version private Long version`? Si no, ¿está libre de `@Version`?
2. **¿Cero validaciones de Bean Validation (`jakarta.validation`)?**
   * ¿Se eliminaron anotaciones como `@NotBlank`, `@Size`, `@AssertTrue` de la entidad? ¿Se delegaron todas al respectivo `Request` DTO para evitar ejecuciones redundantes en Hibernate?
3. **¿La configuración de Lombok es segura?**
   * ¿Tiene `@NoArgsConstructor(access = AccessLevel.PROTECTED)` y `@AllArgsConstructor(access = AccessLevel.PRIVATE)` junto con `@Builder`?
   * ¿Tiene `@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)` incluyendo únicamente el atributo `@Id` marcado con `@EqualsAndHashCode.Include`?
   * ¿El `@ToString` tiene `onlyExplicitlyIncluded = true` y excluye relaciones perezosas (lazy)?
4. **¿Se configuró correctamente la perezosidad (Lazy Loading)?**
   * ¿Toda relación `@ManyToOne`, `@OneToOne`, `@OneToMany` o `@ManyToMany` tiene configurado explícitamente `fetch = FetchType.LAZY`?
5. **¿Las colecciones de relaciones ToMany son seguras?**
   * ¿Están definidas como `Set` (en vez de `List`) para evitar la excepción de carga múltiple `MultipleBagFetchException`?
   * ¿Están marcadas con `@Builder.Default` e inicializadas por defecto (ej. `= new HashSet<>()`)?
6. **¿El borrado lógico está bien estructurado?**
   * ¿Tiene `@SQLDelete(sql = "UPDATE ...")`?
   * ¿El campo `borradoLogico` está anotado con `@Builder.Default` y configurado con la definición de columna `TINYINT DEFAULT 0`?
7. **¿Se configuraron los índices en `@Table(indexes = ...)`?**
   * En entidades transaccionales de negocio, ¿se declararon los índices compuestos (filtros frecuentes `borrado_logico` + FKs) e individuales (búsquedas por texto u ordenación) directamente en la anotación `@Table`?

---
*Este estándar garantiza la estabilidad, rendimiento e integridad de datos en el dominio de Arrupeteca.*
