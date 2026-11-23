package org.example.gestorclientesfx.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Único punto del proyecto que conoce cómo conectarse a MySQL (Paso 7).
 * Si cambian el puerto, el usuario, la contraseña o el nombre de la base,
 * solo se modifica esta clase.
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/clientes_fx";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private DatabaseConnection() {
        // clase utilitaria: no se crean objetos
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
