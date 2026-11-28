package cl.duoc.cineestrella.model;

/**
 * Película para adultos.
 * <p>
 * Precio de la entrada: precio base según el formato; si se exhibe en función
 * trasnoche, se descuenta un 15%.
 */
public class Adulto extends Pelicula {

    public static final String CALIFICACION = "Adulto";
    private static final double DESCUENTO_TRASNOCHE = 0.15;

    private boolean trasnoche;

    public Adulto(String formato, String titulo, int duracion, boolean trasnoche) {
        super(formato, titulo, duracion);
        this.trasnoche = trasnoche;
    }

    public boolean isTrasnoche() {
        return trasnoche;
    }

    public void setTrasnoche(boolean trasnoche) {
        this.trasnoche = trasnoche;
    }

    @Override
    public String getCalificacion() {
        return CALIFICACION;
    }

    @Override
    public double calcularPrecioEntrada() {
        double precio = calcularPrecioBase();
        if (trasnoche) {
            precio -= precio * DESCUENTO_TRASNOCHE;
        }
        return precio;
    }
}
