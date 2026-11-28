package cl.duoc.cineestrella.dao;

import cl.duoc.cineestrella.model.Pelicula;

import java.util.List;

/**
 * Operaciones de almacenamiento que utiliza la ventana.
 * <p>
 * Si una operación no puede completarse, debe lanzar PersistenciaException con un
 * mensaje que indique qué operación falló; la ventana muestra ese mensaje en el
 * área de mensajes.
 */
public interface PeliculaDAO {

    /**
     * @return todas las películas almacenadas, cada una como objeto de su calificación
     *         (TodoEspectador o Adulto), con todos sus datos y su identificador.
     */
    List<Pelicula> obtenerTodos() throws PersistenciaException;

    /**
     * Guarda una película nueva con todos sus datos, incluidos los propios de su calificación.
     * El identificador lo genera la base de datos.
     */
    void insertar(Pelicula pelicula) throws PersistenciaException;

    /**
     * Modifica todos los datos de la película que tiene el mismo identificador,
     * incluidos los propios de su calificación.
     */
    void actualizar(Pelicula pelicula) throws PersistenciaException;

    /**
     * Elimina la película con el identificador indicado.
     */
    void eliminar(int id) throws PersistenciaException;
}
