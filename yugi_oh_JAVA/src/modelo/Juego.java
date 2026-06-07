package modelo;

import java.util.*;
import excepciones.CartaInvalidaException;

public class Juego {

    private Jugador jugadorActual;
    private int turnoActual;

    private Queue<String>          colaEventos;
    private HashMap<String, Carta> registroCartas;

    public Juego() {
        colaEventos    = new LinkedList<>();
        registroCartas = new HashMap<>();
    }

    public void encolarEvento(String evento) { colaEventos.offer(evento); }
    public String siguienteEvento()          { return colaEventos.poll(); }
    public boolean hayEventosPendientes()    { return !colaEventos.isEmpty(); }

    public void registrarCarta(Carta carta) {
        registroCartas.put(carta.getNombre(), carta);
    }

    public Carta buscarCarta(String nombre) throws CartaInvalidaException {
        Carta c = registroCartas.get(nombre);
        if (c == null) throw new CartaInvalidaException(
            "La carta '" + nombre + "' no existe en el registro.");
        return c;
    }

    public ArrayList<Carta> construirMazo() {
        ArrayList<Carta> mazo = new ArrayList<>();
        mazo.add(new Monstruo("Dragon Blanco", 3000, 2500, 8));
        mazo.add(new Monstruo("Kuriboh", 300, 200, 1));
        mazo.add(new CartaMagica("Olla de la Codicia", "robar"));
        mazo.add(new CartaMagica("Curacion", "recuperar"));
        mazo.add(new CartaTrampa("Negar Ataque", "negar_ataque", "cuando_atacan"));
        mazo.add(new CartaTrampa("Fuerza Espejo", "fuerza_espejo", "cuando_atacan"));
        for (Carta c : mazo) registrarCarta(c);
        return mazo;
    }

    public void barajar(ArrayList<Carta> mazo) {
        Collections.shuffle(mazo);
    }

   public void repartirCartas(
        Jugador j1,
        Jugador j2,
        ArrayList<Carta> mazo) {

    ArrayList<Carta> mazoBarajado =
            new ArrayList<>(mazo);

    Collections.shuffle(mazoBarajado);

    for (Carta c : mazoBarajado) {
        registrarCarta(c);
    }

    for (int i = 0; i < mazoBarajado.size(); i++) {

        if (i % 2 == 0)
            j1.getMazoStack().push(mazoBarajado.get(i));
        else
            j2.getMazoStack().push(mazoBarajado.get(i));
    }

    for (int i = 0; i < 5; i++) {
        j1.robarCarta();
        j2.robarCarta();
    }

    encolarEvento(
            "Cartas repartidas. Cada jugador tiene 5 cartas.");
}
    public void iniciarJuego(Jugador j1, Jugador j2) {
        jugadorActual = j1;
        turnoActual   = 1;
        ArrayList<Carta> mazo = construirMazo();
        repartirCartas(j1, j2, mazo);
        encolarEvento("¡El duelo comenzó! Turno de " + j1.getNombre());
    }

    public String robarCarta(Jugador jugador) {
        String r = jugador.robarCarta();
        encolarEvento(r);
        return r;
    }

    public String invocarMonstruo(Jugador jugador, Monstruo monstruo) {
        boolean inv = jugador.getCampo().invocarMonstruo(monstruo);
        if (inv) {
            jugador.getManoLinked().remove(monstruo);
            String msg = monstruo.getNombre() + " fue invocado";
            encolarEvento(msg);
            return msg;
        }
        return "No hay espacio";
    }

    public String colocarTrampa(Jugador jugador, CartaTrampa trampa) {
        boolean col = jugador.getCampo().colocarTrampa(trampa);
        if (col) {
            jugador.getManoLinked().remove(trampa);
            encolarEvento("Trampa colocada: " + trampa.getNombre());
            return "Trampa colocada";
        }
        return "No hay espacio";
    }

    public void pasarTurno(Jugador j1, Jugador j2) {
        jugadorActual = (jugadorActual == j1) ? j2 : j1;
        turnoActual++;
        encolarEvento("Turno " + turnoActual + ": " + jugadorActual.getNombre());
    }

    public boolean chequearFinDePartida(Jugador j1, Jugador j2) {
        return j1.getLp() <= 0 || j2.getLp() <= 0;
    }


