package cl.duoc.viajes.repositorio;

import cl.duoc.viajes.model.ViajeEstudios;
import cl.duoc.viajes.model.ViajeVacaciones;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ViajeVacacionesRepository {
    //conocer el Json
    private final Path archivo = Path.of("viajes_vacaciones.json");
    private final ObservableList<ViajeVacaciones> vacaciones = FXCollections.observableArrayList();
    private final Gson gson= new GsonBuilder().setPrettyPrinting().create();

    public ViajeVacacionesRepository() throws IOException {
        cargar();
    }

    private void cargar() throws IOException{
        if (!Files.exists(archivo)){
            Files.writeString(archivo,"[]");
            return;
        }
        String json = Files.readString(archivo);
        ViajeVacaciones[] datos = gson.fromJson(json,ViajeVacaciones[].class);
        if (datos!= null){
            vacaciones.addAll(datos);
        }
    }

    public ObservableList<ViajeVacaciones> obtenerTodos() {
        return vacaciones;
    }

    public void agregar(ViajeVacaciones viaje) throws IOException{
        vacaciones.add(viaje);
        guardar();
    }

    public void eliminar(ViajeVacaciones viaje) throws IOException{
        vacaciones.remove(viaje);
        guardar();
    }

    private void guardar() throws IOException{
        Files.writeString(archivo,gson.toJson(vacaciones));
    }
}
