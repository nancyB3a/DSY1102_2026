package org.example.gestorclientesfx.model;

/**
 * MODELO: representa una fila de la tabla cliente.
 * La capa DAO la usa para mapear resultados y el controlador para poblar la TableView.
 */
public class Cliente {

    private int id;
    private String nombre;
    private String correo;
    private String ciudad;

    public Cliente() {
    }

    public Cliente(int id, String nombre, String correo, String ciudad) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.ciudad = ciudad;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    @Override
    public String toString() {
        return "Cliente{id=" + id + ", nombre='" + nombre + "', correo='" + correo + "', ciudad='" + ciudad + "'}";
    }
}
