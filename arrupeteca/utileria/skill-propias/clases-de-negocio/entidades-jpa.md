# Est├índar de Desarrollo: Entidades JPA (Arrupeteca)

Este documento establece las directrices y buenas pr├ícticas definitivas para la capa de persistencia en Arrupeteca. Su objetivo es garantizar la consistencia entre el c├│digo Java y el motor de base de datos MySQL, optimizar el rendimiento y proteger la integridad del dominio de datos en entornos concurrentes.

---

## 1. Clasificaci├│n de Entidades

Para evitar el sobredise├▒o y optimizar el almacenamiento, dividimos las entidades en dos categor├¡as:

| Tipo de Entidad | Caracter├¡sticas | Herencia | Bloqueo Optimista (`@Version`) | Ejemplo |
| :--- | :--- | :--- | :--- | :--- |
| **Transaccionales / Negocio** | Datos que sufren cambios frecuentes, inserciones constantes y concurrencia multi-usuario. | Extiende de `Auditable` | **S├¡** (Campo `Long version`) | `Autor`, `Libro`, `EjemplarLibro` |
| **Cat├ílogos / Maestros** | Datos de referencia est├íticos o con cambios extremadamente poco frecuentes. | **No** (Sin auditor├¡a) | **Opcional** (Solo si se requiere evitar sobrescrituras/Lost Updates) | `Pais` (Sin versi├│n), `Edificio` (Con versi├│n) |

---

## 2. Separaci├│n de Responsabilidades (Cero Validaciones Java)

Las entidades JPA act├║an ├║nicamente como un espejo de la estructura f├¡sica de la base de datos (DDL).

*   **Prohibido**: Usar anotaciones de la especificaci├│n Jakarta Bean Validation (tales como `@NotBlank`, `@NotNull`, `@Size`, `@AssertTrue`, `@Pattern`, etc.) dentro de las clases de Entidad JPA.
*   **Enfoque Correcto**:
    *   La validaci├│n de formato, restricciones de longitud y reglas de l├│gica de negocio se realizan **exclusivamente a nivel de API** en los Request DTOs (ej. `AutorRequest.java`).
    *   La base de datos se protege mediante propiedades de columna f├¡sicas en JPA (ej. `@Column(nullable = false, length = 100)`).
*   **Ventaja**: Evita ejecuciones redundantes de validaciones en cada ciclo de persistencia de Hibernate, optimizando la CPU y flexibilizando las inserciones parciales en las pruebas de integraci├│n.

---

## 3. Lombok y Constructores Seguros

El uso de Lombok debe ser riguroso para evitar efectos secundarios indeseados en Hibernate:

*   **Getter y Setter**: Usar `@Getter` y `@Setter` a nivel de clase.
*   **Prohibici├│n de `@Data`**: NUNCA usar `@Data` a nivel de entidad. Genera implementaciones de `equals()`, `hashCode()` y `toString()` impl├¡citas que rompen el comportamiento Lazy de JPA y cargan relaciones no deseadas.
*   **Constructores para Builder**:
    *   `@NoArgsConstructor(access = AccessLevel.PROTECTED)`: Permite a Hibernate instanciar la entidad mediante reflexi├│n, pero proh├¡be que el desarrollador instancie entidades vac├¡as e inconsistentes en la l├│gica de negocio.
    *   `@AllArgsConstructor(access = AccessLevel.PRIVATE)`: Restringe el constructor de todos los argumentos para forzar el uso exclusivo de `@Builder`.
    *   `@Builder`: Habilita la construcci├│n fluida de objetos.
*   **`@EqualsAndHashCode` Segura**:
    *   Usar `@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)`.
    *   Anotar ├║nicamente el campo `@Id` con `@EqualsAndHashCode.Include`.
    *   **Prop├│sito**: Prevenir ciclos infinitos de mapeo y desbordamientos de pila (`StackOverflowError`) con entidades relacionadas en relaciones bidireccionales.
*   **`@ToString` Exclusivo**:
    *   Usar `@ToString(onlyExplicitlyIncluded = true)`.
    *   Anotar solo la clave primaria y atributos inmutables de identidad natural (ej. `nombre`, `seudonimo`).
    *   NUNCA incluir relaciones lazy ni campos de versi├│n para evitar consultas adicionales accidentales a la base de datos.

---

## 4. Mapeo de Atributos y Relaciones

*   **Identificadores (`@Id`)**: Usar siempre `GenerationType.IDENTITY` para MySQL. Configurar `@Column(name = "id_entidad", updatable = false)`.
*   **Carga Perezosa (Lazy Loading)**: Todas las relaciones `@ManyToOne`, `@OneToOne`, `@OneToMany` y `@ManyToMany` deben configurarse expl├¡citamente con `FetchType.LAZY` (ej. `@ManyToOne(fetch = FetchType.LAZY)`).
*   **Uso de Colecciones**: Usar la interfaz `Set` en lugar de `List` en relaciones ToMany para prevenir excepciones de carga m├║ltiple (`MultipleBagFetchException`). Inicializar siempre la colecci├│n en el constructor o mediante `@Builder.Default`:
    ```java
    @Builder.Default
    @ManyToMany(mappedBy = "categorias", fetch = FetchType.LAZY)
    private Set<Obra> obras = new HashSet<>();
    ```
