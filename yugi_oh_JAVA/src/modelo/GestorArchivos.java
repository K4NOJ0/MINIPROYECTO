package modelo;

import java.io.*;
import java.util.*;

public class GestorArchivos {

    private static final String ARCHIVO_RESULTADOS = "resultados.txt";
    private static final String ARCHIVO_PARTIDAS = "partidas.txt";
    private static GestorArchivos instancia;

    private GestorArchivos() {
    }

    public static GestorArchivos getInstance() {
        if (instancia == null) {
            instancia = new GestorArchivos();
        }
        return instancia;
    }

    public void guardarSlot(int slot, String contenido) {
        String inicio = "[GUARDADO_" + slot + "]";
        String fin = "[FIN_GUARDADO_" + slot + "]";

        List<String> lineas = new ArrayList<>();
        File archivo = new File(ARCHIVO_PARTIDAS);
        if (archivo.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String l;
                while ((l = br.readLine()) != null)
                    lineas.add(l);
            } catch (IOException e) {
                System.err.println("[GestorArchivos] Error al leer partidas: " + e.getMessage());
            }
        }

        List<String> resultado = new ArrayList<>();
        boolean dentro = false;
        for (String l : lineas) {
            if (l.equals(inicio)) {
                dentro = true;
                continue;
            }
            if (l.equals(fin)) {
                dentro = false;
                continue;
            }
            if (!dentro)
                resultado.add(l);
        }

        resultado.add(inicio);
        for (String l : contenido.split("\n"))
            resultado.add(l);
        resultado.add(fin);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_PARTIDAS))) {
            for (String l : resultado) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[GestorArchivos] Error al guardar slot " + slot + ": " + e.getMessage());
        }
    }

    public String cargarSlot(int slot) {
        String inicio = "[GUARDADO_" + slot + "]";
        String fin = "[FIN_GUARDADO_" + slot + "]";

        File archivo = new File(ARCHIVO_PARTIDAS);
        if (!archivo.exists())
            return null;

        StringBuilder sb = new StringBuilder();
        boolean dentro = false;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String l;
            while ((l = br.readLine()) != null) {
                if (l.equals(inicio)) {
                    dentro = true;
                    continue;
                }
                if (l.equals(fin)) {
                    dentro = false;
                    continue;
                }
                if (dentro) {
                    sb.append(l).append("\n");
                }
            }
        } catch (IOException e) {
            System.err.println("[GestorArchivos] Error al cargar slot " + slot + ": " + e.getMessage());
            return null;
        }
        return sb.length() == 0 ? null : sb.toString();
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
        if (!f.exists())
            return lineas;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.isBlank())
                    lineas.add(linea);
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
            System.err.println("[GestorArchivos] Error al leer posicion: " + e.getMessage());
            return null;
        }
    }
}
