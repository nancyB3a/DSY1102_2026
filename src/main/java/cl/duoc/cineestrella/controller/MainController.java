package cl.duoc.cineestrella.controller;

import cl.duoc.cineestrella.dao.PeliculaDAO;
import cl.duoc.cineestrella.dao.PeliculaJdbcDAO; // Modificación del estudiante
import cl.duoc.cineestrella.dao.PersistenciaException;
import cl.duoc.cineestrella.model.Adulto;
import cl.duoc.cineestrella.model.Pelicula;
import cl.duoc.cineestrella.model.TodoEspectador;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.Locale;

/**
 * Controlador de la ventana principal: formulario, tabla y área de mensajes.
 * Accede a los datos únicamente a través de la interfaz PeliculaDAO.
 */
public class MainController {

    // Modificación del estudiante: se asigna la implementación JDBC de PeliculaDAO.
    private final PeliculaDAO dao = new PeliculaJdbcDAO();

    private static final Locale LOCALE_CL = Locale.forLanguageTag("es-CL");

    @FXML private ComboBox<String> cbCalificacion;
    @FXML private ComboBox<String> cbFormato;
    @FXML private TextField txtTitulo;
    @FXML private TextField txtDuracion;
    @FXML private TextField txtIdioma;
    @FXML private CheckBox chkPromocion;
    @FXML private CheckBox chkTrasnoche;
    @FXML private Button btnRegistrar;
    @FXML private Button btnCancelar;

    @FXML private TableView<Pelicula> tabla;
    @FXML private TableColumn<Pelicula, Integer> colId;
    @FXML private TableColumn<Pelicula, String> colCalificacion;
    @FXML private TableColumn<Pelicula, String> colFormato;
    @FXML private TableColumn<Pelicula, String> colTitulo;
    @FXML private TableColumn<Pelicula, Integer> colDuracion;
    @FXML private TableColumn<Pelicula, String> colPrecio;

    @FXML private Label lblMensaje;

    /** Película que se está editando; null cuando el formulario registra una nueva. */
    private Pelicula peliculaEnEdicion;

