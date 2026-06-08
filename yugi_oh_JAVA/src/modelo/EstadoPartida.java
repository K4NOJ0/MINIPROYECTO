package modelo;

import java.util.ArrayList;

public class EstadoPartida {

    private final String nombreTurnoActual;
    private final EstadoJugador j1;
    private final EstadoJugador j2;

    public EstadoPartida(Jugador turnoActual, Jugador jugador1, Jugador jugador2) {
        this.nombreTurnoActual = turnoActual.getNombre();
        this.j1 = new EstadoJugador(jugador1);
        this.j2 = new EstadoJugador(jugador2);
    }

    public String getNombreTurnoActual() {
        return nombreTurnoActual;
    }

    public EstadoJugador getJ1() {
        return j1;
    }

    public EstadoJugador getJ2() {
        return j2;
    }

    public static class EstadoJugador {

        private final String nombre;
        private final int lp;
        private final ArrayList<String> mano;
        private final ArrayList<String> mazo;
        private final ArrayList<String> campoMonstruos;
        private final ArrayList<String> campoTrampas;

        public EstadoJugador(Jugador j) {
            this.nombre = j.getNombre();
            this.lp = j.getLp();

            this.mano = new ArrayList<>();
            for (Carta c : j.getMano()) mano.add(serializarCarta(c));
            
            this.mazo = new ArrayList<>();
            for (Carta c : j.getMazo()) mazo.add(serializarCarta(c));
            
            this.campoMonstruos = new ArrayList<>();
            for (Monstruo m : j.getCampo().getZonaMonstruos()) {
                campoMonstruos.add(m.getNombre() + "|" + (m.isEnModoAtaque() ? "ATK" : "DEF") + "|" + (m.isYaAtaco() ? "ATACO" : "NO_ATACO") + "|" + m.getAtk() + "|" + m.getDef() + "|" + m.getNivel());
            }
            
            this.campoTrampas = new ArrayList<>();
            for (CartaTrampa t : j.getCampo().getZonaTrampas()) {
                campoTrampas.add(t.getNombre() + "|" + (t.isActiva() ? "ACTIVA" : "OCULTA") + "|" + t.getEfecto() + "|" + t.getCondicion());
            }
        }

        private String serializarCarta(Carta c) {
            if (c instanceof Monstruo) {
                Monstruo m = (Monstruo) c;
                return m.getNombre() + "|Monstruo|" + m.getAtk() + "|" + m.getDef() + "|" + m.getNivel();
            } else if (c instanceof CartaMagica) {
                CartaMagica cm = (CartaMagica) c;
                return cm.getNombre() + "|Magia|" + cm.getEfecto();
            } else if (c instanceof CartaTrampa) {
                CartaTrampa ct = (CartaTrampa) c;
                return ct.getNombre() + "|Trampa|" + ct.getEfecto() + "|" + ct.getCondicion();
            }
            return c.getNombre() + "|" + c.getTipo();
        }

        public String getNombre() { return nombre; }
        public int getLp() { return lp; }
        public ArrayList<String> getMano() { return mano; }
        public ArrayList<String> getMazo() { return mazo; }
        public ArrayList<String> getCampoMonstruos() { return campoMonstruos; }
        public ArrayList<String> getCampoTrampas() { return campoTrampas; }
    }
}