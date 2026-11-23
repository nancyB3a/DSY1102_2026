package org.example.gestorclientesfx.dao;

import org.example.gestorclientesfx.model.Cliente;

import java.util.List;

/**
 * Contrato de acceso a datos de clientes (Paso 8).
 * El controlador solo conoce esta interfaz: no sabe si los datos vienen de MySQL,
 * de otra base de datos o de una implementación de prueba.
 */
public interface ClienteDao {

    List<Cliente> listarTodos();

    void insertar(Cliente cliente);

    void actualizar(Cliente cliente);

    void eliminar(int id);
}
