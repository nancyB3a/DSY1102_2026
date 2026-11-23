package org.example.gestorclientesfx.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import org.example.gestorclientesfx.dao.ClienteDao;
import org.example.gestorclientesfx.dao.ClienteDaoMySql;
import org.example.gestorclientesfx.dao.DataAccessException;
import org.example.gestorclientesfx.model.Cliente;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * CONTROLADOR de cliente-view.fxml (Pasos 11 a 14).
 * Responde a los eventos de la interfaz, valida las entradas y refresca la TableView.
 * No contiene SQL: todo el acceso a datos se delega en el DAO.
 */
public class ClienteController {

    /** Formato básico de correo: texto@dominio.ext */
    private static final Pattern FORMATO_CORREO = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colCorreo;
    @FXML private TableColumn<Cliente, String> colCiudad;

    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtCiudad;

    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Label lblEstado;

    /** El controlador conoce la interfaz ClienteDao; la implementación se elige en un solo lugar. */
    private final ClienteDao clienteDao = new ClienteDaoMySql();
    private final ObservableList<Cliente> datos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colCiudad.setCellValueFactory(new PropertyValueFactory<>("ciudad"));

        tablaClientes.setItems(datos);

        // Paso 12: al seleccionar una fila (con el mouse o con las flechas) se copia al formulario
        tablaClientes.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, nuevo) -> seleccionarCliente());

        // Actualizar y Eliminar solo tienen sentido con un cliente seleccionado
        btnActualizar.disableProperty().bind(txtId.textProperty().isEmpty());
        btnEliminar.disableProperty().bind(txtId.textProperty().isEmpty());

        // Se carga después de mostrar la ventana: si MySQL no responde, el aviso aparece sobre ella
        Platform.runLater(this::cargarClientes);
    }

    /** Paso 11: pide los clientes al DAO y actualiza la lista observable de la tabla. */
    private void cargarClientes() {
        try {
            List<Cliente> clientes = clienteDao.listarTodos();
            datos.setAll(clientes);
            lblEstado.setText(clientes.size() + (clientes.size() == 1 ? " cliente registrado" : " clientes registrados"));
        } catch (DataAccessException e) {
            datos.clear();
            lblEstado.setText("Sin conexión con la base de datos");
            mostrarError(e);
        }
    }

    /** Paso 12: copia la fila seleccionada a los campos del formulario. */
    @FXML
    private void seleccionarCliente() {
        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            txtId.setText(String.valueOf(seleccionado.getId()));
            txtNombre.setText(seleccionado.getNombre());
            txtCorreo.setText(seleccionado.getCorreo());
            txtCiudad.setText(seleccionado.getCiudad());
        }
    }

    /** Paso 13: crea un cliente nuevo con los datos del formulario. */
    @FXML
    private void guardarCliente() {
        if (!formularioValido()) {
            return;
        }
        Cliente cliente = new Cliente(
                0, // el id lo asigna MySQL (AUTO_INCREMENT)
                txtNombre.getText().trim(),
                txtCorreo.getText().trim(),
                txtCiudad.getText().trim()
        );
        try {
            clienteDao.insertar(cliente);
            cargarClientes();
            limpiarFormulario();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Cliente guardado",
                    cliente.getNombre() + " se registró correctamente.");
        } catch (DataAccessException e) {
            mostrarError(e);
        }
    }

    /** Paso 13: modifica el cliente seleccionado con los datos del formulario. */
    @FXML
    private void actualizarCliente() {
        if (txtId.getText().isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin selección", "Seleccione en la tabla el cliente que desea actualizar.");
            return;
        }
        if (!formularioValido()) {
            return;
        }
        Cliente cliente = new Cliente(
                Integer.parseInt(txtId.getText()),
                txtNombre.getText().trim(),
                txtCorreo.getText().trim(),
                txtCiudad.getText().trim()
        );
        try {
            clienteDao.actualizar(cliente);
            cargarClientes();
            limpiarFormulario();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Cliente actualizado",
                    "Los datos de " + cliente.getNombre() + " se actualizaron correctamente.");
        } catch (DataAccessException e) {
            mostrarError(e);
        }
    }

    /** Paso 14: elimina el cliente seleccionado, previa confirmación. */
    @FXML
    private void eliminarCliente() {
        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin selección", "Seleccione en la tabla el cliente que desea eliminar.");
            return;
        }
        Alert confirmacion = crearAlerta(Alert.AlertType.CONFIRMATION, "Eliminar cliente",
                "¿Eliminar a " + seleccionado.getNombre() + "? Esta acción no se puede deshacer.");
        ButtonType botonEliminar = new ButtonType("Eliminar", ButtonBar.ButtonData.OK_DONE);
        ButtonType botonCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmacion.getButtonTypes().setAll(botonEliminar, botonCancelar);

        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != botonEliminar) {
            return; // el usuario canceló o cerró el diálogo
        }
        try {
            clienteDao.eliminar(seleccionado.getId());
            cargarClientes();
            limpiarFormulario();
            lblEstado.setText(lblEstado.getText() + " · " + seleccionado.getNombre() + " eliminado");
        } catch (DataAccessException e) {
            mostrarError(e);
        }
    }

    /** Paso 14: deja el formulario vacío y quita la selección de la tabla. */
    @FXML
    private void limpiarFormulario() {
        tablaClientes.getSelectionModel().clearSelection();
        txtId.clear();
        txtNombre.clear();
        txtCorreo.clear();
        txtCiudad.clear();
        txtNombre.requestFocus();
    }

    // ------------------------------------------------------------------
    // Validación del formulario
    // ------------------------------------------------------------------

    private boolean formularioValido() {
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String ciudad = txtCiudad.getText().trim();

        if (nombre.isEmpty()) {
            return invalido(txtNombre, "Ingrese el nombre del cliente.");
        }
        if (nombre.length() > 100) {
            return invalido(txtNombre, "El nombre no puede superar los 100 caracteres.");
        }
        if (correo.isEmpty()) {
            return invalido(txtCorreo, "Ingrese el correo del cliente.");
        }
        if (!FORMATO_CORREO.matcher(correo).matches()) {
            return invalido(txtCorreo, "El correo no tiene un formato válido (ejemplo: nombre@dominio.cl).");
        }
        if (correo.length() > 120) {
            return invalido(txtCorreo, "El correo no puede superar los 120 caracteres.");
        }
        if (ciudad.isEmpty()) {
            return invalido(txtCiudad, "Ingrese la ciudad del cliente.");
        }
        if (ciudad.length() > 80) {
            return invalido(txtCiudad, "La ciudad no puede superar los 80 caracteres.");
        }
        return true;
    }

    private boolean invalido(TextField campo, String mensaje) {
        mostrarAlerta(Alert.AlertType.WARNING, "Datos no válidos", mensaje);
        campo.requestFocus();
        return false;
    }

    // ------------------------------------------------------------------
    // Mensajes al usuario
    // ------------------------------------------------------------------

    private void mostrarError(DataAccessException e) {
        String detalle = e.getCause() != null ? e.getCause().getMessage() : "";
        mostrarAlerta(Alert.AlertType.ERROR, "Error de base de datos",
                e.getMessage() + "\nRevise que MySQL esté activo y que exista la base clientes_fx.\n\nDetalle: " + detalle);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        crearAlerta(tipo, titulo, mensaje).showAndWait();
    }

    /** Crea un Alert con el mismo tema oscuro de la ventana principal. */
    private Alert crearAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        // Botón en español, sin depender del idioma del sistema operativo
        alerta.getButtonTypes().setAll(new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE));
        alerta.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // muestra el mensaje completo, sin "..."
        if (tablaClientes.getScene() != null) {
            alerta.initOwner(tablaClientes.getScene().getWindow());
            alerta.getDialogPane().getStylesheets().addAll(tablaClientes.getScene().getStylesheets());
            alerta.getDialogPane().getStylesheets().addAll(tablaClientes.getScene().getRoot().getStylesheets());
        }
        return alerta;
    }
}
