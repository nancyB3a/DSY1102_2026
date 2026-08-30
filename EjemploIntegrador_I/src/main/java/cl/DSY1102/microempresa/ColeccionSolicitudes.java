package cl.DSY1102.microempresa;

import java.util.ArrayList;

public class ColeccionSolicitudes {
    private ArrayList<Solicitud> solicitudes;

    public ColeccionSolicitudes() {
        solicitudes = new ArrayList<Solicitud>();
    }

    public void agregarSolicitud(Solicitud solicitud) {
        solicitudes.add(solicitud);
        System.out.println("Solicitud agregada: " + solicitud.toString());
    }

    public void eliminarSolicitud(Solicitud solicitud) {
        if (solicitudes.remove(solicitud)) {
            System.out.println("Solicitud eliminada: " + solicitud.toString());
        } else {
            System.out.println("La solicitud no se encontró.");
        }
    }

    public ArrayList<Solicitud> obtenerSolicitudesCliente(int rutCliente) {
        ArrayList<Solicitud> resultado = new ArrayList<>();
        for (Solicitud r : solicitudes) {
            if (r.getCliente().getRun() == rutCliente) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    public void obtenerSolicitudesNombre(Cliente cliente) {
        for (Solicitud r : solicitudes) {
            if (r.getCliente().getNombre().equals(cliente.getNombre())) {
                System.out.println(r.toString());
            }
        }
    }

    public void imprimeSolicitudes(){
        for (Solicitud r : solicitudes){
            r.verSolicitud();
        }
    }
}
