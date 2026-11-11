package org.example.pruebaconexionfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Carga la vista y abre la ventana.
 */
public class App extends Application {

    private static final String VISTA = "/org/example/pruebaconexionfx/view/conexion-view.fxml";

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(VISTA));
        stage.setTitle("Prueba de conexión");
        stage.setScene(new Scene(loader.load()));
        stage.setResizable(false);
        stage.show();
    }
}
