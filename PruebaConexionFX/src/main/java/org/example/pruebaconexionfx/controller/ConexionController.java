package org.example.pruebaconexionfx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.pruebaconexionfx.database.DatabaseConnection;
import org.example.pruebaconexionfx.model.EstadoConexion;

/**
 * CONTROLADOR de conexion-view.fxml.
 * Pide la prueba a la capa de conexión y muestra el resultado en la vista.
 * No contiene código JDBC.
 */
public class ConexionController {

    @FXML private Label lblEstado;
    @FXML private Label lblServidor;
    @FXML private Label lblBaseDeDatos;
    @FXML private Label lblUsuario;
    @FXML private Label lblDetalle;

    @FXML
    private void initialize() {
        // Se prueba apenas se abre la ventana
        Platform.runLater(this::probarConexion);
    }

    @FXML
    private void probarConexion() {
        EstadoConexion estado = DatabaseConnection.probarConexion();
        mostrar(estado);
    }

    private void mostrar(EstadoConexion estado) {
        lblEstado.setText(estado.isConectado() ? "● Conectado" : "● Sin conexión");
        lblEstado.getStyleClass().removeAll("estado-ok", "estado-error");
        lblEstado.getStyleClass().add(estado.isConectado() ? "estado-ok" : "estado-error");

        lblServidor.setText(estado.getServidor());
        lblBaseDeDatos.setText(estado.getBaseDeDatos());
        lblUsuario.setText(estado.getUsuario());
        lblDetalle.setText(estado.isConectado()
                ? "Todo listo para continuar con la capa DAO."
                : estado.getMensaje());

        // La ventana se ajusta al largo del mensaje
        if (lblEstado.getScene() != null) {
            lblEstado.getScene().getWindow().sizeToScene();
        }
    }
}