    @FXML
    private void initialize() {
        cbCalificacion.setItems(FXCollections.observableArrayList(TodoEspectador.CALIFICACION, Adulto.CALIFICACION));
        cbCalificacion.valueProperty().addListener((obs, anterior, actual) -> actualizarCamposPorCalificacion());
        cbFormato.setItems(FXCollections.observableArrayList(Pelicula.FORMATO_2D, Pelicula.FORMATO_3D));

        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colCalificacion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCalificacion()));
        colFormato.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFormato()));
        colTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
        colDuracion.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getDuracion()));
        colPrecio.setCellValueFactory(c ->
                new SimpleStringProperty(formatearPrecio(c.getValue().calcularPrecioEntrada())));
        tabla.setPlaceholder(new Label("No hay películas para mostrar."));

        limpiarFormulario();
        cargarTabla();
    }

    // ---------------------------------------------------------------- acciones

    @FXML
    private void onRegistrar() {
        if (!hayDao()) {
            return;
        }
        Pelicula pelicula;
        try {
            pelicula = crearPeliculaDesdeFormulario();
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
            return;
        }

        try {
            if (peliculaEnEdicion == null) {
                dao.insertar(pelicula);
                cargarTabla();
                limpiarFormulario();
                mostrarExito(describir(pelicula) + " registrada correctamente.");
            } else {
                pelicula.setId(peliculaEnEdicion.getId());
                dao.actualizar(pelicula);
                cargarTabla();
                limpiarFormulario();
                mostrarExito(describir(pelicula) + " actualizada correctamente.");
            }
        } catch (PersistenciaException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void onEditar() {
        Pelicula seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Selecciona una película de la tabla para editar.");
            return;
        }
        peliculaEnEdicion = seleccionada;
        cbCalificacion.setValue(seleccionada.getCalificacion());
        cbCalificacion.setDisable(true);
        cbFormato.setValue(seleccionada.getFormato());
        txtTitulo.setText(seleccionada.getTitulo());
        txtDuracion.setText(String.valueOf(seleccionada.getDuracion()));
        if (seleccionada instanceof TodoEspectador todoEspectador) {
            txtIdioma.setText(todoEspectador.getIdioma());
            chkPromocion.setSelected(todoEspectador.isEnPromocion());
        } else if (seleccionada instanceof Adulto adulto) {
            chkTrasnoche.setSelected(adulto.isTrasnoche());
        }
        btnRegistrar.setText("Guardar cambios");
        btnCancelar.setDisable(false);
        txtTitulo.requestFocus();
        mostrarInfo("Editando " + describir(seleccionada)
                + ". Modifica los datos y presiona Guardar cambios.");
    }

    @FXML
    private void onCancelar() {
        limpiarFormulario();
        mostrarInfo("Edición cancelada.");
    }

    @FXML
    private void onEliminar() {
        if (!hayDao()) {
            return;
        }
        Pelicula seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Selecciona una película de la tabla para eliminar.");
            return;
        }
        try {
            dao.eliminar(seleccionada.getId());
            if (peliculaEnEdicion != null && peliculaEnEdicion.getId() == seleccionada.getId()) {
                limpiarFormulario();
            }
            cargarTabla();
            mostrarExito(describir(seleccionada) + " eliminada correctamente.");
        } catch (PersistenciaException e) {
            mostrarError(e.getMessage());
        }
    }

    // ---------------------------------------------------------------- apoyo

    /** Vuelve a leer todas las películas desde el almacenamiento y las muestra en la tabla. */
    private void cargarTabla() {
        if (!hayDao()) {
            tabla.getItems().clear();
            return;
        }
        try {
            tabla.setItems(FXCollections.observableArrayList(dao.obtenerTodos()));
        } catch (PersistenciaException e) {
            tabla.getItems().clear();
            mostrarError("Error al leer la información: " + e.getMessage());
        }
    }

    private boolean hayDao() {
        if (dao == null) {
            mostrarError("Aún no hay una implementación de PeliculaDAO asignada en MainController.");
            return false;
        }
        return true;
    }

    private Pelicula crearPeliculaDesdeFormulario() {
        String calificacion = cbCalificacion.getValue();
        if (calificacion == null) {
            throw new IllegalArgumentException("Selecciona la calificación de la película.");
        }
        String formato = cbFormato.getValue();
        if (formato == null) {
            throw new IllegalArgumentException("Selecciona el formato de la película.");
        }
        String titulo = txtTitulo.getText();
        int duracion = leerEntero(txtDuracion.getText(), "La duración debe ser un número entero de minutos.");

        if (TodoEspectador.CALIFICACION.equals(calificacion)) {
            return new TodoEspectador(formato, titulo, duracion, txtIdioma.getText(), chkPromocion.isSelected());
        }
        return new Adulto(formato, titulo, duracion, chkTrasnoche.isSelected());
    }

    private int leerEntero(String texto, String mensajeError) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(mensajeError);
        }
    }

    private void actualizarCamposPorCalificacion() {
        boolean esTodoEspectador = TodoEspectador.CALIFICACION.equals(cbCalificacion.getValue());
        boolean esAdulto = Adulto.CALIFICACION.equals(cbCalificacion.getValue());
        txtIdioma.setDisable(!esTodoEspectador);
        chkPromocion.setDisable(!esTodoEspectador);
        chkTrasnoche.setDisable(!esAdulto);
    }

    private void limpiarFormulario() {
        peliculaEnEdicion = null;
        cbCalificacion.setDisable(false);
        cbCalificacion.setValue(null);
        cbFormato.setValue(null);
        txtTitulo.clear();
        txtDuracion.clear();
        txtIdioma.clear();
        chkPromocion.setSelected(false);
        chkTrasnoche.setSelected(false);
        btnRegistrar.setText("Registrar");
        btnCancelar.setDisable(true);
        actualizarCamposPorCalificacion();
        tabla.getSelectionModel().clearSelection();
    }

    private String describir(Pelicula pelicula) {
        return pelicula.getTitulo() + " (" + pelicula.getCalificacion() + ")";
    }

    private String formatearPrecio(double precio) {
        return String.format(LOCALE_CL, "$%,.0f", precio);
    }

    private void mostrarExito(String mensaje) {
        mostrarMensaje(mensaje, "#eef5ec", "#a9cfa1", "#1e4620");
    }

    private void mostrarInfo(String mensaje) {
        mostrarMensaje(mensaje, "#eef3f8", "#a8c1d9", "#1f3a56");
    }

    private void mostrarError(String mensaje) {
        mostrarMensaje(mensaje, "#fbeeee", "#e0a3a3", "#8a1f1f");
    }

    private void mostrarMensaje(String mensaje, String fondo, String borde, String texto) {
        lblMensaje.setStyle("-fx-padding: 8 10 8 10; -fx-background-color: " + fondo
                + "; -fx-border-color: " + borde + "; -fx-text-fill: " + texto + ";");
        lblMensaje.setText(mensaje);
    }
}
