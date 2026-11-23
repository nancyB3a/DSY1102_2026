-- =====================================================================
-- GestorClientesFX - Base de datos (Paso 3)
-- Ejecutar completo en MySQL Workbench o en la consola de MySQL.
-- =====================================================================

-- Indica que este archivo está en UTF-8 (evita que los acentos se guarden mal)
SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS clientes_fx
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE clientes_fx;

CREATE TABLE IF NOT EXISTS cliente (
    id     INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(120) NOT NULL,
    ciudad VARCHAR(80)  NOT NULL
);

-- Datos de prueba (opcional): permiten ver la tabla con registros desde el primer arranque
INSERT INTO cliente (nombre, correo, ciudad) VALUES
    ('Camila Rojas',    'camila.rojas@correo.cl',    'Santiago'),
    ('Matías González', 'matias.gonzalez@correo.cl', 'Valparaíso'),
    ('Valentina Soto',  'valentina.soto@correo.cl',  'Concepción');

SELECT * FROM cliente;
