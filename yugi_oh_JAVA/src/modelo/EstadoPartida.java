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
            this.mazo = new ArrayList<>();
            this.campoMonstruos = new ArrayList<>();
            this.campoTrampas = new ArrayList<>();

            // MANO
            for (Carta c : j.getMano()) {

                if (c instanceof Monstruo m) {

                    mano.add(
                        m.getNombre() + "|" +
                        m.getAtk() + "|" +
                        m.getDef() + "|" +
                        m.getNivel() + "|" +
                        "Monstruo"
                    );

                } else {

                    mano.add(c.getNombre() + "|" + c.getTipo());
                }
            }

            // MAZO
            for (Carta c : j.getMazo()) {

                if (c instanceof Monstruo m) {

                    mazo.add(
                        m.getNombre() + "|" +
                        m.getAtk() + "|" +
                        m.getDef() + "|" +
                        m.getNivel() + "|" +
                        "Monstruo"
                    );

                } else {

                    mazo.add(c.getNombre() + "|" + c.getTipo());
                }
            }

            // CAMPO MONSTRUOS
            for (Monstruo m : j.getCampo().getZonaMonstruos()) {

                campoMonstruos.add(
                    m.getNombre() + "|" +
                    m.getAtk() + "|" +
                    m.getDef() + "|" +
                    m.getNivel() + "|" +
                    (m.isEnModoAtaque() ? "ATK" : "DEF") + "|" +
                    (m.isYaAtaco() ? "ATACO" : "NO_ATACO")
                );
            }

            // CAMPO TRAMPAS
            for (CartaTrampa t : j.getCampo().getZonaTrampas()) {

                campoTrampas.add(
                    t.getNombre() + "|" +
                    (t.isActiva() ? "ACTIVA" : "OCULTA")
                );
            }
        }

        public String getNombre() {
            return nombre;
        }

        public int getLp() {
            return lp;
        }

        public ArrayList<String> getMano() {
            return mano;
        }

        public ArrayList<String> getMazo() {
            return mazo;
        }

        public ArrayList<String> getCampoMonstruos() {
            return campoMonstruos;
        }

        public ArrayList<String> getCampoTrampas() {
            return campoTrampas;
        }
    }
}