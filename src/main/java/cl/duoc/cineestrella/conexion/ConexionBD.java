package cl.duoc.cineestrella.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Reúne los datos de conexión a la base de datos y entrega conexiones.
 * Ninguna otra clase debe contener estos datos.
 */
public class ConexionBD {

    // Modificación del estudiante: datos de conexión del laboratorio.
    // Oracle: URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1", con el usuario del esquema.
    private static final String URL = "jdbc:mysql://localhost:3306/cineestrella";
    private static final String USUARIO = "root";
    private static final String CLAVE = "";

    private ConexionBD() {
    }

    /**
     * @return una conexión abierta a la base de datos. Quien la utiliza debe cerrarla.
     * @throws SQLException si no es posible conectarse.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
}
