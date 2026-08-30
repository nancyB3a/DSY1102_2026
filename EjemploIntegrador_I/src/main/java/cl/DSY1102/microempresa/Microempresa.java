package cl.DSY1102.microempresa;

import java.util.ArrayList;
import java.util.Date;

public class Microempresa {
    public static void main(String[] args) {
        // TODO code application logic here
        String categ="hortaliza";
        Producto p0 = new Producto();
        Solicitud sol = new Solicitud();//CREE este para mantener el correlativo del número de solicitud
        try{
            p0 = new Producto(100, 890, 1000, categ, "Cebolla",'K');
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        Producto p1 = new Producto(1000, 890, 1000, "Verdura", "Zanahoria",'G');
        Producto p2 = new Producto(1110, 579, 1, "Verdura", "Pepino",'U');
        Producto p3 = new Producto(5000, 1789, 1, "Fruta", "Piña",'U');
        System.out.println(p1.toString());
        Cliente c1=new Cliente(111, 9111, "Francisco Isla", "fran@algo.cl", "Su casa 111", '9');
        Cliente c2=new Cliente(222, 9222, "Francisca Mar", "frany@algo.cl", "Su casa 222", 'K');
        Solicitud pedido = new Solicitud();
        Solicitud pedido2 = new Solicitud();
        Solicitud pedido3 = new Solicitud();
        pedido.setNumero(sol.obtenerNroSolicitud());
        pedido.setCliente(c1);//COLABORACIÓN
        pedido.setFecha(new Date());
        pedido.agregarProducto(p2, 3);
        pedido.agregarProducto(p1, 1000);

        pedido2.setNumero(sol.obtenerNroSolicitud());
        pedido2.setCliente(c2);//COLABORACIÓN
        pedido2.setFecha(new Date());
        pedido2.agregarProducto(p3, 2);

        pedido3.setNumero(sol.obtenerNroSolicitud());
        pedido3.setCliente(c1);//COLABORACIÓN
        pedido3.setFecha(new Date());
        pedido3.agregarProducto(p3, 2);
        pedido3.agregarProducto(p1, 500);
        pedido3.agregarProducto(p2, 1);

        pedido.verSolicitud();
        pedido2.verSolicitud();
        pedido3.verSolicitud();

        //Colecciones
        System.out.println("\nCon Colecciones");
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        ColeccionSolicitudes pedidos = new ColeccionSolicitudes();
        pedidos.agregarSolicitud(pedido);
        pedidos.agregarSolicitud(pedido2);
        pedidos.agregarSolicitud(pedido3);

        System.out.println("\nSolicitud Por Nombre");
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        pedidos.obtenerSolicitudesNombre(c1);

        System.out.println("\nSolicitud Por RUT");
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        ArrayList<Solicitud> coleccion =  pedidos.obtenerSolicitudesCliente(222);
        for (Solicitud c: coleccion){
            c.verSolicitud();
        }

        System.out.println("\nImprimir Colecciones");
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        pedidos.imprimeSolicitudes();
    }

}
