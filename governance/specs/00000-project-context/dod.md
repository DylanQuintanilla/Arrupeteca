# Deployment and Operations Document (DOD) - Arrupeteca (Documento de Despliegue y Operaciones)

## 1. Introduction (Introducción)
Este documento describe el proceso de despliegue, configuración y mantenimiento operativo del sistema Arrupeteca.

## 2. Infrastructure Requirements (Requisitos de Infraestructura)
- **Runtime**: Java 21 (JDK 21+)
- **Build Tool**: Gradle 8.x
- **Database**: MySQL 8.x
- **OS**: Linux / Windows / macOS

## 3. Local Deployment (Despliegue Local)

### 3.1 Database Setup (Configuración de Base de Datos)
1. Iniciar el servicio de MySQL.
2. Crear la base de datos `arrupeteca`.
3. Configurar las credenciales en `arrupeteca/src/main/resources/application.properties`.

### 3.2 Build and Run (Construcción y Ejecución)
Desde la carpeta `arrupeteca`, ejecutar:
```bash
./gradlew bootRun
```

## 4. Environment Configuration (Configuración del Entorno)
- `application.properties`: Configuración base.
- `application-dev.properties`: Perfil de desarrollo (configuración local).

## 5. Operations and Monitoring (Operaciones y Monitoreo)
- **Logging**: Los registros se emiten a la consola estándar. En el futuro, se configurará un archivo de logs dedicado.
- **Audit**: Los cambios en los datos se rastrean automáticamente en las tablas correspondientes mediante los campos de auditoría heredados de `Auditable`.

## 6. Backup and Recovery (Respaldo y Recuperación)
- El respaldo de la base de datos MySQL debe programarse mediante tareas `mysqldump` regulares.
- El borrado lógico (flag `borradoLogico`) actúa como la primera línea de defensa para la recuperación de datos ante borrados accidentales.
