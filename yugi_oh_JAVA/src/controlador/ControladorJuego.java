package controlador;

import java.util.ArrayList;
import java.util.Random;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import modelo.*;
import vista.TableroJuego;
import persistencia.GestorArchivos;

public class ControladorJuego {
    private Jugador j1, j2, turnoActual, rival;
    private Juego juego;
    private boolean primerTurno;
    private boolean yaJugoCartaEsteTurno;
    private boolean yaRoboEsteTurno;
    private Object cartaSeleccionadaMano;
    private Monstruo monstruoAtacanteSeleccionado;
    private TableroJuego vista;

    public ControladorJuego(String nombre1, String nombre2, TableroJuego vista) {
        this.vista = vista;
        this.juego = new Juego();
        this.j1 = new Jugador(nombre1);
        this.j2 = new Jugador(nombre2);

        ArrayList<Carta> mazo = construirMazo();
        juego.repartirCartas(j1, j2, mazo);

        turnoActual = new Random().nextBoolean() ? j1 : j2;
        rival = (turnoActual == j1) ? j2 : j1;
        primerTurno = true;
        yaJugoCartaEsteTurno = false;
        yaRoboEsteTurno = false;
        cartaSeleccionadaMano = null;
        monstruoAtacanteSeleccionado = null;
    }

    public void robarCarta() {
        if (!esMiTurno()) return;
        if (yaRoboEsteTurno) { 
            vista.agregarLog("Ya robaste una carta este turno."); 
            return; 
        }

        if (turnoActual.getMazo().isEmpty()) {
            finalizarJuego(rival, "¡Se quedó sin cartas en el mazo!");
            return;
        }
        turnoActual.robarCarta();
        yaRoboEsteTurno = true;
        vista.agregarLog(" " + turnoActual.getNombre() + " robó una carta. Mano: " + turnoActual.getMano().size());
        vista.actualizarTablero();
    }

    public void jugarCartaSeleccionada() {
        if (!esMiTurno()) return;
        if (!yaRoboEsteTurno) { 
            vista.agregarLog(" Debes robar una carta primero."); 
            return; 
        }
        if (yaJugoCartaEsteTurno) { 
            vista.agregarLog(" Ya jugaste una carta este turno."); 
            return; 
        }
        
        Carta cartaSel = vista.getCartaSeleccionada();
        if (cartaSel == null) { 
            vista.agregarLog(" Selecciona una carta de tu mano primero."); 
            return; 
        }

        // Logica para jugar la carta
    }

    public void invocarMonstruo(Monstruo m) {
        int modo = vista.getDialogos().preguntarModoInvocacion(m.getNombre());

        m.setEnModoAtaque(modo == 0 || modo == -1); // -1 es CLOSED_OPTION

        if (turnoActual.getCampo().invocarMonstruo(m)) {
            turnoActual.getMano().remove(m);
            vista.agregarLog(turnoActual.getNombre() + " invocó a " + m.getNombre() +
                " (" + (m.isEnModoAtaque() ? "Ataque" : "Defensa") + ")");
            yaJugoCartaEsteTurno = true;
            vista.limpiarSeleccionMano();

        } else {
            vista.agregarLog(" Campo lleno. No puedes invocar más monstruos.");
        }
    }

    public void atacarConMonstruo(Monstruo atacante) {
        if (!esMiTurno()) return;
        if (!yaRoboEsteTurno) { 
            vista.agregarLog(" Debes robar primero."); 
            return; 
        }
        if (primerTurno && turnoActual == (rival == j2 ? j1 : j2)) {
            vista.agregarLog(" No puedes atacar en el primer turno.");
            return;
        }
        if (atacante.isYaAtaco()) { 
            vista.agregarLog(" " + atacante.getNombre() + " ya atacó este turno."); 
            return; 
        }
        if (!atacante.isEnModoAtaque()) { 
            vista.agregarLog(" Solo los monstruos en modo Ataque pueden atacar."); 
            return; 
        }

        if (rival.getCampo().getZonaMonstruos().isEmpty()) {
            String result = juego.atacarDirecto(atacante, rival);
            vista.agregarLog(result);
        } else {
            monstruoAtacanteSeleccionado = atacante;
            vista.agregarLog(" " + atacante.getNombre() + " listo para atacar. Selecciona el monstruo objetivo en el campo rival.");
            vista.resaltarCampoRival(true);
            return;
        }

        vista.actualizarTablero();
        verificarFinJuego();
    }

