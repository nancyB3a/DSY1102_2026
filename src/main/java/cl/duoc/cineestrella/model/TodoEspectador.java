package cl.duoc.cineestrella.model;

/**
 * Película para todo espectador.
 * <p>
 * Precio de la entrada: precio base según el formato; si está en promoción familiar,
 * se descuenta un 20%.
 */
public class TodoEspectador extends Pelicula implements Promocionable {

    public static final String CALIFICACION = "Todo Espectador";
    private static final double DESCUENTO_PROMOCION = 0.20;

    private String idioma;
    private boolean enPromocion;

    public TodoEspectador(String formato, String titulo, int duracion, String idioma, boolean enPromocion) {
        super(formato, titulo, duracion);
        setIdioma(idioma);
        this.enPromocion = enPromocion;
    }

    public String getIdioma() {
        return idioma;
    }

    /** @param idioma idioma de la función (por ejemplo, "Español"); no puede ser nulo ni vacío. */
    public void setIdioma(String idioma) {
        if (idioma == null || idioma.isBlank()) {
            throw new IllegalArgumentException("El idioma no puede estar vacío.");
        }
        this.idioma = idioma.trim();
    }

    @Override
    public boolean isEnPromocion() {
        return enPromocion;
    }

    @Override
    public void setEnPromocion(boolean enPromocion) {
        this.enPromocion = enPromocion;
    }

    @Override
    public String getCalificacion() {
        return CALIFICACION;
    }

    @Override
    public double calcularPrecioEntrada() {
        double precio = calcularPrecioBase();
        if (enPromocion) {
            precio -= precio * DESCUENTO_PROMOCION;
        }
        return precio;
    }
}
