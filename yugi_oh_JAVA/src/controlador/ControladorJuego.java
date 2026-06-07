
package controlador;
import modelo.CargadorCartas;
import modelo.CargadorCartas;
import java.util.ArrayList;
import java.util.Random;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import modelo.*;
import modelo.EstadoPartida;
import modelo.GestorMementos;
import modelo.GestorArchivos;
import modelo.PartidaSerializer;
import modelo.PartidaSerializer.DatosJugador;
import modelo.PartidaSerializer.DatosPartida;
import vista.TableroJuego;

public class ControladorJuego implements TableroJuego.TableroListener {

    private Jugador j1, j2, turnoActual, rival;
    private Juego   juego;
    private boolean primerTurno;
    private boolean yaJugoCartaEsteTurno;
    private boolean yaRoboEsteTurno;
    private Carta   cartaSeleccionadaMano;
    private Monstruo monstruoAtacanteSeleccionado;
    private TableroJuego vista;

    // GestorMementos usa un Stack internamente para guardar estados (undo)
    private GestorMementos gestorMementos = new GestorMementos();

    private static final String ARCHIVO_PARTIDA = "partida_guardada.txt";

    public ControladorJuego(String nombre1, String nombre2, TableroJuego vista) {
        this.vista = vista;
        this.juego = new Juego();
        this.j1    = new Jugador(nombre1);
        this.j2    = new Jugador(nombre2);

        ArrayList<Carta> mazo = construirMazo();
        juego.repartirCartas(j1, j2, mazo);

        turnoActual = new Random().nextBoolean() ? j1 : j2;
        rival       = (turnoActual == j1) ? j2 : j1;
        primerTurno          = true;
        yaJugoCartaEsteTurno = false;
        yaRoboEsteTurno      = false;
        cartaSeleccionadaMano        = null;
        monstruoAtacanteSeleccionado = null;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  IMPLEMENTACIÓN DE TableroListener
    // ════════════════════════════════════════════════════════════════════════

    @Override public void onRobarCarta()               { robarCarta(); }
    @Override public void onAtacarConMonstruo(Monstruo m) { atacarConMonstruo(m); }
    @Override public void onAtacarDirecto()            { atacarDirecto(); }
    @Override public void onTerminarTurno()            { terminarTurno(); }

    @Override
    public void onJugarCarta(Carta carta) {
        this.cartaSeleccionadaMano = carta;
        jugarCartaSeleccionada();
    }

    @Override
    public void onSeleccionarObjetivo(Monstruo objetivo) {
        if (monstruoAtacanteSeleccionado == null) return;
        String result = juego.atacarMonstruo(monstruoAtacanteSeleccionado, objetivo, turnoActual, rival);
        vista.agregarLog(result);
        monstruoAtacanteSeleccionado = null;
        actualizarVista();
        verificarFinJuego();
    }

    @Override
    public void onActivarTrampa(CartaTrampa trampa) {
        vista.agregarLog(" Trampa activada: " + trampa.getNombre());
        actualizarVista();
    }

    // ── GUARDAR PARTIDA ──────────────────────────────────────────────────────
    @Override
    public void onGuardarPartida() {
        // 1. Guarda el estado actual en el GestorMementos (Stack de undo)
        EstadoPartida estado = new EstadoPartida(turnoActual, j1, j2);
        gestorMementos.guardarEstado(estado);

        // 2. Serializa el estado a texto con BufferedWriter (via GestorArchivos)
        String contenido = PartidaSerializer.serializar(estado);
        GestorArchivos.getInstance().guardarPartida(ARCHIVO_PARTIDA, contenido);

        vista.agregarLog("💾 Partida guardada correctamente.");
        vista.mostrarMensaje("Partida Guardada",
            "La partida fue guardada en:\n" + ARCHIVO_PARTIDA);
    }

    // ── CARGAR PARTIDA ───────────────────────────────────────────────────────
    @Override
    public void onCargarPartida() {
        // 1. Lee el archivo con BufferedReader (via GestorArchivos)
        String contenido = GestorArchivos.getInstance().cargarPartida(ARCHIVO_PARTIDA);

        if (contenido == null || contenido.isBlank()) {
            vista.mostrarMensaje("Error", "No hay ninguna partida guardada.");
            vista.agregarLog("📂 No se encontró partida guardada.");
            return;
        }

        // 2. Deserializa el texto a objetos
        DatosPartida datos = PartidaSerializer.deserializar(contenido);
        if (datos == null || datos.j1 == null || datos.j2 == null) {
            vista.mostrarMensaje("Error", "El archivo de partida está corrupto.");
            return;
        }

        // 3. Restaura el estado de los jugadores
        restaurarJugador(j1, datos.j1);
        restaurarJugador(j2, datos.j2);

        // 4. Restaura el turno
        if (datos.nombreTurnoActual.equals(j1.getNombre())) {
            turnoActual = j1; rival = j2;
        } else {
            turnoActual = j2; rival = j1;
        }

        // 5. Reinicia flags del turno
        yaRoboEsteTurno      = false;
        yaJugoCartaEsteTurno = false;
        cartaSeleccionadaMano        = null;
        monstruoAtacanteSeleccionado = null;
        primerTurno = false;

        actualizarVista();
        vista.agregarLog("📂 Partida cargada. Turno de: " + turnoActual.getNombre());
        vista.mostrarMensaje("Partida Cargada", "La partida fue restaurada correctamente.");
    }

    /**
     * Restaura el estado de un Jugador a partir de los datos deserializados.
     */
    private void restaurarJugador(Jugador jugador, DatosJugador datos) {
        jugador.setLp(datos.lp);

        // Limpiar estado actual
        jugador.getCampo().limpiar();
        jugador.getMazoStack().clear();
        jugador.getManoLinked().clear();

        // Restaurar mano
        for (Carta c : datos.mano)  jugador.getManoLinked().add(c);

        // Restaurar mazo (push en orden inverso para mantener el orden original)
        for (int i = datos.mazo.size() - 1; i >= 0; i--) {
            jugador.getMazoStack().push(datos.mazo.get(i));
        }

        // Restaurar campo de monstruos
        for (Monstruo m : datos.campoMonstruos) {
            jugador.getCampo().invocarMonstruo(m);
        }

        // Restaurar zona de trampas
        for (CartaTrampa t : datos.campoTrampas) {
            jugador.getCampo().colocarTrampa(t);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  LÓGICA DE JUEGO
    // ════════════════════════════════════════════════════════════════════════

    public void robarCarta() {
        if (yaRoboEsteTurno) {
            vista.agregarLog(" Ya robaste una carta este turno.");
            return;
        }
        if (turnoActual.getMazoStack().isEmpty()) {
            finalizarJuego(rival, "¡Se quedó sin cartas en el mazo!");
            return;
        }
        turnoActual.robarCarta();
        yaRoboEsteTurno = true;
        vista.agregarLog(" " + turnoActual.getNombre() + " robó una carta. Mano: "
            + turnoActual.getMano().size());
        actualizarVista();
    }

    public void jugarCartaSeleccionada() {
        if (!yaRoboEsteTurno) {
            vista.agregarLog(" Debes robar una carta primero."); return;
        }
        if (yaJugoCartaEsteTurno) {
            vista.agregarLog(" Ya jugaste una carta este turno."); return;
        }
        if (cartaSeleccionadaMano == null) {
            vista.agregarLog(" Selecciona una carta de tu mano primero."); return;
        }
        if      (cartaSeleccionadaMano instanceof Monstruo)   invocarMonstruo((Monstruo)   cartaSeleccionadaMano);
        else if (cartaSeleccionadaMano instanceof CartaTrampa) colocarTrampa((CartaTrampa)  cartaSeleccionadaMano);
        else if (cartaSeleccionadaMano instanceof CartaMagica) activarMagica((CartaMagica)  cartaSeleccionadaMano);
    }

    public void invocarMonstruo(Monstruo m) {
        int modo = vista.mostrarSelector("Modo de Invocación",
            "¿Cómo deseas invocar a " + m.getNombre() + "?",
            new String[]{"Ataque", "Defensa"});
        m.setEnModoAtaque(modo == 0 || modo == -1);

        if (turnoActual.getCampo().invocarMonstruo(m)) {
            turnoActual.getManoLinked().remove(m);
            vista.agregarLog(turnoActual.getNombre() + " invocó a " + m.getNombre()
                + " (" + (m.isEnModoAtaque() ? "Ataque" : "Defensa") + ")");
            yaJugoCartaEsteTurno = true;
            cartaSeleccionadaMano = null;
            vista.limpiarSeleccion();
            actualizarVista();
        } else {
            vista.agregarLog(" Campo lleno. No puedes invocar más monstruos.");
        }
    }

    public void colocarTrampa(CartaTrampa t) {
        if (turnoActual.getCampo().colocarTrampa(t)) {
            turnoActual.getManoLinked().remove(t);
            vista.agregarLog(turnoActual.getNombre() + " colocó una trampa boca abajo.");
            yaJugoCartaEsteTurno = true;
            cartaSeleccionadaMano = null;
            vista.limpiarSeleccion();
            actualizarVista();
        } else {
            vista.agregarLog(" Zona de trampas llena.");
        }
    }

    public void activarMagica(CartaMagica m) {
        String resultado = juego.aplicarEfectoMagica(m, turnoActual, rival);
        turnoActual.getManoLinked().remove(m);
        vista.agregarLog(resultado);
        yaJugoCartaEsteTurno = true;
        cartaSeleccionadaMano = null;
        vista.limpiarSeleccion();
        actualizarVista();
    }

    public void atacarConMonstruo(Monstruo atacante) {
        if (!yaRoboEsteTurno) { vista.agregarLog(" Debes robar primero."); return; }
        if (primerTurno)      { vista.agregarLog(" No puedes atacar en el primer turno."); return; }
        if (atacante.isYaAtaco()) {
            vista.agregarLog(" " + atacante.getNombre() + " ya atacó este turno."); return;
        }
        if (!atacante.isEnModoAtaque()) {
            vista.agregarLog(" Solo los monstruos en modo Ataque pueden atacar."); return;
        }
        if (rival.getCampo().getZonaMonstruos().isEmpty()) {
            String result = juego.atacarDirecto(atacante, rival);
            vista.agregarLog(result);
            actualizarVista();
            verificarFinJuego();
        } else {
            monstruoAtacanteSeleccionado = atacante;
            vista.agregarLog(" " + atacante.getNombre() + " listo para atacar. Selecciona el objetivo.");
            vista.resaltarCampoRival(true);
        }
    }

    public void atacarDirecto() {
        if (!rival.getCampo().getZonaMonstruos().isEmpty()) {
            vista.agregarLog(" El rival tiene monstruos."); return;
        }
        if (turnoActual.getCampo().getZonaMonstruos().isEmpty()) {
            vista.agregarLog(" No tienes monstruos para atacar."); return;
        }
        for (Monstruo m : turnoActual.getCampo().getZonaMonstruos()) {
            if (!m.isYaAtaco() && m.isEnModoAtaque()) { atacarConMonstruo(m); return; }
        }
        vista.agregarLog(" Ningún monstruo puede atacar directamente.");
    }

    public void terminarTurno() {
        turnoActual.resetTurno();

        for (CartaTrampa t : turnoActual.getCampo().getZonaTrampas()) {
            if (t.getIdEfecto().equals("proteccion_waboku") && t.isActiva()) {
                turnoActual.setWabokuActivo(true);
                turnoActual.getCampo().getZonaTrampasLinked().remove(t);
                vista.agregarLog(" Waboku activado para " + turnoActual.getNombre());
                break;
            }
        }

        Jugador temp = turnoActual; turnoActual = rival; rival = temp;

        primerTurno          = false;
        yaJugoCartaEsteTurno = false;
        yaRoboEsteTurno      = false;
        cartaSeleccionadaMano        = null;
        monstruoAtacanteSeleccionado = null;
        vista.limpiarSeleccion();

        vista.agregarLog("---");
        vista.agregarLog(" Turno de " + turnoActual.getNombre() + ". Haz clic en 'Robar Carta'.");
        actualizarVista();
    }

    public void verificarFinJuego() {
        if (j1.getLp() <= 0) finalizarJuego(j2, "¡" + j1.getNombre() + " llegó a 0 LP!");
        else if (j2.getLp() <= 0) finalizarJuego(j1, "¡" + j2.getNombre() + " llegó a 0 LP!");
        else if (j1.getMazoStack().isEmpty() && j1.getMano().isEmpty())
            finalizarJuego(j2, "¡" + j1.getNombre() + " se quedó sin cartas!");
        else if (j2.getMazoStack().isEmpty() && j2.getMano().isEmpty())
            finalizarJuego(j1, "¡" + j2.getNombre() + " se quedó sin cartas!");
    }

    public void finalizarJuego(Jugador ganador, String razon) {
        actualizarVista();
        vista.mostrarMensaje("¡Duelo Finalizado!",
            "Ganador: " + ganador.getNombre() + "\n" + razon +
            "\n\"Confía en el corazón de las cartas\" — Yugi Muto");

        String fecha = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        GestorArchivos.getInstance().guardarResultado(
            String.format("[%s] Ganador: %s | LP Finales: %d",
                fecha, ganador.getNombre(), ganador.getLp()));

        boolean revancha = vista.mostrarConfirmacion("Revancha", "¿Deseas jugar de nuevo?");
        vista.dispose();
        if (revancha) {
            TableroJuego nuevoTablero = new TableroJuego(j1.getNombre(), j2.getNombre());
            ControladorJuego nuevo    = new ControladorJuego(j1.getNombre(), j2.getNombre(), nuevoTablero);
            nuevoTablero.setTableroListener(nuevo);
            nuevoTablero.setVisible(true);
        }
    }

    // ── Helper visual ─────────────────────────────────────────────────────────

    private void actualizarVista() {
        vista.actualizarHUD(j1.getLp(), j2.getLp(),
            j1.getMazoStack().size(), j2.getMazoStack().size(),
            turnoActual.getNombre());
        vista.actualizarMano(turnoActual.getMano());
        vista.actualizarCampo(
            turnoActual.getCampo().getZonaMonstruos(),
            turnoActual.getCampo().getZonaTrampas(),
            rival.getCampo().getZonaMonstruos(),
            rival.getCampo().getZonaTrampas(),
            rival.getMano().size());
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Jugador getJ1()                  { return j1; }
    public Jugador getJ2()                  { return j2; }
    public Jugador getTurnoActual()         { return turnoActual; }
    public Jugador getRival()               { return rival; }
    public Juego   getJuego()               { return juego; }
    public boolean isPrimerTurno()          { return primerTurno; }
    public boolean isYaJugoCartaEsteTurno() { return yaJugoCartaEsteTurno; }
    public boolean isYaRoboEsteTurno()      { return yaRoboEsteTurno; }
    public Monstruo getMonstruoAtacanteSeleccionado() { return monstruoAtacanteSeleccionado; }
    public void setMonstruoAtacanteSeleccionado(Monstruo m) { this.monstruoAtacanteSeleccionado = m; }
    public void setVista(TableroJuego v)    { this.vista = v; }

    // ── Construcción del mazo ─────────────────────────────────────────────────

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

    // CARGA DINÁMICA CON REFLECTION
    for (Carta c : CargadorCartas.cargarCartas("yugi_oh_JAVA/cartas/magicas.txt")) {
    mazo.add(c);
}

for (Carta c : CargadorCartas.cargarCartas("yugi_oh_JAVA/cartas/trampas.txt")) {
    mazo.add(c);
}

    for (Carta c : mazo) {
        juego.registrarCarta(c);
    }

    return mazo;
}
}
