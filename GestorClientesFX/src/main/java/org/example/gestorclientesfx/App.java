package org.example.gestorclientesfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Aplicación JavaFX: carga la vista principal y abre la ventana.
 */
public class App extends Application {

    private static final String VISTA = "/org/example/gestorclientesfx/view/cliente-view.fxml";

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(VISTA));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Gestor de Clientes");
        stage.setScene(scene);
        stage.setMinWidth(860);
        stage.setMinHeight(520);
        stage.show();
    }
}
