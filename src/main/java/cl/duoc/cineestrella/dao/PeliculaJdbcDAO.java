package cl.duoc.cineestrella.dao;

import cl.duoc.cineestrella.conexion.ConexionBD;
import cl.duoc.cineestrella.model.Adulto;
import cl.duoc.cineestrella.model.Pelicula;
import cl.duoc.cineestrella.model.TodoEspectador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * ARCHIVO NUEVO (construido por el estudiante).
 * Implementación de PeliculaDAO sobre la base de datos mediante JDBC.
 * Funciona con los scripts de MySQL y de Oracle (mismas tablas y columnas).
 */
public class PeliculaJdbcDAO implements PeliculaDAO {

    // Identificadores del catálogo tipo_calificacion (datos iniciales del script).
    private static final int CALIFICACION_TODO_ESPECTADOR = 1;
    private static final int CALIFICACION_ADULTO = 2;

    private static final String SQL_SELECT =
            "SELECT id, id_calificacion, formato, titulo, duracion, idioma, en_promocion, trasnoche "
            + "FROM pelicula ORDER BY id";
    private static final String SQL_INSERT =
            "INSERT INTO pelicula (id_calificacion, formato, titulo, duracion, idioma, en_promocion, trasnoche) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE =
            "UPDATE pelicula SET id_calificacion = ?, formato = ?, titulo = ?, duracion = ?, idioma = ?, "
            + "en_promocion = ?, trasnoche = ? WHERE id = ?";
    private static final String SQL_DELETE =
            "DELETE FROM pelicula WHERE id = ?";

    @Override
    public List<Pelicula> obtenerTodos() throws PersistenciaException {
        List<Pelicula> peliculas = new ArrayList<>();
        try (Connection conexion = ConexionBD.getConnection();
             PreparedStatement ps = conexion.prepareStatement(SQL_SELECT);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                peliculas.add(crearPelicula(rs));
            }
            return peliculas;
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible conectarse a la base de datos.", e);
        }
    }

    @Override
    public void insertar(Pelicula pelicula) throws PersistenciaException {
        try (Connection conexion = ConexionBD.getConnection();
             PreparedStatement ps = conexion.prepareStatement(SQL_INSERT)) {
            asignarParametros(ps, pelicula);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible registrar la película.", e);
        }
    }

    @Override
    public void actualizar(Pelicula pelicula) throws PersistenciaException {
        try (Connection conexion = ConexionBD.getConnection();
             PreparedStatement ps = conexion.prepareStatement(SQL_UPDATE)) {
            asignarParametros(ps, pelicula);
            ps.setInt(8, pelicula.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible actualizar la película.", e);
        }
    }

    @Override
    public void eliminar(int id) throws PersistenciaException {
        try (Connection conexion = ConexionBD.getConnection();
             PreparedStatement ps = conexion.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("No fue posible eliminar la película.", e);
        }
    }

    /** Crea un TodoEspectador o un Adulto a partir de la fila actual, según su id_calificacion. */
    private Pelicula crearPelicula(ResultSet rs) throws SQLException {
        Pelicula pelicula;
        if (rs.getInt("id_calificacion") == CALIFICACION_TODO_ESPECTADOR) {
            pelicula = new TodoEspectador(rs.getString("formato"), rs.getString("titulo"), rs.getInt("duracion"),
                    rs.getString("idioma"), rs.getBoolean("en_promocion"));
        } else {
            pelicula = new Adulto(rs.getString("formato"), rs.getString("titulo"), rs.getInt("duracion"),
                    rs.getBoolean("trasnoche"));
        }
        pelicula.setId(rs.getInt("id"));
        return pelicula;
    }

    /** Asigna los parámetros 1 a 7 (columnas comunes y propias de la calificación). */
    private void asignarParametros(PreparedStatement ps, Pelicula pelicula) throws SQLException {
        ps.setString(2, pelicula.getFormato());
        ps.setString(3, pelicula.getTitulo());
        ps.setInt(4, pelicula.getDuracion());
        if (pelicula instanceof TodoEspectador todoEspectador) {
            ps.setInt(1, CALIFICACION_TODO_ESPECTADOR);
            ps.setString(5, todoEspectador.getIdioma());
            ps.setBoolean(6, todoEspectador.isEnPromocion());
            ps.setNull(7, Types.INTEGER);
        } else {
            Adulto adulto = (Adulto) pelicula;
            ps.setInt(1, CALIFICACION_ADULTO);
            ps.setNull(5, Types.VARCHAR);
            ps.setNull(6, Types.INTEGER);
            ps.setBoolean(7, adulto.isTrasnoche());
        }
    }
}
