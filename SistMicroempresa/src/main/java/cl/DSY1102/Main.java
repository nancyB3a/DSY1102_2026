package cl.DSY1102;

import java.util.ArrayList;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        //para probar regla de negocio
        String categ = "hortaliza";
        Producto p0 = new Producto();
        Solicitud sol = new Solicitud();//CREE este para mantener el correlativo del número de solicitud
        try{
            p0 = new Producto(100,
                    890,
                    1000,
                    categ,
                    "Cebolla",
                    'G');

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        //para probar mi desarrollo
        Producto p1 = new Producto(1000, 890, 1000, "Verdura", "Zanahoria",'G');
        Producto p2 = new Producto(2000, 579, 1, "Verdura", "Pepino",'U');
        Producto p3 = new Producto(3000, 1789, 1, "Fruta", "Piña",'U');
        System.out.println(p2);

        Cliente c1=new Cliente(111, 9111, '9', "Francisco Isla", "fran@algo.cl", "Su casa 111");
        Cliente c2=new Cliente(222, 9222, 'K', "Francisco Mar", "frany@algo.cl", "Su casa 222");;

        Solicitud pedido1 = new Solicitud();
        Solicitud pedido2 = new Solicitud();
        Solicitud pedido3 = new Solicitud();

        pedido1.setNumero(sol.obtenerCorrelativo());
        pedido1.setFecha(new Date());
        pedido1.setCliente(c1);
        pedido1.agregarProducto(p2,3);
        pedido1.agregarProducto(p1,1000);

        pedido1.verSolicitud();

        pedido2.setNumero(sol.obtenerCorrelativo());
        pedido2.setFecha(new Date());
        pedido2.setCliente(c2);
        pedido2.agregarProducto(p3,2);

        pedido2.verSolicitud();

        pedido3.setNumero(sol.obtenerCorrelativo());
        pedido3.setFecha(new Date());
        pedido3.setCliente(c1);
        pedido3.agregarProducto(p3,2);
        pedido3.agregarProducto(p1,500);
        pedido3.agregarProducto(p2,1);

        pedido3.verSolicitud();

        //Colecciones
        System.out.println("\nCon Colecciones");
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        ColeccionSolicitudes pedidos = new ColeccionSolicitudes();
        pedidos.agregarSolicitud(pedido1);
        pedidos.agregarSolicitud(pedido2);
        pedidos.agregarSolicitud(pedido3);

        System.out.println("\n\nIMPRIMIENDO TODOS LOS PEDIDOS");
        pedidos.imprimirSolicitudes();

        System.out.println("\nSolicitud Por Nombre");
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        pedidos.obtenerSolicitudesPorNombre(c1);

        System.out.println("\nSolicitud Por RUT");
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        ArrayList<Solicitud> coleccion =  pedidos.getSolicitudesPorRUT(222);
        for (Solicitud c: coleccion){
            c.verSolicitud();
        }

        pedidos.buscarSolicitudPorNumero(99);
        pedidos.buscarSolicitudPorNumero(3);
        pedidos.eliminarSolicitud(pedido3);
        pedidos.buscarSolicitudPorNumero(3);


    }
}
