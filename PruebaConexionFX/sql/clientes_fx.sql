-- =====================================================================
-- Base de datos para la prueba de conexión.
-- Es la misma que usará después el proyecto GestorClientesFX.
-- Ejecutar completo en MySQL Workbench.
-- =====================================================================

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
