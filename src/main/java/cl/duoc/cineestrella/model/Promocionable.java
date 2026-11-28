package cl.duoc.cineestrella.model;

/**
 * Comportamiento de las películas que pueden ofrecerse en promoción familiar.
 */
public interface Promocionable {

    /** @return true si la película está en promoción familiar. */
    boolean isEnPromocion();

    /** @param enPromocion true si la película queda en promoción familiar. */
    void setEnPromocion(boolean enPromocion);
}
