package cl.duoc.viajes.controlador;

import cl.duoc.viajes.model.Viaje;
import cl.duoc.viajes.model.ViajeEstudios;
import cl.duoc.viajes.model.ViajeVacaciones;
import cl.duoc.viajes.repositorio.ViajeEstudiosRepository;
import cl.duoc.viajes.repositorio.ViajeVacacionesRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

/**
 * CONTROLADOR de main-view.fxml: formulario de registro, tabla, eliminación y mensajes.
 * Valida la entrada, delega los cálculos en el modelo y los datos en el repositorio.
 */
public class ViajesController {

    @FXML private ComboBox<String> cboTipo;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtDestino;
    @FXML private TextField txtSemanas;
    @FXML private TextField txtCosto;
    @FXML private CheckBox chkAlojamiento;
    @FXML private CheckBox chkTours;
    @FXML private TitledPane panelVacaciones;
    @FXML private TitledPane panelEstudios;
    @FXML private TableView<Viaje> tblViajes;
    @FXML private Label lblMensaje;

    private ViajeVacacionesRepository vacacionesRepository;
    private ViajeEstudiosRepository estudiosRepository;
    /** Colección observable que usa la tabla durante toda la ejecución. */
    private final ObservableList<Viaje> viajes = FXCollections.observableArrayList();


    @FXML
    private void initialize(){
        cboTipo.getItems().addAll("Vacaciones", "Estudios");
        tblViajes.setItems(viajes);

        try{
            vacacionesRepository = new ViajeVacacionesRepository();
            estudiosRepository = new ViajeEstudiosRepository();
            actualizarTabla();
            mostrarMensaje(" ", false);
        } catch(IOException e) {
            mostrarMensaje("Error al leer los archivos de datos: " + e.getMessage(), true);
        }
        cambiarTipo();
    }

    private void mostrarMensaje(String texto, boolean error) {
        lblMensaje.setText(texto);
        lblMensaje.getStyleClass().removeAll("mensaje-ok", "mensaje-error");
        lblMensaje.getStyleClass().add(error ? "mensaje-error": "mensaje-ok");
    }

    /** Sincroniza la colección observable existente con ambos repositorios. */
    private void actualizarTabla() {
        viajes.clear();
        if (vacacionesRepository != null){
            viajes.addAll(vacacionesRepository.obtenerTodos());
        }
        if (estudiosRepository != null){
            viajes.addAll(estudiosRepository.obtenerTodos());
        }
    }

    /** Solo quedan habilitados los datos del tipo elegido. */
    @FXML
    private void cambiarTipo(){
        panelEstudios.setDisable(!"Estudios".equals(cboTipo.getValue()));
        panelVacaciones.setDisable(!"Vacaciones".equals(cboTipo.getValue()));
    }

    @FXML
    private void registrar(){

        if (vacacionesRepository == null || estudiosRepository == null) {
            mostrarMensaje("No fue posible cargar los archivos de datos.", true);
            return;
        }
        String tipo = cboTipo.getValue();
        if (tipo == null) {
            mostrarMensaje("Tipo: debe seleccionar una opción.", true);
            return;
        }
        if (txtCodigo.getText() == null) {
            mostrarMensaje("Código de reserva: no puede estar vacío ni contener solo espacios.", true);
            return;
        }
        if (txtDestino.getText() == null) {
            mostrarMensaje("Destino: no puede estar vacío ni contener solo espacios.", true);
            return;
        }
        int semanas;
        try{
            semanas = Integer.parseInt(txtSemanas.getText().trim());
        } catch (NumberFormatException e) {
            semanas = -1;
        }
        if (semanas < 1 || semanas >54){
            mostrarMensaje("Semanas:debe ser un número entero entre 1 y 54", true);
            return;
        }
        try {
            Viaje viaje;
            if ("Vacaciones".equals(tipo)) {
                ViajeVacaciones vacaciones = new ViajeVacaciones(txtCodigo.getText(), txtDestino.getText(),
                        semanas, chkAlojamiento.isSelected());
                if (chkTours.isSelected()) {
                    vacaciones.incluirTours();
                }
                vacacionesRepository.agregar(vacaciones);
                viaje = vacaciones;
            } else {
                double costoCurso;
                try {
                    costoCurso = Double.parseDouble(txtCosto.getText().trim().replace(',', '.'));
                } catch (NumberFormatException ex) {
                    costoCurso = -1;
                }
                if (costoCurso < 500 || costoCurso > 8000) {
                    mostrarMensaje("Costo del curso: debe ser un número entre 500 y 8000 dólares.", true);
                    return;
                }
                ViajeEstudios estudios = new ViajeEstudios(txtCodigo.getText(), txtDestino.getText(), semanas, costoCurso);
                estudiosRepository.agregar(estudios);
                viaje = estudios;
            }

            actualizarTabla();
            mostrarMensaje(viaje.getCodigoReserva() + " (" + viaje.getTipo() + ") registrado correctamente.", false);
            limpiarFormulario();
        } catch (IllegalArgumentException ex) {
            mostrarMensaje(ex.getMessage(), true); // respaldo: validación del modelo
        } catch (IOException ex) {
            mostrarMensaje("Error al guardar los archivos de datos: " + ex.getMessage(), true);
            actualizarTabla();
        }
    }
    @FXML
    private void eliminar(){
        Viaje seleccionado = tblViajes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarMensaje("Debe seleccionar un viaje de la tabla para eliminarlo.", true);
            return;
        }
        try {
            if (seleccionado instanceof ViajeVacaciones vacaciones) {
                vacacionesRepository.eliminar(vacaciones);
            } else if (seleccionado instanceof ViajeEstudios estudios) {
                estudiosRepository.eliminar(estudios);
            }
            actualizarTabla();
            mostrarMensaje(seleccionado.getCodigoReserva() + " eliminado correctamente.", false);
        } catch (IOException ex) {
            mostrarMensaje("Error al guardar los archivos de datos: " + ex.getMessage(), true);
        }
    }

    private void limpiarFormulario() {
        cboTipo.getSelectionModel().clearSelection();
        txtCodigo.clear();
        txtDestino.clear();
        txtSemanas.clear();
        chkAlojamiento.setSelected(false);
        chkTours.setSelected(false);
        txtCosto.clear();
        cambiarTipo();
    }


}
