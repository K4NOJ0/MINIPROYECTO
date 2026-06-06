package persistencia;

import modelo.*;
import patrones.EstadoPartida;
import patrones.EstadoPartida.EstadoJugador;

import java.util.ArrayList;

/**
 * Convierte un EstadoPartida a texto y lo reconstruye desde texto.
 *
 * Formato del archivo:
 *   [PARTIDA]
 *   TurnoActual=NOMBRE
 *
 *   [JUGADOR1]
 *   Nombre=X
 *   LP=8000
 *   Mano=CartaA|Monstruo,CartaB|Magia,...
 *   Mazo=CartaC|Trampa,...
 *   CampoMonstruos=Dragon|ATK|NO_ATACO,...
 *   CampoTrampas=Agujero|OCULTA,...
 */
public class PartidaSerializer {

    // ── Serializar (Estado → texto) ───────────────────────────────────────────
    public static String serializar(EstadoPartida estado) {
        StringBuilder sb = new StringBuilder();
        sb.append("[PARTIDA]\n");
        sb.append("TurnoActual=").append(estado.getNombreTurnoActual()).append("\n\n");
        sb.append(serializarJugador(estado.getJ1(), 1));
        sb.append(serializarJugador(estado.getJ2(), 2));
        return sb.toString();
    }

    private static String serializarJugador(EstadoJugador j, int numero) {
        StringBuilder sb = new StringBuilder();
        sb.append("[JUGADOR").append(numero).append("]\n");
        sb.append("Nombre=").append(j.getNombre()).append("\n");
        sb.append("LP=").append(j.getLp()).append("\n");
        sb.append("Mano=").append(String.join(",", j.getMano())).append("\n");
        sb.append("Mazo=").append(String.join(",", j.getMazo())).append("\n");
        sb.append("CampoMonstruos=").append(String.join(",", j.getCampoMonstruos())).append("\n");
        sb.append("CampoTrampas=").append(String.join(",", j.getCampoTrampas())).append("\n\n");
        return sb.toString();
    }

    // ── Deserializar (texto → DatosPartida) ───────────────────────────────────
    /**
     * Lee el texto guardado y reconstruye los datos necesarios para
     * reiniciar la partida desde ese punto.
     */
    public static DatosPartida deserializar(String contenido) {
        if (contenido == null || contenido.isBlank()) return null;

        String[] lineas = contenido.split("\n");
        DatosPartida datos = new DatosPartida();

        int seccion = 0; // 0=cabecera, 1=jugador1, 2=jugador2
        DatosJugador jugActual = null;

        for (String linea : lineas) {
            linea = linea.trim();
            if (linea.isEmpty()) continue;

            if (linea.equals("[PARTIDA]")) { seccion = 0; continue; }
            if (linea.equals("[JUGADOR1]")) {
                seccion = 1;
                datos.j1 = new DatosJugador();
                jugActual = datos.j1;
                continue;
            }
            if (linea.equals("[JUGADOR2]")) {
                seccion = 2;
                datos.j2 = new DatosJugador();
                jugActual = datos.j2;
                continue;
            }

            String[] partes = linea.split("=", 2);
            if (partes.length < 2) continue;
            String clave = partes[0].trim();
            String valor = partes[1].trim();

            if (seccion == 0) {
                if (clave.equals("TurnoActual")) datos.nombreTurnoActual = valor;
            } else if (jugActual != null) {
                switch (clave) {
                    case "Nombre":        jugActual.nombre = valor; break;
                    case "LP":            jugActual.lp = Integer.parseInt(valor); break;
                    case "Mano":          jugActual.mano = parsearCartas(valor); break;
                    case "Mazo":          jugActual.mazo = parsearCartas(valor); break;
                    case "CampoMonstruos":jugActual.campoMonstruos = parsearMonstruos(valor); break;
                    case "CampoTrampas":  jugActual.campoTrampas = parsearTrampas(valor); break;
                }
            }
        }
        return datos;
    }

    // ── Parsers de cada tipo de carta ─────────────────────────────────────────

    private static ArrayList<Carta> parsearCartas(String valor) {
        ArrayList<Carta> lista = new ArrayList<>();
        if (valor.isBlank()) return lista;
        for (String entrada : valor.split(",")) {
            entrada = entrada.trim();
            if (entrada.isEmpty()) continue;
            String[] partes = entrada.split("\\|");
            if (partes.length < 2) continue;
            String nombre = partes[0];
            String tipo   = partes[1];
            switch (tipo) {
                case "Monstruo": lista.add(new Monstruo(nombre, 0, 0, 1)); break;
                case "Magia":    lista.add(new CartaMagica(nombre, "desconocido")); break;
                case "Trampa":   lista.add(new CartaTrampa(nombre, "desconocido", "desconocido")); break;
            }
        }
        return lista;
    }

    private static ArrayList<Monstruo> parsearMonstruos(String valor) {
        ArrayList<Monstruo> lista = new ArrayList<>();
        if (valor.isBlank()) return lista;
        for (String entrada : valor.split(",")) {
            entrada = entrada.trim();
            if (entrada.isEmpty()) continue;
            // formato: nombre|ATK|ATACO  o  nombre|DEF|NO_ATACO
            String[] partes = entrada.split("\\|");
            if (partes.length < 3) continue;
            Monstruo m = new Monstruo(partes[0], 0, 0, 1);
            m.setEnModoAtaque(partes[1].equals("ATK"));
            m.setYaAtaco(partes[2].equals("ATACO"));
            lista.add(m);
        }
        return lista;
    }

    private static ArrayList<CartaTrampa> parsearTrampas(String valor) {
        ArrayList<CartaTrampa> lista = new ArrayList<>();
        if (valor.isBlank()) return lista;
        for (String entrada : valor.split(",")) {
            entrada = entrada.trim();
            if (entrada.isEmpty()) continue;
            // formato: nombre|ACTIVA  o  nombre|OCULTA
            String[] partes = entrada.split("\\|");
            if (partes.length < 2) continue;
            CartaTrampa t = new CartaTrampa(partes[0], "desconocido", "desconocido");
            t.setActiva(partes[1].equals("ACTIVA"));
            lista.add(t);
        }
        return lista;
    }

    // ── Clases de datos reconstruidos ─────────────────────────────────────────

    public static class DatosPartida {
        public String       nombreTurnoActual;
        public DatosJugador j1;
        public DatosJugador j2;
    }

    public static class DatosJugador {
        public String              nombre;
        public int                 lp;
        public ArrayList<Carta>    mano          = new ArrayList<>();
        public ArrayList<Carta>    mazo          = new ArrayList<>();
        public ArrayList<Monstruo> campoMonstruos = new ArrayList<>();
        public ArrayList<CartaTrampa> campoTrampas = new ArrayList<>();
    }
}
