package cl.duoc.viajes.model;

/**
 * Representa a cualquier viaje que registra el viajero.
 * Define los datos comunes y delega en cada tipo de viaje el cálculo del
 * costo estimado.
 */
public abstract class Viaje {

    private String codigoReserva;
    private String destino;
    private int semanas;

    /**
     * Constructor sin parámetros, requerido para reconstruir objetos.
     */
    protected Viaje() {
    }

    public Viaje(String codigoReserva, String destino, int semanas) {
        setCodigoReserva(codigoReserva);
        setDestino(destino);
        setSemanas(semanas);
    }

    public String getCodigoReserva() {
        return codigoReserva;
    }

    public void setCodigoReserva(String codigoReserva) {
        if (codigoReserva == null || codigoReserva.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de reserva no puede ser nulo ni vacío.");
        }
        this.codigoReserva = codigoReserva.trim();
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        if (destino == null || destino.trim().isEmpty()) {
            throw new IllegalArgumentException("El destino no puede ser nulo ni vacío.");
        }
        this.destino = destino.trim();
    }

    public int getSemanas() {
        return semanas;
    }

    public void setSemanas(int semanas) {
        if (semanas < 1 || semanas > 54) {
            throw new IllegalArgumentException("Las semanas deben estar entre 1 y 54.");
        }
        this.semanas = semanas;
    }

    /**
     * Calcula el costo estimado del viaje, en dólares, según las reglas propias de cada tipo de viaje.
     *
     * @return costo estimado en dólares (USD)
     */
    public abstract double calcularCostoEstimado();

    /**
     * Permite mostrar el costo calculado en una tabla JavaFX mediante PropertyValueFactory.
     */
    public double getCostoCursoUsd() {
        return calcularCostoEstimado();
    }

    /**
     * Retorna el nombre del tipo de viaje ("Vacaciones" o "Estudios").
     *
     * @return tipo de viaje
     */
    public abstract String getTipo();

    @Override
    public String toString() {
        return "Reserva: " + codigoReserva + " | Destino: " + destino;
    }
}
