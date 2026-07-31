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
  (1, 'Inicial 3 - Parvularia', 0),
  (2, 'Primer Grado - Noveno Grado', 0),
  (3, 'Bachillerato General', 0),
  (4, 'Bachillerato tecnico contador', 0),
  (5, 'Bachillerato tecnico electronica', 0),
  (6, 'Bachillerato tecnico en software', 0);

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
