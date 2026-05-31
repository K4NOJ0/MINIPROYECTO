package persistencia;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GestorArchivos {
    private static GestorArchivos instancia;

    private GestorArchivos() {
      
    }

    public static GestorArchivos getInstance() {
        if (instancia == null) {
            instancia = new GestorArchivos();
        }
        return instancia;
    }

    public void guardarPartida(String ruta, String contenido) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ruta))) {
            writer.write(contenido);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String cargarPartida(String ruta) {
        try {
            return new String(Files.readAllBytes(Paths.get(ruta)));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void guardarResultado(String resultado) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resultados.txt", true))) {
            writer.write(resultado);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
