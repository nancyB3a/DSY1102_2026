package cl.duoc.cineestrella.model;

/**
 * Datos comunes de todas las películas de la cartelera.
 * <p>
 * El identificador (id) vale 0 mientras la película no ha sido almacenada.
 * Los métodos set rechazan los datos inválidos con una IllegalArgumentException
 * cuyo mensaje explica el motivo.
 * <p>
 * El precio base de la entrada depende del formato: 2D $5.500 y 3D $7.500.
 * Cada subclase aplica sobre él sus propias reglas.
 */
public abstract class Pelicula {

    public static final String FORMATO_2D = "2D";
    public static final String FORMATO_3D = "3D";
    public static final int DURACION_MINIMA = 60;
    public static final int DURACION_MAXIMA = 240;

    private static final double PRECIO_2D = 5500;
    private static final double PRECIO_3D = 7500;

    private int id;
    private String formato;
    private String titulo;
    private int duracion;

    protected Pelicula(String formato, String titulo, int duracion) {
        setFormato(formato);
        setTitulo(titulo);
        setDuracion(duracion);
    }

    /** @return identificador de la película (0 si aún no ha sido almacenada). */
    public int getId() {
        return id;
    }

    /** @param id identificador asignado al almacenar la película (no negativo). */
    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("El identificador no puede ser negativo.");
        }
        this.id = id;
    }

    public String getFormato() {
        return formato;
    }

    /** @param formato "2D" o "3D". */
    public void setFormato(String formato) {
        if (!FORMATO_2D.equals(formato) && !FORMATO_3D.equals(formato)) {
            throw new IllegalArgumentException("El formato debe ser 2D o 3D.");
        }
        this.formato = formato;
    }

    public String getTitulo() {
        return titulo;
    }

    /** @param titulo no puede ser nulo ni vacío. */
    public void setTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío.");
        }
        this.titulo = titulo.trim();
    }

    public int getDuracion() {
        return duracion;
    }

    /** @param duracion en minutos, entre 60 y 240. */
    public void setDuracion(int duracion) {
        if (duracion < DURACION_MINIMA || duracion > DURACION_MAXIMA) {
            throw new IllegalArgumentException("La duración debe estar entre 60 y 240 minutos.");
        }
        this.duracion = duracion;
    }

    /** @return precio de la entrada según el formato: 2D $5.500, 3D $7.500. */
    protected double calcularPrecioBase() {
        return FORMATO_3D.equals(formato) ? PRECIO_3D : PRECIO_2D;
    }

    /** @return calificación de la película: "Todo Espectador" o "Adulto". */
    public abstract String getCalificacion();

    /** @return precio de la entrada, en pesos. */
    public abstract double calcularPrecioEntrada();
}