    public void atacarDirecto() {
        if (!esMiTurno()) return;
        if (!rival.getCampo().getZonaMonstruos().isEmpty()) {
            vista.agregarLog("El rival tiene monstruos. No puedes atacar directamente.");
            return;
        }
        if (turnoActual.getCampo().getZonaMonstruos().isEmpty()) {
            vista.agregarLog("No tienes monstruos para atacar.");
            return;
        }

        for (Monstruo m : turnoActual.getCampo().getZonaMonstruos()) {
            if (!m.isYaAtaco() && m.isEnModoAtaque()) {
                atacarConMonstruo(m);
                return;
            }
        }
        vista.agregarLog(" Ningún monstruo puede atacar directamente.");
    }

    public void terminarTurno() {
        if (!esMiTurno()) return;

        turnoActual.resetTurno();

        for (CartaTrampa t : turnoActual.getCampo().getZonaTrampas()) {
            if (t.getIdEfecto().equals("proteccion_waboku") && t.isActiva()) {
                turnoActual.setWabokuActivo(true);
                turnoActual.getCampo().getZonaTrampas().remove(t);
                vista.agregarLog(" Waboku activado para proteger a " + turnoActual.getNombre() + " este turno.");
                break;
            }
        }

        Jugador temp = turnoActual;
        turnoActual = rival;
        rival = temp;

        primerTurno = false;
        yaJugoCartaEsteTurno = false;
        yaRoboEsteTurno = false;
        vista.limpiarSeleccionMano();
        monstruoAtacanteSeleccionado = null;

        vista.agregarLog("---");
        vista.agregarLog(" Turno de " + turnoActual.getNombre() + ". Haz clic en 'Robar Carta' para comenzar.");
        vista.actualizarTablero();
    }

    public void verificarFinJuego() {
        if (j1.getLp() <= 0) {
            finalizarJuego(j2, "¡" + j1.getNombre() + " llegó a 0 LP!");
        } else if (j2.getLp() <= 0) {
            finalizarJuego(j1, "¡" + j2.getNombre() + " llegó a 0 LP!");
        } else if (j1.getMazo().isEmpty() && j1.getMano().isEmpty()) {
            finalizarJuego(j2, "¡" + j1.getNombre() + " se quedó sin cartas!");
        } else if (j2.getMazo().isEmpty() && j2.getMano().isEmpty()) {
            finalizarJuego(j1, "¡" + j2.getNombre() + " se quedó sin cartas!");
        }
    }

    public void finalizarJuego(Jugador ganador, String razon) {
        vista.actualizarTablero();
        String msg = "<html><center>" +
            "<h1>¡DUELO FINALIZADO!</h1>" +
            "<h2>Ganador: " + ganador.getNombre() + "</h2>" +
            "<p>" + razon + "</p>" +
            "<p><i>\"Confía en el corazón de las cartas\" — Yugi Muto</i></p>" +
            "</center></html>";

        vista.getDialogos().mostrarMensajeFinJuego(msg);

        // Guardar resultado
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logResultado = String.format("[%s] Ganador: %s | LP Finales: %d", fecha, ganador.getNombre(), ganador.getLp());
        GestorArchivos.getInstance().guardarResultado(logResultado);

        int r = vista.getDialogos().preguntarRevancha();
        vista.dispose();
        if (r == 0) { // 0 es YES_OPTION
            // Reiniciar juego
        }
    }

    private boolean esMiTurno() {
        return true;
    }

    public Jugador getJ1() { return j1; }
    public Jugador getJ2() { return j2; }
    public Jugador getTurnoActual() { return turnoActual; }
    public Jugador getRival() { return rival; }
    public Juego getJuego() { return juego; }
    public boolean isPrimerTurno() { return primerTurno; }
    public boolean isYaJugoCartaEsteTurno() { return yaJugoCartaEsteTurno; }
    public boolean isYaRoboEsteTurno() { return yaRoboEsteTurno; }
    public Monstruo getMonstruoAtacanteSeleccionado() { return monstruoAtacanteSeleccionado; }
    public void setMonstruoAtacanteSeleccionado(Monstruo m) { this.monstruoAtacanteSeleccionado = m; }
    public void setVista(TableroJuego v) { this.vista = v; }

