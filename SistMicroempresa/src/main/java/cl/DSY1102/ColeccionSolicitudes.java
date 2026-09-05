package cl.DSY1102;

import java.util.ArrayList;

public class ColeccionSolicitudes {
    //defino atributo para almacenar colección
    private ArrayList<Solicitud> solicitudes;

    public ColeccionSolicitudes() { //debo inicializar mi colección
        solicitudes = new ArrayList<Solicitud>();
    }

    //método para agregar una solicitud a la colección
    public void agregarSolicitud(Solicitud solicitud){
        this.solicitudes.add(solicitud);
        System.out.println("Solicitud Agregada: " + solicitud.toString());
    }

    //método para eliminar una solicitud de la colección
    public void eliminarSolicitud(Solicitud solicitud){
        if (solicitudes.remove(solicitud)){
            System.out.println("Solicitud Eliminada: " + solicitud.toString());
        }else {
            System.out.println("Solicitud NO encontrada");
        }
    }

    //método para buscar las solicitudes de clientes por rut
    public ArrayList<Solicitud> getSolicitudesPorRUT(int rutCliente){
        //defino una lista para que almacene las solicitudes que encuentre para un RUT
        ArrayList<Solicitud> lista = new ArrayList<>();
        for (Solicitud sol : solicitudes){
            if (sol.getCliente().getRun() == rutCliente){
                lista.add(sol);
            }
        }
        return lista;
    }

    //método para imprimir las solicitudes de un cliente buscado por su nombre
    public void obtenerSolicitudesPorNombre(Cliente cli){
        for (Solicitud aux : solicitudes){
            if (aux.getCliente().getNombre().equals(cli.getNombre())){
                System.out.println(aux.toString());
            }
        }
    }

    //método para imprimir una solicitus buscada por su número
    public void buscarSolicitudPorNumero(int nro){
        boolean encontrado = false;
        for (Solicitud tmp : solicitudes){
            if (tmp.getNumero() == nro){
                encontrado = true;
                System.out.println(tmp.toString());
            }
        }
        if (!encontrado){
            System.out.println("Solicitud NO encontrada!!");
        }
    }

    public void imprimirSolicitudes(){
        for (Solicitud s: solicitudes){
            s.verSolicitud();
        }
    }
}
