package cl.DSY1102.microempresa;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Solicitud {

    //region ATRIBUTOS
    private int numero,total;
    private Date fecha;
    private Cliente cliente; //Este es el atributo de COLABORACIÓN
    private String productos=""; //acá irán TODOS los productos comprados por un cliente
    //endregion

    //region CONSTRUCTOR's

    public Solicitud() {
    }

    public Solicitud(int numero, int total, Date fecha, Cliente cliente, String productos) {
        this.numero = numero;
        this.total = total;
        this.fecha = fecha;
        this.cliente = cliente;
        this.productos = productos;
    }
    //endregion

    //region GETTER && SETTER

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getProductos() {
        return productos;
    }

    public void setProductos(String productos) {
        this.productos = productos;
    }

    //endregion

    //region CUSTOMER's

    @Override
    public String toString() {
        return "Solicitud{" +
                "numero=" + numero +
                ", total=" + total +
                ", fecha=" + fecha +
                ", cliente=" + cliente +
                ", productos='" + productos + '\'' +
                '}';
    }

    /**
     * método para agregar productos a la solicitud
     * e incrementar el total
     * @param producto
     * @param cantidad
     */
    public void agregarProducto(Producto producto, int cantidad){
        String unidadMedida;
        if (producto.getMedida()=='U') {
            unidadMedida = "unidad(es)";
            this.total +=producto.getPrecio()*cantidad;
        }else{
            unidadMedida = "gramos";
            this.total += (int)((producto.getPrecio()*cantidad)/producto.getCantidad());
        }
        //armo mi String de productos
        this.productos += producto.getDescripcion() +
                " $" + producto.getPrecio() +
                " " + cantidad +
                " " + unidadMedida + ";\n";
    }

    /**
     * método que hace el descuento indicado por parámetro
     * @param porcentaje
     * @return
     */
    public int descontar(int porcentaje){
        return (int)(this.total*porcentaje/100);
    }
    /**
     * método para obtener el correlativo de la solicitud
     * @return
     */
    public int obtenerNroSolicitud(){
        this.numero+=1;
        return this.numero;
    }
    /**
     * método para ver la solicitud
     */
    public void verSolicitud(){
        SimpleDateFormat formated= new SimpleDateFormat("dd-MMM-yyyy");
        System.out.println("Solicitud: "+this.numero);
        System.out.println("***************************");
        System.out.println("Cliente: " + this.cliente.getNombre());
        System.out.println("Fecha: "+ formated.format(this.fecha));
        System.out.println(this.productos);
        System.out.println("***************************");
        System.out.println("Total: $" + this.total+"\n");
    }

    //endregion
}
