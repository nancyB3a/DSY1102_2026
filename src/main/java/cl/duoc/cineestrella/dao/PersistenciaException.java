package cl.duoc.cineestrella.dao;

/**
 * Error de almacenamiento. Su mensaje se muestra en el área de mensajes de la ventana.
 */
public class PersistenciaException extends Exception {

    private static final long serialVersionUID = 1L;

    public PersistenciaException(String mensaje) {
        super(mensaje);
    }

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
