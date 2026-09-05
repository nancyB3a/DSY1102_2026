package cl.DSY1102;

public class Producto {

    //region ATRIBUTOS
    private int codigo, precio, cantidad;
    private String subCategoria, descripcion;
    private char medida;
    //endregion

    //region CONSTRUCTORES
    public Producto() {
    }
    public Producto(int codigo, int precio, int cantidad, String subCategoria, String descripcion, char medida) {
        this.codigo = codigo;
        this.precio = precio;
        this.cantidad = cantidad;
        //this.subCategoria = subCategoria;
        this.setSubCategoria(subCategoria);
        this.descripcion = descripcion;
        this.medida = medida;
    }
    //endregion

    //region GETTER & SETTER
    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getSubCategoria() {
        return subCategoria;
    }

    public void setSubCategoria(String subCategoria) {
        if (!subCategoria.equalsIgnoreCase("fruta") &&
        !subCategoria.equalsIgnoreCase("verdura")){
            throw new IllegalArgumentException("La sub-categoría debe ser FRUTA ó VERDURA");
        }
        this.subCategoria = subCategoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public char getMedida() {
        return medida;
    }

    public void setMedida(char medida) {
        this.medida = medida;
    }
    //endregion

    //region CUSTOMER's
    @Override
    public String toString() {
        return "Producto{" +
                "codigo=" + codigo +
                ", precio=" + precio +
                ", cantidad=" + cantidad +
                ", subCategoria='" + subCategoria + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", medida=" + medida +
                '}';
    }
    //endregion
}