*   **Tipos de Datos**: Usar tipos nativos modernos de `java.time` (como `Year`, `LocalDate`, `LocalDateTime`).

---

## 6. Borrado L├│gico (Soft Delete)

Para preservar la integridad hist├│rica ante eliminaciones:
*   Anotar la entidad con `@SQLDelete(sql = "UPDATE tabla SET borrado_logico = 1 WHERE id_tabla = ?")`.
*   Definir el atributo `private boolean borradoLogico = false;` mapeado como `@Column(name = "borrado_logico", nullable = false)`.
*   Asegurar que el builder inicialice el estado por defecto anot├índolo con `@Builder.Default`.

---

## 7. Configuraci├│n de ├ìndices en la Base de Datos (`@Table(indexes = ...)`)

Para garantizar el rendimiento ├│ptimo de las consultas sin depender de scripts SQL externos que se pueden perder durante `create-drop` o migraciones, **los ├¡ndices deben definirse directamente en la entidad JPA**:

*   **Uso de `@Table(indexes = { @Index(...) })`**: Declarar la lista de ├¡ndices en la anotaci├│n `@Table`.
*   **├ìndices Compuestos (Filtros M├║ltiples)**: Agrupar columnas que frecuentemente se consultan juntas en las cl├íusulas `WHERE` (ej. `borrado_logico` junto con claves for├íneas o campos de estado).
    *   *Regla del Prefijo Izquierdo*: La columna m├ís filtrada y constante (como `borrado_logico`) debe colocarse en la primera posici├│n del ├¡ndice compuesto.
*   **├ìndices Individuales (B├║squedas Textuales y ├Ünicas)**: Crear ├¡ndices individuales para columnas de texto buscadas por coincidencia parcial (`nombre`, `titulo`), ordenaciones (`ORDER BY`) o identificadores ├║nicos (`isbn`).
*   **L├¡mite Recomendado**: Definir entre **2 y 4 ├¡ndices por entidad transaccional** para equilibrar la velocidad de lectura (`SELECT`) sin penalizar el rendimiento de inserci├│n (`INSERT` / `UPDATE`).
*   *(Nota: Las tablas intermedias `@ManyToMany` son indexadas autom├íticamente por JPA en sus claves for├íneas)*.

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

    @Column(name = "borrado_logico", nullable = false)
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

## 9. Lista de Autoverificaci├│n (Checklist del Desarrollador)

Antes de dar por terminada la refactorizaci├│n o creaci├│n de una nueva entidad, h├ígase las siguientes preguntas:

1. **┬┐Qu├⌐ tipo de entidad es?**
   * *Si es Transaccional/Negocio*: ┬┐Extiende de `Auditable` y tiene el campo `@Version private Long version`?
   * *Si es Cat├ílogo*: ┬┐Evita heredar de `Auditable`? Si requiere control de concurrencia, ┬┐tiene `@Version private Long version`? Si no, ┬┐est├í libre de `@Version`?
2. **┬┐Cero validaciones de Bean Validation (`jakarta.validation`)?**
   * ┬┐Se eliminaron anotaciones como `@NotBlank`, `@Size`, `@AssertTrue` de la entidad? ┬┐Se delegaron todas al respectivo `Request` DTO para evitar ejecuciones redundantes en Hibernate?
3. **┬┐La configuraci├│n de Lombok es segura?**
   * ┬┐Tiene `@NoArgsConstructor(access = AccessLevel.PROTECTED)` y `@AllArgsConstructor(access = AccessLevel.PRIVATE)` junto con `@Builder`?
   * ┬┐Tiene `@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)` incluyendo ├║nicamente el atributo `@Id` marcado con `@EqualsAndHashCode.Include`?
   * ┬┐El `@ToString` tiene `onlyExplicitlyIncluded = true` y excluye relaciones perezosas (lazy)?
4. **┬┐Se configur├│ correctamente la perezosidad (Lazy Loading)?**
   * ┬┐Toda relaci├│n `@ManyToOne`, `@OneToOne`, `@OneToMany` o `@ManyToMany` tiene configurado expl├¡citamente `fetch = FetchType.LAZY`?
5. **┬┐Las colecciones de relaciones ToMany son seguras?**
   * ┬┐Est├ín definidas como `Set` (en vez de `List`) para evitar la excepci├│n de carga m├║ltiple `MultipleBagFetchException`?
   * ┬┐Est├ín marcadas con `@Builder.Default` e inicializadas por defecto (ej. `= new HashSet<>()`)?
6. **┬┐El borrado l├│gico est├í bien estructurado?**
   * ┬┐Tiene `@SQLDelete(sql = "UPDATE ...")`?
   * ┬┐El campo `borradoLogico` est├í anotado con `@Builder.Default` y configurado con `@Column(name = "borrado_logico", nullable = false)`?
7. **┬┐Se configuraron los ├¡ndices en `@Table(indexes = ...)`?**
   * En entidades transaccionales de negocio, ┬┐se declararon los ├¡ndices compuestos (filtros frecuentes `borrado_logico` + FKs) e individuales (b├║squedas por texto u ordenaci├│n) directamente en la anotaci├│n `@Table`?

---
*Este est├índar garantiza la estabilidad, rendimiento e integridad de datos en el dominio de Arrupeteca.*
