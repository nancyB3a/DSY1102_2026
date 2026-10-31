package cl.duoc.viajes.model;

/**
 * Viaje por estudios.
 * Costo estimado: valor del curso más USD 250 de manutención por cada semana.
 */
public class ViajeEstudios extends Viaje {

    private static final double MANUTENCION_SEMANA = 250;

    private double costoCursoUsd;

    /**
     * Constructor sin parámetros, requerido para reconstruir objetos.
     */
    public ViajeEstudios() {
    }

    public ViajeEstudios(String codigoReserva, String destino, int semanas, double costoCursoUsd) {
        super(codigoReserva, destino, semanas);
        setCostoCursoUsd(costoCursoUsd);
    }

    public double getCostoCursoUsd() {
        return costoCursoUsd;
    }

    public void setCostoCursoUsd(double costoCursoUsd) {
        this.costoCursoUsd = costoCursoUsd;
    }

    @Override
    public double calcularCostoEstimado() {
        return costoCursoUsd + MANUTENCION_SEMANA * getSemanas();
    }

    @Override
    public String getTipo() {
        return "Estudios";
    }
}
