package modelo;

import java.io.*;
import java.nio.file.*;

public class GestorArchivos {

    private static final String ARCHIVO_RESULTADOS = "resultados.txt";
    private static GestorArchivos instancia;

    private GestorArchivos() {}

    public static GestorArchivos getInstance() {
        if (instancia == null) {
            instancia = new GestorArchivos();
        }
        return instancia;
    }

    public void guardarPartida(String ruta, String contenido) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
            bw.write(contenido);
            bw.flush();
        } catch (IOException e) {
            System.err.println("[GestorArchivos] Error al guardar partida: " + e.getMessage());
        }
    }
    public String cargarPartida(String ruta) {

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                sb.append(linea).append("\n");
            }
        } catch (IOException e) {
            System.err.println("[GestorArchivos] Error al cargar partida: " + e.getMessage());
            return null;
        }
        return sb.toString();
    }

    public void guardarResultado(String resultado) {

        try (RandomAccessFile raf = new RandomAccessFile(ARCHIVO_RESULTADOS, "rw")) {
            raf.seek(raf.length());          
            raf.writeBytes(resultado + "\n");
        } catch (IOException e) {
            System.err.println("[GestorArchivos] Error al guardar resultado: " + e.getMessage());
        }
    }


    public java.util.List<String> leerResultados() {
        java.util.List<String> lineas = new java.util.ArrayList<>();
        File f = new File(ARCHIVO_RESULTADOS);
        if (!f.exists()) return lineas;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.isBlank()) lineas.add(linea);
            }
        } catch (IOException e) {
            System.err.println("[GestorArchivos] Error al leer resultados: " + e.getMessage());
        }
        return lineas;
    }

    public String leerLineaEnPosicion(long posicion) {
        try (RandomAccessFile raf = new RandomAccessFile(ARCHIVO_RESULTADOS, "r")) {
            raf.seek(posicion);    
            return raf.readLine();  
        } catch (IOException e) {
            System.err.println("[GestorArchivos] Error al leer posición: " + e.getMessage());
            return null;
        }
    }
}
