package org.example.gestorclientesfx.dao;

/**
 * Error de acceso a datos. Envuelve la SQLException original para que el
 * controlador pueda informar al usuario con un Alert, sin depender de JDBC.
 */
public class DataAccessException extends RuntimeException {

    public DataAccessException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
