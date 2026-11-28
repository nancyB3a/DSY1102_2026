-- =====================================================================
-- Cine Estrella - Base de datos (MySQL 8)
-- Crea la base de datos cineestrella, sus tablas y los datos iniciales.
-- Ejecuta el script completo. No lo modifiques.
-- =====================================================================

SET NAMES utf8mb4;

DROP DATABASE IF EXISTS cineestrella;
CREATE DATABASE cineestrella CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE cineestrella;

-- Catálogo de calificaciones
CREATE TABLE tipo_calificacion (
    id_calificacion  INT          NOT NULL,
    descripcion      VARCHAR(20)  NOT NULL,
    CONSTRAINT pk_tipo_calificacion PRIMARY KEY (id_calificacion),
    CONSTRAINT uq_tipo_calificacion_descripcion UNIQUE (descripcion)
);

-- Películas de la cartelera.
-- idioma y en_promocion se usan solo para películas Todo Espectador;
-- trasnoche se usa solo para películas Adulto.
CREATE TABLE pelicula (
    id               INT           NOT NULL AUTO_INCREMENT,
    id_calificacion  INT           NOT NULL,
    formato          VARCHAR(2)    NOT NULL,
    titulo           VARCHAR(100)  NOT NULL,
    duracion         INT           NOT NULL,
    idioma           VARCHAR(30)   NULL,
    en_promocion     BOOLEAN       NULL,
    trasnoche        BOOLEAN       NULL,
    CONSTRAINT pk_pelicula PRIMARY KEY (id),
    CONSTRAINT fk_pelicula_calificacion FOREIGN KEY (id_calificacion)
        REFERENCES tipo_calificacion (id_calificacion),
    CONSTRAINT ck_pelicula_formato CHECK (formato IN ('2D', '3D')),
    CONSTRAINT ck_pelicula_duracion CHECK (duracion BETWEEN 60 AND 240)
);

-- Datos iniciales
INSERT INTO tipo_calificacion (id_calificacion, descripcion) VALUES (1, 'Todo Espectador');
INSERT INTO tipo_calificacion (id_calificacion, descripcion) VALUES (2, 'Adulto');

INSERT INTO pelicula (id_calificacion, formato, titulo, duracion, idioma, en_promocion, trasnoche)
    VALUES (1, '2D', 'Coco', 105, 'Español', TRUE, NULL);
INSERT INTO pelicula (id_calificacion, formato, titulo, duracion, idioma, en_promocion, trasnoche)
    VALUES (1, '3D', 'Avatar', 162, 'Subtitulada', FALSE, NULL);
INSERT INTO pelicula (id_calificacion, formato, titulo, duracion, idioma, en_promocion, trasnoche)
    VALUES (2, '2D', 'Oppenheimer', 180, NULL, NULL, TRUE);
INSERT INTO pelicula (id_calificacion, formato, titulo, duracion, idioma, en_promocion, trasnoche)
    VALUES (2, '3D', 'Dune', 155, NULL, NULL, FALSE);
INSERT INTO pelicula (id_calificacion, formato, titulo, duracion, idioma, en_promocion, trasnoche)
    VALUES (1, '2D', 'Up', 96, 'Español', FALSE, NULL);
