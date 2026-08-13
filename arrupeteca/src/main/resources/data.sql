-- DDL/DML de semilla para tipo_usuario
INSERT INTO tipo_usuario (id_tipo, nombre, borrado_logico) VALUES
  (1, 'admin', 0),
  (2, 'alumno', 0),
  (3, 'maestro', 0),
  (4, 'personal administrativo', 0);

-- DDL/DML de semilla para parentesco
INSERT INTO parentesco (id_parentesco, nombre, borrado_logico) VALUES
  (1, 'Padre', 0),
  (2, 'Madre', 0),
  (3, 'Tutor Legal', 0),
  (4, 'Hermano/a', 0),
  (5, 'Otro', 0);

-- DDL/DML de semilla para ciclo
INSERT INTO ciclo (id_ciclo, nombre, borrado_logico) VALUES
  (1, 'Primer ciclo', 0),
  (2, 'Segundo ciclo', 0),
  (3, 'Tercer ciclo', 0),
  (4, 'Cuarto ciclo', 0);

-- DDL/DML de semilla para grado
INSERT INTO grado (id_grado, nombre, borrado_logico) VALUES
  (1, 'Tercer Año de Vida', 0),
  (2, 'Cuarto Año de Vida', 0),
  (3, 'Quinto Año de Vida', 0),
  (4, 'Sexto Año de Vida', 0),
  (5, 'Primer Grado', 0),
  (6, 'Segundo Grado', 0),
  (7, 'Tercer Grado', 0),
  (8, 'Cuarto Grado', 0),
  (9, 'Quinto Grado', 0),
  (10, 'Sexto Grado', 0),
  (11, 'Séptimo Grado', 0),
  (12, 'Octavo Grado', 0),
  (13, 'Noveno Grado', 0),
  (14, 'Primer Año Bachillerato Contador', 0),
  (15, 'Primer Año Bachillerato en Desarrollo Software', 0),
  (16, 'Primer Año Bachillerato Electrónica', 0),
  (17, 'Primer Año Bachillerato General', 0),
  (18, 'Segundo Año Bachillerato Contador', 0),
  (19, 'Segundo Año Bachillerato en Desarrollo Software', 0),
  (20, 'Segundo Año Bachillerato Electrónica', 0),
  (21, 'Segundo Año Bachillerato General', 0),
  (22, 'Tercer Año Bachillerato Contador', 0),
  (23, 'Tercer Año Bachillerato Electrónica', 0),
  (24, 'Tercer Año Bachillerato en Desarrollo Software', 0);

-- DDL/DML de semilla para estado_fisico
INSERT INTO estado_fisico (id_estado_fisico, nombre, borrado_logico) VALUES
  (1, 'Nuevo', 0),
  (2, 'Como nuevo', 0),
  (3, 'Buen estado', 0),
  (4, 'Aceptable', 0),
  (5, 'Deteriorado', 0),
  (6, 'Muy deteriorado', 0),
  (7, 'Incompleto', 0);

-- DDL/DML de semilla para disponibilidad
INSERT INTO disponibilidad (id_disponibilidad, nombre, borrado_logico) VALUES
  (1, 'Disponible', 0),
  (2, 'Prestado', 0),
  (3, 'Reservado', 0),
  (4, 'Mantenimiento', 0),
  (5, 'Perdido', 0),
  (6, 'Botado', 0),
  (7, 'Donado', 0);

-- DDL/DML de semilla para etiqueta
INSERT INTO etiqueta (id_etiqueta, nombre, borrado_logico) VALUES
  (1, 'Tecnología', 0),
  (2, 'Día del Niño', 0),
  (3, 'Día de la Madre', 0),
  (4, 'Recomendados', 0),
  (5, 'Novedades', 0);

-- Usuarios Semilla (Contraseña para ambos: Password123!)
INSERT INTO usuario (id_usuario, nombres, apellidos, correo, contrasena, carnet, bloqueado_por_mora, fecha_registro, ultima_fecha_actividad, id_tipo, borrado_logico) VALUES
  (1, 'Administrador', 'Sistema', 'admin@arrupeteca.com', '$2a$12$NBKBxYFKEVLPqH//GUr/Z.OxOkHw.EgEi5rZVUC7CFbGni1Ui8dBC', 'ADM-0001', 0, NOW(), NOW(), 1, 0),
  (2, 'Carlos', 'Pérez', 'alumno@arrupeteca.com', '$2a$12$NBKBxYFKEVLPqH//GUr/Z.OxOkHw.EgEi5rZVUC7CFbGni1Ui8dBC', 'ALU-2026-001', 0, NOW(), NOW(), 2, 0);
