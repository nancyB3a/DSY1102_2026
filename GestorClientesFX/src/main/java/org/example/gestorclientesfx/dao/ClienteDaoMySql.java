package org.example.gestorclientesfx.dao;

import org.example.gestorclientesfx.database.DatabaseConnection;
import org.example.gestorclientesfx.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del DAO con MySQL (Pasos 9 y 10). Aquí vive todo el código JDBC.
 * <p>
 * Cada método abre su conexión con try-with-resources, que cierra automáticamente
 * Connection, PreparedStatement y ResultSet aunque ocurra un error.
 * Los datos del usuario siempre viajan como parámetros (?) de un PreparedStatement.
 */
public class ClienteDaoMySql implements ClienteDao {

    @Override
    public List<Cliente> listarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT id, nombre, correo, ciudad FROM cliente ORDER BY id";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                clientes.add(new Cliente(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("ciudad")
                ));
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudieron cargar los clientes.", e);
        }
        return clientes;
    }

    @Override
    public void insertar(Cliente cliente) {
        String sql = "INSERT INTO cliente(nombre, correo, ciudad) VALUES (?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getCorreo());
            ps.setString(3, cliente.getCiudad());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo guardar el cliente.", e);
        }
    }

    @Override
    public void actualizar(Cliente cliente) {
        String sql = "UPDATE cliente SET nombre = ?, correo = ?, ciudad = ? WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getCorreo());
            ps.setString(3, cliente.getCiudad());
            ps.setInt(4, cliente.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo actualizar el cliente.", e);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM cliente WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo eliminar el cliente.", e);
        }
    }
}
