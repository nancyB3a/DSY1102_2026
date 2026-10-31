package cl.duoc.viajes.model;

/**
 * Contrato que cumplen los viajes que pueden incluir tours guiados en el destino.
 */
public interface Turistico {

    /**
     * Indica si el viaje tiene tours incluidos.
     *
     * @return true si los tours están incluidos
     */
    boolean tieneToursIncluidos();

    /**
     * Incluye los tours en el viaje.
     */
    void incluirTours();
}