    public String atacarMonstruo(Monstruo atacante, Monstruo defensor,
                                  Jugador atacanteJugador, Jugador defensorJugador) {
        StringBuilder log = new StringBuilder();

        // Revisar trampas activas del defensor
        for (CartaTrampa t : new ArrayList<>(defensorJugador.getCampo().getZonaTrampas())) {
            if (!t.isActiva()) continue;
            switch (t.getIdEfecto()) {
                case "negar_ataque":
                    defensorJugador.getCampo().getZonaTrampasLinked().remove(t);
                    atacante.setYaAtaco(true);
                    encolarEvento("ATAQUE CANCELADO por " + t.getNombre());
                    return "ATAQUE CANCELADO";
                case "blindaje_sakuretsu":
                    defensorJugador.getCampo().getZonaTrampasLinked().remove(t);
                    atacanteJugador.getCampo().getZonaMonstruosLinked().remove(atacante);
                    atacante.setYaAtaco(true);
                    encolarEvento(atacante.getNombre() + " destruido por " + t.getNombre());
                    return atacante.getNombre() + " destruido";
                case "fuerza_espejo":
                    defensorJugador.getCampo().getZonaTrampasLinked().remove(t);
                    atacanteJugador.getCampo().getZonaMonstruosLinked().clear();
                    encolarEvento("Fuerza Espejo activa");
                    return "Todos los monstruos destruidos";
                case "cilindro_magico":
                    defensorJugador.getCampo().getZonaTrampasLinked().remove(t);
                    atacanteJugador.recibirDano(atacante.getAtk());
                    atacante.setYaAtaco(true);
                    encolarEvento("Cilindro Mágico: daño reflejado");
                    return "Daño reflejado";
            }
        }

        atacante.setYaAtaco(true);

        if (defensor.isModoAtaque()) {
            if (atacante.getAtk() > defensor.getAtk()) {
                int diff = atacante.getAtk() - defensor.getAtk();
                defensorJugador.getCampo().getZonaMonstruosLinked().remove(defensor);
                defensorJugador.recibirDano(diff);
                log.append(atacante.getNombre()).append(" destruyó a ").append(defensor.getNombre());
            } else if (atacante.getAtk() < defensor.getAtk()) {
                int diff = defensor.getAtk() - atacante.getAtk();
                atacanteJugador.getCampo().getZonaMonstruosLinked().remove(atacante);
                atacanteJugador.recibirDano(diff);
                log.append(defensor.getNombre()).append(" destruyó a ").append(atacante.getNombre());
            } else {
                defensorJugador.getCampo().getZonaMonstruosLinked().remove(defensor);
                atacanteJugador.getCampo().getZonaMonstruosLinked().remove(atacante);
                log.append("Ambos monstruos destruidos");
            }
        } else {
            if (atacante.getAtk() > defensor.getDef()) {
                defensorJugador.getCampo().getZonaMonstruosLinked().remove(defensor);
                log.append(defensor.getNombre()).append(" destruido en defensa");
            } else if (atacante.getAtk() < defensor.getDef()) {
                int diff = defensor.getDef() - atacante.getAtk();
                atacanteJugador.recibirDano(diff);
                log.append("Ataque bloqueado");
            } else {
                defensorJugador.getCampo().getZonaMonstruosLinked().remove(defensor);
                log.append("Empate");
            }
        }

        String resultado = log.toString();
        encolarEvento(resultado);
        return resultado;
    }

    public String aplicarEfectoMagica(CartaMagica carta, Jugador jugador, Jugador rival) {
        String resultado = carta.activarEfecto(jugador, rival);
        jugador.getManoLinked().remove(carta);
        encolarEvento(jugador.getNombre() + " activó " + carta.getNombre() + ": " + resultado);
        return resultado;
    }

    public String atacarDirecto(Monstruo atacante, Jugador defensorJugador) {
        for (CartaTrampa t : new ArrayList<>(defensorJugador.getCampo().getZonaTrampas())) {
            if (t.isActiva() && t.getIdEfecto().equals("negar_ataque")) {
                defensorJugador.getCampo().getZonaTrampasLinked().remove(t);
                atacante.setYaAtaco(true);
                encolarEvento("ATAQUE CANCELADO por " + t.getNombre());
                return "ATAQUE CANCELADO";
            }
        }
        atacante.setYaAtaco(true);
        defensorJugador.recibirDano(atacante.getAtk());
        String msg = atacante.getNombre() + " hizo ataque directo";
        encolarEvento(msg);
        return msg;
    }

    public HashMap<String, Carta> getRegistroCartas() { return registroCartas; }
    public Queue<String>          getColaEventos()    { return colaEventos; }
}