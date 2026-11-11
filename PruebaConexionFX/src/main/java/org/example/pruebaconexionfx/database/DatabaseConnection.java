package org.example.pruebaconexionfx.database;

import org.example.pruebaconexionfx.model.EstadoConexion;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Único punto del proyecto que sabe cómo conectarse a MySQL.
 * Si cambian el puerto, el usuario, la contraseña o el nombre de la base,
 * solo se modifica esta clase.
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/clientes_fx";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";   // ← tu contraseña de MySQL

    private DatabaseConnection() {
        // clase utilitaria: no se crean objetos
    }

    /** Abre una conexión nueva. Quien la pide debe cerrarla (try-with-resources). */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Intenta conectarse y devuelve el resultado como un objeto del modelo.
     * La conexión se cierra apenas se leen sus datos.
     */
    public static EstadoConexion probarConexion() {
        try (Connection con = getConnection()) {
            DatabaseMetaData info = con.getMetaData();
            String servidor = info.getDatabaseProductName() + " " + info.getDatabaseProductVersion();
            return EstadoConexion.exitosa(servidor, con.getCatalog(), info.getUserName());
        } catch (SQLException e) {
            return EstadoConexion.fallida(explicar(e) + "\n\nDetalle técnico: " + e.getMessage());
        }
    }

    /** Traduce los errores de conexión más comunes a una indicación para el usuario. */
    private static String explicar(SQLException e) {
        if (e.getErrorCode() == 1045) {
            return "Usuario o contraseña incorrectos. Revise USER y PASSWORD en DatabaseConnection.";
        }
        if (e.getErrorCode() == 1049) {
            return "La base de datos clientes_fx no existe. Ejecute el script sql/clientes_fx.sql.";
        }
        if (e.getSQLState() != null && e.getSQLState().startsWith("08")) {
            return "MySQL no responde. Verifique que el servicio esté iniciado y que use el puerto 3306.";
        }
        if (e.getMessage() != null && e.getMessage().startsWith("No suitable driver")) {
            return "No se encontró el conector de MySQL. Recargue Maven y revise la dependencia mysql-connector-j.";
        }
        return "No se pudo conectar con la base de datos.";
    }
}
