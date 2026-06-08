package modelo;

import modelo.*;
import java.lang.reflect.Constructor;
import modelo.EstadoPartida;
import modelo.EstadoPartida.EstadoJugador;
import java.util.List;
import java.util.ArrayList;

public class PartidaSerializer {

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

    public static DatosPartida deserializar(String contenido) {
        if (contenido == null || contenido.isBlank())
            return null;
        String[] lineas = contenido.split("\n");
        DatosPartida datos = new DatosPartida();
        int seccion = 0;
        DatosJugador jugActual = null;
        for (String linea : lineas) {
            linea = linea.trim();
            if (linea.isEmpty())
                continue;
            if (linea.equals("[PARTIDA]")) {
                seccion = 0;
                continue;
            }
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
            if (partes.length < 2)
                continue;
            String clave = partes[0].trim();
            String valor = partes[1].trim();
            if (seccion == 0) {
                if (clave.equals("TurnoActual"))
                    datos.nombreTurnoActual = valor;
            } else if (jugActual != null) {
                switch (clave) {
                    case "Nombre":
                        jugActual.nombre = valor;
                        break;
                    case "LP":
                        jugActual.lp = Integer.parseInt(valor);
                        break;
                    case "Mano":
                        jugActual.mano = parsearCartas(valor);
                        break;
                    case "Mazo":
                        jugActual.mazo = parsearCartas(valor);
                        break;
                    case "CampoMonstruos":
                        jugActual.campoMonstruos = parsearMonstruos(valor);
                        break;
                    case "CampoTrampas":
                        jugActual.campoTrampas = parsearTrampas(valor);
                        break;
                }
            }
        }
        return datos;
    }

    private static String serializeCarta(Carta carta) {
        String className = carta.getClass().getName();
        if (carta instanceof Monstruo) {
            Monstruo m = (Monstruo) carta;
            return className + ";" + m.getNombre() + ";" + m.getAtk() + ";" + m.getDef() + ";" + m.getNivel();
        } else if (carta instanceof CartaMagica) {
            CartaMagica cm = (CartaMagica) carta;
            return className + ";" + cm.getNombre() + ";" + cm.getEfecto();
        } else if (carta instanceof CartaTrampa) {
            CartaTrampa ct = (CartaTrampa) carta;
            return className + ";" + ct.getNombre() + ";" + ct.getEfecto() + ";" + ct.getCondicion();
        } else {
            return className + ";" + carta.getNombre();
        }
    }

    private static ArrayList<Carta> parsearCartas(String valor) {
        ArrayList<Carta> lista = new ArrayList<>();
        if (valor.isBlank())
            return lista;
        for (String entrada : valor.split(",")) {
            entrada = entrada.trim();
            if (entrada.isEmpty())
                continue;
            String[] partes = entrada.split("\\|");
            if (partes.length < 2)
                continue;
            String nombre = partes[0];
            String tipo = partes[1];
            Carta carta;
            if (tipo.equalsIgnoreCase("Monstruo")) {
                int atk = partes.length > 2 ? Integer.parseInt(partes[2]) : 0;
                int def = partes.length > 3 ? Integer.parseInt(partes[3]) : 0;
                int nivel = partes.length > 4 ? Integer.parseInt(partes[4]) : 1;
                carta = new Monstruo(nombre, atk, def, nivel);
            } else if (tipo.equalsIgnoreCase("Magia") || tipo.equalsIgnoreCase("CartaMagica")) {
                String efecto = partes.length > 2 ? partes[2] : "desconocido";
                carta = new CartaMagica(nombre, efecto);
            } else if (tipo.equalsIgnoreCase("Trampa") || tipo.equalsIgnoreCase("CartaTrampa")) {
                String efecto = partes.length > 2 ? partes[2] : "desconocido";
                String condicion = partes.length > 3 ? partes[3] : "desconocido";
                carta = new CartaTrampa(nombre, efecto, condicion);
            } else {
                carta = new Monstruo(nombre, 0, 0, 1);
            }
            lista.add(carta);
        }
        return lista;
    }

    private static ArrayList<Monstruo> parsearMonstruos(String valor) {
        ArrayList<Monstruo> lista = new ArrayList<>();
        if (valor.isBlank())
            return lista;
        for (String entrada : valor.split(",")) {
            entrada = entrada.trim();
            if (entrada.isEmpty())
                continue;
            String[] partes = entrada.split("\\|");
            if (partes.length < 3)
                continue;
            int atk = partes.length > 3 ? Integer.parseInt(partes[3]) : 0;
            int def = partes.length > 4 ? Integer.parseInt(partes[4]) : 0;
            int nivel = partes.length > 5 ? Integer.parseInt(partes[5]) : 1;
            Monstruo m = new Monstruo(partes[0], atk, def, nivel);
            m.setEnModoAtaque(partes[1].equals("ATK"));
            m.setYaAtaco(partes[2].equals("ATACO"));
            lista.add(m);
        }
        return lista;
    }

    private static ArrayList<CartaTrampa> parsearTrampas(String valor) {
        ArrayList<CartaTrampa> lista = new ArrayList<>();
        if (valor.isBlank())
            return lista;
        for (String entrada : valor.split(",")) {
            entrada = entrada.trim();
            if (entrada.isEmpty())
                continue;
            String[] partes = entrada.split("\\|");
            if (partes.length < 2)
                continue;
            String efecto = partes.length > 2 ? partes[2] : "desconocido";
            String condicion = partes.length > 3 ? partes[3] : "desconocido";
            CartaTrampa t = new CartaTrampa(partes[0], efecto, condicion);
            t.setActiva(partes[1].equals("ACTIVA"));
            lista.add(t);
        }
        return lista;
    }

    public static class DatosPartida {
        public String nombreTurnoActual;
        public DatosJugador j1;
        public DatosJugador j2;
    }

    public static class DatosJugador {
        public String nombre;
        public int lp;
        public ArrayList<Carta> mano = new ArrayList<>();
        public ArrayList<Carta> mazo = new ArrayList<>();
        public ArrayList<Monstruo> campoMonstruos = new ArrayList<>();
        public ArrayList<CartaTrampa> campoTrampas = new ArrayList<>();
    }
}
