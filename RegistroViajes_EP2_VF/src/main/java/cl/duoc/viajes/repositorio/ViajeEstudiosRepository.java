package cl.duoc.viajes.repositorio;

import cl.duoc.viajes.model.Viaje;
import cl.duoc.viajes.model.ViajeEstudios;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Repositorio de los viajes por estudios: mantiene la colección en memoria
 * y delega en el DAO la lectura y escritura de viajes_estudios.json.
 * No depende de JavaFX: trabaja con una colección del dominio (List).
 */

public class ViajeEstudiosRepository {
    //conocer el Json
    private final Path archivo = Path.of("viajes_estudios.json");
    private final ObservableList<ViajeEstudios> estudios = FXCollections.observableArrayList();
    private final Gson gson= new GsonBuilder().setPrettyPrinting().create();

    public ViajeEstudiosRepository() throws IOException{
        cargar();
    }

    private void cargar() throws IOException{
        if (!Files.exists(archivo)){
            Files.writeString(archivo,"[]");
            return;
        }
        String json = Files.readString(archivo);
        ViajeEstudios[] datos = gson.fromJson(json,ViajeEstudios[].class);
        if (datos!= null){
            estudios.addAll(datos);
        }
    }

    public ObservableList<ViajeEstudios> obtenerTodos() {
        return estudios;
    }

    public void agregar(ViajeEstudios viaje) throws IOException{
        estudios.add(viaje);
        guardar();
    }

    public void eliminar(ViajeEstudios viaje) throws IOException{
        estudios.remove(viaje);
        guardar();
    }

    private void guardar() throws IOException{
        Files.writeString(archivo,gson.toJson(estudios));
    }

}
