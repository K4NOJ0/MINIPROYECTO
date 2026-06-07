package modelo;

import modelo.GestorArchivos;
import java.util.*;
import java.util.regex.*;

public class AnalizadorEstadisticas {

    private HashMap<String, Integer> victoriasPorJugador;
    private List<RegistroPartida>    partidas;
    private static final Pattern PATRON_LINEA = Pattern.compile(
        "\\[(.*?)\\] Ganador: (.*?) \\| LP Finales: (\\d+)"
    );

    public AnalizadorEstadisticas() {
        victoriasPorJugador = new HashMap<>();
        partidas            = new ArrayList<>();
    }

    public void cargar() {
        victoriasPorJugador.clear();
        partidas.clear();

        List<String> lineas = GestorArchivos.getInstance().leerResultados();

        for (String linea : lineas) {
            Matcher m = PATRON_LINEA.matcher(linea);
            if (!m.find()) continue;

            String fecha   = m.group(1);
            String ganador = m.group(2).trim();
            int    lpFinal = Integer.parseInt(m.group(3));

            partidas.add(new RegistroPartida(fecha, ganador, lpFinal));

            victoriasPorJugador.merge(ganador, 1, Integer::sum);
        }
    }

    public int totalPartidas() { return partidas.size(); }

    public String jugadorMasVictorias() {
        return victoriasPorJugador.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("Sin datos");
    }

    public int victoriasDeJugador(String nombre) {
        return victoriasPorJugador.getOrDefault(nombre, 0);
    }

    public RegistroPartida partidaConMasLP() {
        return partidas.stream()
            .max(Comparator.comparingInt(RegistroPartida::getLpFinal))
            .orElse(null);
    }

    public List<RegistroPartida> getPartidas() { return partidas; }

    /** Mapa completo de victorias por jugador. */
    public HashMap<String, Integer> getVictoriasPorJugador() { return victoriasPorJugador; }

    public static class RegistroPartida {
        private final String fecha;
        private final String ganador;
        private final int    lpFinal;

        public RegistroPartida(String fecha, String ganador, int lpFinal) {
            this.fecha   = fecha;
            this.ganador = ganador;
            this.lpFinal = lpFinal;
        }

        public String getFecha()   { return fecha; }
        public String getGanador() { return ganador; }
        public int    getLpFinal() { return lpFinal; }
    }
}
