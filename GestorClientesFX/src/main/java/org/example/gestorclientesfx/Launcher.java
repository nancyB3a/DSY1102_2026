package org.example.gestorclientesfx;

import javafx.application.Application;

/**
 * Punto de entrada del programa: ejecuta esta clase para iniciar la aplicación.
 * <p>
 * Existe como clase separada de {@link App} para que JavaFX arranque desde
 * IntelliJ sin module-info.java ni configuración adicional.
 */
public class Launcher {

    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}