    private ArrayList<Carta> construirMazo() {
        ArrayList<Carta> mazo = new ArrayList<>();
        mazo.add(new Monstruo("Dragón Blanco de Ojos Azules", 3000, 2500, 8));
        mazo.add(new Monstruo("Mago Oscuro", 2500, 2100, 7));
        mazo.add(new Monstruo("Calavera Invocada", 2500, 1200, 6));
        mazo.add(new Monstruo("Dragón Negro de Ojos Rojos", 2400, 2000, 7));
        mazo.add(new Monstruo("Jinzo", 2400, 1500, 6));
        mazo.add(new Monstruo("Destructor de Espadas", 2600, 2300, 7));
        mazo.add(new Monstruo("Chica Maga Oscura", 2000, 1700, 6));
        mazo.add(new Monstruo("La Jinn", 1800, 1000, 4));
        mazo.add(new Monstruo("Buey de Batalla", 1700, 1000, 4));
        mazo.add(new Monstruo("Neo el Espadachín Mágico", 1700, 1000, 4));
        mazo.add(new Monstruo("Guardián Celta", 1400, 1200, 4));
        mazo.add(new Monstruo("Elfa Géminis", 1900, 900, 4));
        mazo.add(new Monstruo("Soldado Archidemonio", 1900, 1500, 4));
        mazo.add(new Monstruo("Vorcerader", 1900, 1200, 4));
        mazo.add(new Monstruo("Gigante Soldado de Piedra", 1300, 2000, 3));
        mazo.add(new Monstruo("Elfa Mística", 800, 2000, 4));
        mazo.add(new Monstruo("Aqua Madoor", 1200, 2000, 4));
        mazo.add(new Monstruo("Muro de Ilusión", 1000, 1850, 4));
        mazo.add(new Monstruo("Segador del Espíritu", 300, 200, 3));
        mazo.add(new Monstruo("Kuriboh", 300, 200, 1));
        mazo.add(new Monstruo("Sangan", 1000, 600, 3));
        mazo.add(new Monstruo("Bruja del Bosque Negro", 1100, 1200, 4));
        mazo.add(new Monstruo("Insecto Comehombres", 450, 600, 2));
        mazo.add(new Monstruo("Dama Arpía", 1300, 1400, 4));
        mazo.add(new Monstruo("Hermanas Arpía", 1950, 2100, 6));
        mazo.add(new Monstruo("Gearfried el Caballero de Hierro", 1800, 1600, 4));
        mazo.add(new Monstruo("Fuerza Exiliada", 1000, 1000, 4));
        mazo.add(new Monstruo("Hada de Inyección Lily", 400, 1500, 3));
        mazo.add(new Monstruo("Mago del Tiempo", 500, 400, 2));
        mazo.add(new Monstruo("Dragón Bebé", 1200, 700, 3));
        
        mazo.add(new CartaMagica("Olla de la Codicia", "robar"));
        mazo.add(new CartaMagica("Polvo del Cosmos", "robar"));
        mazo.add(new CartaMagica("Escudo Místico", "recuperar"));
        mazo.add(new CartaMagica("Expansión Astral", "recuperar"));
        mazo.add(new CartaMagica("Renacer del Espíritu", "recuperar"));
        mazo.add(new CartaMagica("Universo Atómico", "destruir"));
        mazo.add(new CartaMagica("Lluvia de Relámpagos", "destruir"));
        mazo.add(new CartaMagica("Trampa de Araña", "destruir"));
        mazo.add(new CartaMagica("Terraformación", "boost"));
        mazo.add(new CartaMagica("Poder Oscuro", "boost"));
        
        mazo.add(new CartaTrampa("Agujero Trampa", "agujero_trampa", "invocacion"));
        mazo.add(new CartaTrampa("Fuerza Espejo", "fuerza_espejo", "ataque"));
        mazo.add(new CartaTrampa("Negar Ataque", "negar_ataque", "ataque"));
        mazo.add(new CartaTrampa("Cilindro Mágico", "cilindro_magico", "ataque"));
        mazo.add(new CartaTrampa("Tributo Torrencial", "tributo_torrencial", "invocacion"));
        mazo.add(new CartaTrampa("Agujero Trampa Sin Fondo", "agujero_sin_fondo", "invocacion"));
        mazo.add(new CartaTrampa("Evacuación Forzada", "evacuacion_forzada", "ataque"));
        mazo.add(new CartaTrampa("Blindaje Sakuretsu", "blindaje_sakuretsu", "ataque"));
        mazo.add(new CartaTrampa("Protección Waboku", "proteccion_waboku", "ataque"));
        mazo.add(new CartaTrampa("Tornado de Polvo", "tornado_polvo", "ataque"));
        return mazo;
    }
}
