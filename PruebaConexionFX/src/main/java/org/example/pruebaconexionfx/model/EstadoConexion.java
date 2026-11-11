package org.example.pruebaconexionfx.model;

/**
 * MODELO: resultado de un intento de conexión con la base de datos.
 * <p>
 * No sabe nada de JavaFX ni de JDBC: solo guarda los datos que la vista mostrará.
 * Se crea con uno de los dos métodos de fábrica: {@link #exitosa} o {@link #fallida}.
 */
public class EstadoConexion {

    private final boolean conectado;
    private final String mensaje;
    private final String servidor;
    private final String baseDeDatos;
    private final String usuario;

    private EstadoConexion(boolean conectado, String mensaje, String servidor, String baseDeDatos, String usuario) {
        this.conectado = conectado;
        this.mensaje = mensaje;
        this.servidor = servidor;
        this.baseDeDatos = baseDeDatos;
        this.usuario = usuario;
    }

    /** La conexión se abrió correctamente. */
    public static EstadoConexion exitosa(String servidor, String baseDeDatos, String usuario) {
        return new EstadoConexion(true, "Conexión exitosa", servidor, baseDeDatos, usuario);
    }

    /** No se pudo conectar; el mensaje explica el motivo. */
    public static EstadoConexion fallida(String mensaje) {
        return new EstadoConexion(false, mensaje, "-", "-", "-");
    }

    public boolean isConectado() { return conectado; }
    public String getMensaje() { return mensaje; }
    public String getServidor() { return servidor; }
    public String getBaseDeDatos() { return baseDeDatos; }
    public String getUsuario() { return usuario; }

    @Override
    public String toString() {
        return conectado
                ? "Conectado a " + baseDeDatos + " (" + servidor + ") como " + usuario
                : "Sin conexión: " + mensaje;
    }
}
