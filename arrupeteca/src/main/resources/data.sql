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

-- Usuarios Semilla (Contraseña para ambos: Password123!)
INSERT INTO usuario (id_usuario, nombres, apellidos, correo, contrasena, carnet, bloqueado_por_mora, fecha_registro, ultima_fecha_actividad, id_tipo, borrado_logico) VALUES
  (1, 'Administrador', 'Sistema', 'admin@arrupeteca.com', '$2a$12$NBKBxYFKEVLPqH//GUr/Z.OxOkHw.EgEi5rZVUC7CFbGni1Ui8dBC', 'ADM-0001', 0, NOW(), NOW(), 1, 0),
  (2, 'Carlos', 'Pérez', 'alumno@arrupeteca.com', '$2a$12$NBKBxYFKEVLPqH//GUr/Z.OxOkHw.EgEi5rZVUC7CFbGni1Ui8dBC', 'ALU-2026-001', 0, NOW(), NOW(), 2, 0);
