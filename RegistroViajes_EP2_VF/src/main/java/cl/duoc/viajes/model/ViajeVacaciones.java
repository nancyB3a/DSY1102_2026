package cl.duoc.viajes.model;

/**
 * Viaje por vacaciones. Puede incluir tours guiados en el destino.
 * Costo estimado: USD 800 por semana; si incluye alojamiento, recargo del 25%.
 */
public class ViajeVacaciones extends Viaje implements Turistico {

    private static final double COSTO_SEMANA = 800;
    private static final double RECARGO_ALOJAMIENTO = 0.25;

    private boolean alojamientoIncluido;
    private boolean toursIncluidos;

    /**
     * Constructor sin parámetros, requerido para reconstruir objetos.
     */
    public ViajeVacaciones() {
    }

    public ViajeVacaciones(String codigoReserva, String destino, int semanas, boolean alojamientoIncluido) {
        super(codigoReserva, destino, semanas);
        setAlojamientoIncluido(alojamientoIncluido);
        this.toursIncluidos = false;
    }

    public boolean isAlojamientoIncluido() {
        return alojamientoIncluido;
    }

    public void setAlojamientoIncluido(boolean alojamientoIncluido) {
        this.alojamientoIncluido = alojamientoIncluido;
    }

    @Override
    public boolean tieneToursIncluidos() {
        return toursIncluidos;
    }

    @Override
    public void incluirTours() {
        this.toursIncluidos = true;
    }

    @Override
    public double calcularCostoEstimado() {
        double costo = COSTO_SEMANA * getSemanas();
        if (alojamientoIncluido) {
            return costo + costo * RECARGO_ALOJAMIENTO;
        }
        return costo;
    }

    @Override
    public String getTipo() {
        return "Vacaciones";
    }
}
