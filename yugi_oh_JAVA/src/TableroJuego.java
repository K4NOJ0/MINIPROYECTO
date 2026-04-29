import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class TableroJuego extends JFrame {

    // Game state
    private Jugador j1, j2, turnoActual, rival;
    private Juego juego;
    private boolean primerTurno;
    private boolean yaJugoCartaEsteTurno;
    private boolean yaRoboEsteTurno;
    private CartaPanel cartaSeleccionadaMano;
    private Monstruo monstruoAtacanteSeleccionado;

    // UI Components
    private JPanel panelCampoRival, panelCampoJugador;
    private JPanel panelTrampasRival, panelTrampasJugador;
    private JPanel panelManoJugador, panelManoRival;
    private JLabel lblLpJ1, lblLpJ2, lblTurno, lblMazoJ1, lblMazoJ2;
    private JTextArea logArea;
    private JButton btnTerminarTurno, btnAtacarDirecto;
    private JPanel panelBotones;

    // Colors
    private static final Color BG_DARK    = new Color(8, 18, 8);
    private static final Color BG_CAMPO   = new Color(15, 45, 15);
    private static final Color BG_RIVAL   = new Color(40, 10, 10);
    private static final Color GOLD       = new Color(212, 175, 55);
    private static final Color GREEN_LINE = new Color(50, 180, 50);
    private static final Color RED_HP     = new Color(220, 60, 60);
    private static final Color BLUE_HP    = new Color(80, 160, 255);

    public TableroJuego(String nombre1, String nombre2) {
        juego = new Juego();
        j1 = new Jugador(nombre1);
        j2 = new Jugador(nombre2);

        // Build deck and deal
        ArrayList<Carta> mazo = construirMazo();
        juego.repartirCartas(j1, j2, mazo);

        // Random first turn
        turnoActual = new Random().nextBoolean() ? j1 : j2;
        rival = (turnoActual == j1) ? j2 : j1;
        primerTurno = true;
        yaJugoCartaEsteTurno = false;
        yaRoboEsteTurno = false;

        setTitle("Yu-Gi-Oh! — Duelo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(1000, 700));

        initUI();
        actualizarTablero();
        agregarLog("🎴 ¡El duelo comienza! Primer turno: " + turnoActual.getNombre());
        agregarLog("📜 Turno de " + turnoActual.getNombre() + ". Haz clic en 'Robar Carta' para comenzar.");
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(5, 5)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(BG_DARK);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        root.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // ---- TOP HUD ----
        JPanel hud = createHUD();
        root.add(hud, BorderLayout.NORTH);

        // ---- CENTER: CAMPO ----
        JPanel centro = new JPanel(new GridLayout(5, 1, 3, 3));
        centro.setOpaque(false);

        panelManoRival = createZonePanel("🂠 Mano del Rival", BG_RIVAL, false);
        panelTrampasRival = createZonePanel("⬇ Zona Trampa Rival", new Color(30, 10, 10), false);
        panelCampoRival = createZonePanel("⚔ Campo Rival", BG_CAMPO, false);
        panelCampoJugador = createZonePanel("🛡 Tu Campo", BG_CAMPO, true);
        panelTrampasJugador = createZonePanel("⬇ Tu Zona Trampa", new Color(10, 30, 10), true);

        // Divider line
        JSeparator sep = new JSeparator();
        sep.setForeground(GREEN_LINE);
        sep.setBackground(GREEN_LINE);

        centro.add(panelManoRival);
        centro.add(panelTrampasRival);
        centro.add(panelCampoRival);
        centro.add(panelCampoJugador);
        centro.add(panelTrampasJugador);

        root.add(centro, BorderLayout.CENTER);

        // ---- BOTTOM: MANO JUGADOR + LOG + BOTONES ----
        JPanel bottom = new JPanel(new BorderLayout(5, 0));
        bottom.setOpaque(false);
        bottom.setPreferredSize(new Dimension(0, 200));

        panelManoJugador = createZonePanel("🃏 Tu Mano", new Color(10, 30, 10), true);
        panelManoJugador.setPreferredSize(new Dimension(600, 180));
        bottom.add(panelManoJugador, BorderLayout.CENTER);

        // Log + buttons
        JPanel rightPanel = new JPanel(new BorderLayout(0, 5));
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(350, 200));

        logArea = new JTextArea(8, 30);
        logArea.setEditable(false);
        logArea.setBackground(new Color(5, 10, 5));
        logArea.setForeground(new Color(180, 255, 180));
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 120, 50)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setPreferredSize(new Dimension(340, 130));
        scroll.setBorder(null);
        rightPanel.add(scroll, BorderLayout.CENTER);

        panelBotones = createBotonPanel();
        rightPanel.add(panelBotones, BorderLayout.SOUTH);

        bottom.add(rightPanel, BorderLayout.EAST);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel createHUD() {
        JPanel hud = new JPanel(new GridLayout(1, 5, 10, 0));
        hud.setOpaque(false);
        hud.setPreferredSize(new Dimension(0, 60));
        hud.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, GOLD));

        lblLpJ1 = createHUDLabel("❤ " + j1.getNombre() + ": 8000 LP", RED_HP);
        lblMazoJ1 = createHUDLabel("🂠 Mazo: 20", Color.LIGHT_GRAY);
        lblTurno = createHUDLabel("TURNO", GOLD);
        lblMazoJ2 = createHUDLabel("🂠 Mazo: 20", Color.LIGHT_GRAY);
        lblLpJ2 = createHUDLabel("❤ " + j2.getNombre() + ": 8000 LP", BLUE_HP);

        hud.add(lblLpJ1);
        hud.add(lblMazoJ1);
        hud.add(lblTurno);
        hud.add(lblMazoJ2);
        hud.add(lblLpJ2);

        return hud;
    }

    private JLabel createHUDLabel(String text, Color color) {
        JLabel lbl = new JLabel(text, JLabel.CENTER);
        lbl.setFont(new Font("Serif", Font.BOLD, 14));
        lbl.setForeground(color);
        return lbl;
    }

    private JPanel createZonePanel(String title, Color bg, boolean esJugador) {
        JPanel wrapper = new JPanel(new BorderLayout(2, 2)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(bg);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            }
        };
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 120, 50, 80), 1),
            BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Serif", Font.BOLD, 11));
        lbl.setForeground(new Color(150, 200, 150));
        wrapper.add(lbl, BorderLayout.NORTH);

        JPanel cartas = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        cartas.setOpaque(false);
        cartas.setName("cartas");
        wrapper.add(cartas, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createBotonPanel() {
        JPanel p = new JPanel(new GridLayout(2, 2, 5, 5));
        p.setOpaque(false);

        JButton btnRobar = createBtn("🃏 Robar Carta", new Color(50, 100, 200));
        btnRobar.addActionListener(e -> robarCarta());

        JButton btnJugar = createBtn("▶ Jugar Carta", new Color(60, 150, 60));
        btnJugar.addActionListener(e -> jugarCartaSeleccionada());

        btnAtacarDirecto = createBtn("⚡ Ataque Directo", new Color(180, 80, 0));
        btnAtacarDirecto.addActionListener(e -> atacarDirecto());

        btnTerminarTurno = createBtn("⏭ Terminar Turno", new Color(120, 50, 120));
        btnTerminarTurno.addActionListener(e -> terminarTurno());

        p.add(btnRobar);
        p.add(btnJugar);
        p.add(btnAtacarDirecto);
        p.add(btnTerminarTurno);

        return p;
    }

    private JButton createBtn(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed() ? color.darker() :
                          getModel().isRollover() ? color.brighter() : color;
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        btn.setFont(new Font("Serif", Font.BOLD, 12));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 38));
        return btn;
    }

    // ===================== ACCIONES DE LA TERMINAL =====================

    private void robarCarta() {
        if (!esMiTurno()) return;
        if (yaRoboEsteTurno) { agregarLog("⚠ Ya robaste una carta este turno."); return; }

        if (turnoActual.getMazo().isEmpty()) {
            finalizarJuego(rival, "¡Se quedó sin cartas en el mazo!");
            return;
        }
        turnoActual.robarCarta();
        yaRoboEsteTurno = true;
        agregarLog(" " + turnoActual.getNombre() + " robó una carta. Mano: " + turnoActual.getMano().size());
        actualizarTablero();
    }

    private void jugarCartaSeleccionada() {
        if (!esMiTurno()) return;
        if (!yaRoboEsteTurno) { agregarLog(" Debes robar una carta primero."); return; }
        if (yaJugoCartaEsteTurno) { agregarLog(" Ya jugaste una carta este turno."); return; }
        if (cartaSeleccionadaMano == null) { agregarLog(" Selecciona una carta de tu mano primero."); return; }

        Carta carta = cartaSeleccionadaMano.getCarta();

        if (carta instanceof Monstruo) {
            Monstruo m = (Monstruo) carta;
            if (m.getNivel() > 4) {
                if (turnoActual.getCampo().getZonaMonstruos().isEmpty()) {
                    agregarLog(" Necesitas sacrificar un monstruo para invocar a " + m.getNombre() + " (nivel " + m.getNivel() + ").");
                    mostrarDialogoSacrificio(m);
                    return;
                }
            }
            invocarMonstruo(m);
        } else if (carta instanceof CartaMagica) {
            CartaMagica cm = (CartaMagica) carta;
            turnoActual.getMano().remove(carta);
            cm.activarEfecto(turnoActual, rival);
            agregarLog(" " + turnoActual.getNombre() + " activó " + cm.getNombre() + ": " + cm.getEfectoDescripcion());
            yaJugoCartaEsteTurno = true;
            cartaSeleccionadaMano = null;
        } else if (carta instanceof CartaTrampa) {
            CartaTrampa ct = (CartaTrampa) carta;
            if (turnoActual.getCampo().colocarTrampa(ct)) {
                turnoActual.getMano().remove(carta);
                ct.setActiva(true);
                agregarLog(" " + turnoActual.getNombre() + " colocó la trampa " + ct.getNombre() + " boca abajo.");
                yaJugoCartaEsteTurno = true;
                cartaSeleccionadaMano = null;
            } else {
                agregarLog(" Zona de trampas llena.");
            }
        }

        actualizarTablero();
        verificarFinJuego();
    }

    private void mostrarDialogoSacrificio(Monstruo objetivo) {
        ArrayList<Monstruo> enCampo = turnoActual.getCampo().getZonaMonstruos();
        if (enCampo.isEmpty()) {
            agregarLog(" No tienes monstruos para sacrificar.");
            return;
        }

        String[] opciones = new String[enCampo.size()];
        for (int i = 0; i < enCampo.size(); i++) {
            Monstruo m = enCampo.get(i);
            opciones[i] = m.getNombre() + " (ATK:" + m.getAtk() + ")";
        }

        int sel = JOptionPane.showOptionDialog(this,
            "Elige un monstruo para sacrificar e invocar a " + objetivo.getNombre() + " (Nivel " + objetivo.getNivel() + ")",
            "Sacrificio Requerido", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, opciones, opciones[0]);

        if (sel >= 0) {
            Monstruo sacrificado = enCampo.remove(sel);
            agregarLog(" " + turnoActual.getNombre() + " sacrificó a " + sacrificado.getNombre() + ".");
            invocarMonstruo(objetivo);
        }
    }

    private void invocarMonstruo(Monstruo m) {
        // Ask for mode
        int modo = JOptionPane.showOptionDialog(this,
            "¿En qué posición invocar a " + m.getNombre() + "?",
            "Modo de Invocación", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, new String[]{"Modo Ataque", "Modo Defensa"}, "Modo Ataque");

        m.setEnModoAtaque(modo == 0 || modo == JOptionPane.CLOSED_OPTION);

        if (turnoActual.getCampo().invocarMonstruo(m)) {
            turnoActual.getMano().remove(m);
            agregarLog("⚔ " + turnoActual.getNombre() + " invocó a " + m.getNombre() +
                " (" + (m.isEnModoAtaque() ? "Ataque" : "Defensa") + ")");
            yaJugoCartaEsteTurno = true;
            cartaSeleccionadaMano = null;
        } else {
            agregarLog(" Campo lleno. No puedes invocar más monstruos.");
        }
    }

    private void atacarConMonstruo(Monstruo atacante) {
        if (!esMiTurno()) return;
        if (!yaRoboEsteTurno) { agregarLog(" Debes robar primero."); return; }
        if (primerTurno && turnoActual == (rival == j2 ? j1 : j2)) {
            agregarLog(" No puedes atacar en el primer turno.");
            return;
        }
        if (atacante.isYaAtaco()) { agregarLog(" " + atacante.getNombre() + " ya atacó este turno."); return; }
        if (!atacante.isEnModoAtaque()) { agregarLog(" Solo los monstruos en modo Ataque pueden atacar."); return; }

        if (rival.getCampo().getZonaMonstruos().isEmpty()) {
            String result = juego.atacarDirecto(atacante, rival);
            agregarLog(result);
        } else {

            monstruoAtacanteSeleccionado = atacante;
            agregarLog(" " + atacante.getNombre() + " listo para atacar. Selecciona el monstruo objetivo en el campo rival.");
            resaltarCampoRival(true);
            return;
        }

        actualizarTablero();
        verificarFinJuego();
    }

    private void atacarDirecto() {
        if (!esMiTurno()) return;
        if (!rival.getCampo().getZonaMonstruos().isEmpty()) {
            agregarLog("⚠ El rival tiene monstruos. No puedes atacar directamente.");
            return;
        }
        if (turnoActual.getCampo().getZonaMonstruos().isEmpty()) {
            agregarLog("⚠ No tienes monstruos para atacar.");
            return;
        }

        for (Monstruo m : turnoActual.getCampo().getZonaMonstruos()) {
            if (!m.isYaAtaco() && m.isEnModoAtaque()) {
                atacarConMonstruo(m);
                return;
            }
        }
        agregarLog(" Ningún monstruo puede atacar directamente.");
    }

    private void terminarTurno() {
        if (!esMiTurno()) return;


        turnoActual.resetTurno();


        for (CartaTrampa t : turnoActual.getCampo().getZonaTrampas()) {
            if (t.getIdEfecto().equals("proteccion_waboku") && t.isActiva()) {
                turnoActual.setWabokuActivo(true);
                turnoActual.getCampo().getZonaTrampas().remove(t);
                agregarLog(" Waboku activado para proteger a " + turnoActual.getNombre() + " este turno.");
                break;
            }
        }

        // Swap turns
        Jugador temp = turnoActual;
        turnoActual = rival;
        rival = temp;

        primerTurno = false;
        yaJugoCartaEsteTurno = false;
        yaRoboEsteTurno = false;
        cartaSeleccionadaMano = null;
        monstruoAtacanteSeleccionado = null;

        agregarLog("---");
        agregarLog(" Turno de " + turnoActual.getNombre() + ". Haz clic en 'Robar Carta' para comenzar.");
        actualizarTablero();
    }



    private void actualizarTablero() {

        lblLpJ1.setText(" " + j1.getNombre() + ": " + j1.getLp() + " LP");
        lblLpJ2.setText(" " + j2.getNombre() + ": " + j2.getLp() + " LP");
        lblMazoJ1.setText(" Mazo " + j1.getNombre() + ": " + j1.getMazo().size());
        lblMazoJ2.setText(" Mazo " + j2.getNombre() + ": " + j2.getMazo().size());
        lblTurno.setText("TURNO: " + turnoActual.getNombre().toUpperCase());

  
        Jugador jugador = turnoActual;
        Jugador rivalUI = rival;

        // Campo jugador
        actualizarZonaMonstruos(panelCampoJugador, jugador.getCampo().getZonaMonstruos(), true);
        actualizarZonaTrampas(panelTrampasJugador, jugador.getCampo().getZonaTrampas(), true);
        actualizarMano(panelManoJugador, jugador.getMano(), true);

        // Campo rival
        actualizarZonaMonstruos(panelCampoRival, rivalUI.getCampo().getZonaMonstruos(), false);
        actualizarZonaTrampas(panelTrampasRival, rivalUI.getCampo().getZonaTrampas(), false);
        actualizarManoRival(panelManoRival, rivalUI.getMano().size());

        revalidate();
        repaint();
    }

    private JPanel getCartasPanel(JPanel zone) {
        for (Component c : zone.getComponents()) {
            if (c instanceof JPanel && "cartas".equals(((JPanel)c).getName())) return (JPanel) c;
        }
        return zone;
    }

    private void actualizarZonaMonstruos(JPanel zone, ArrayList<Monstruo> monstruos, boolean esJugador) {
        JPanel cartas = getCartasPanel(zone);
        cartas.removeAll();
        for (Monstruo m : monstruos) {
            CartaPanel cp = new CartaPanel(m, true);
            if (esJugador) {
                cp.addMouseListener(new MouseAdapter() {
                    @Override public void mouseClicked(MouseEvent e) {
                        if (monstruoAtacanteSeleccionado == null) {
                            atacarConMonstruo(m);
                        }
                    }
                });
            } else {

                cp.addMouseListener(new MouseAdapter() {
                    @Override public void mouseClicked(MouseEvent e) {
                        if (monstruoAtacanteSeleccionado != null) {
                            String result = juego.atacarMonstruo(monstruoAtacanteSeleccionado, m,
                                turnoActual, rival);
                            agregarLog(result);
                            monstruoAtacanteSeleccionado = null;
                            resaltarCampoRival(false);
                            actualizarTablero();
                            verificarFinJuego();
                        }
                    }
                });
            }
            cartas.add(cp);
        }
        cartas.revalidate();
        cartas.repaint();
    }

    private void actualizarZonaTrampas(JPanel zone, ArrayList<CartaTrampa> trampas, boolean esJugador) {
        JPanel cartas = getCartasPanel(zone);
        cartas.removeAll();
        for (CartaTrampa ct : trampas) {
            CartaPanel cp = new CartaPanel(ct, true);
            if (esJugador) {
                cp.addMouseListener(new MouseAdapter() {
                    @Override public void mouseClicked(MouseEvent e) {
                        int r = JOptionPane.showConfirmDialog(TableroJuego.this,
                            "¿Activar " + ct.getNombre() + "?\n" + ct.getEfectoDescripcion(),
                            "Activar Trampa", JOptionPane.YES_NO_OPTION);
                        if (r == JOptionPane.YES_OPTION) {
                            ct.activarEfecto(turnoActual, rival);
                            turnoActual.getCampo().getZonaTrampas().remove(ct);
                            agregarLog(" ¡" + turnoActual.getNombre() + " activó la trampa " + ct.getNombre() + "!");
                            actualizarTablero();
                            verificarFinJuego();
                        }
                    }
                });
            }
            cartas.add(cp);
        }
        cartas.revalidate();
        cartas.repaint();
    }

    private void actualizarMano(JPanel zone, ArrayList<Carta> mano, boolean esJugador) {
        JPanel cartas = getCartasPanel(zone);
        cartas.removeAll();
        for (Carta carta : mano) {
            CartaPanel cp = new CartaPanel(carta);
            if (esJugador) {
                cp.addMouseListener(new MouseAdapter() {
                    @Override public void mouseClicked(MouseEvent e) {
                        if (cartaSeleccionadaMano != null) cartaSeleccionadaMano.setSeleccionada(false);
                        if (cartaSeleccionadaMano == cp) {
                            cartaSeleccionadaMano = null;
                        } else {
                            cartaSeleccionadaMano = cp;
                            cp.setSeleccionada(true);
                            agregarLog("🃏 Seleccionada: " + carta.getNombre() + " — " + carta.getDescripcion());
                        }
                    }
                });
            }
            cartas.add(cp);
        }
        cartas.revalidate();
        cartas.repaint();
    }

    private void actualizarManoRival(JPanel zone, int cantidad) {
        JPanel cartas = getCartasPanel(zone);
        cartas.removeAll();
        for (int i = 0; i < cantidad; i++) {
            JPanel dorso = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(30, 30, 90));
                    g2.fillRoundRect(0, 0, getWidth()-2, getHeight()-2, 10, 10);
                    g2.setColor(new Color(212, 175, 55));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(0, 0, getWidth()-3, getHeight()-3, 10, 10);
                    g2.setFont(new Font("Serif", Font.BOLD, 16));
                    g2.setColor(new Color(212, 175, 55, 150));
                    g2.drawString("?", getWidth()/2 - 5, getHeight()/2 + 6);
                }
            };
            dorso.setPreferredSize(new Dimension(80, 110));
            dorso.setOpaque(false);
            cartas.add(dorso);
        }
        cartas.revalidate();
        cartas.repaint();
    }

    private void resaltarCampoRival(boolean activar) {
        JPanel cartas = getCartasPanel(panelCampoRival);
        Color border = activar ? new Color(255, 100, 50, 200) : new Color(50, 120, 50, 80);
        panelCampoRival.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(border, activar ? 3 : 1),
            BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        panelCampoRival.repaint();
    }


    private boolean esMiTurno() {
        return true; 
    }

    private void verificarFinJuego() {
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

    private void finalizarJuego(Jugador ganador, String razon) {
        actualizarTablero();
        String msg = "<html><center>" +
            "<h1>🏆 ¡DUELO FINALIZADO! 🏆</h1>" +
            "<h2>Ganador: " + ganador.getNombre() + "</h2>" +
            "<p>" + razon + "</p>" +
            "<p><i>\"Confía en el corazón de las cartas\" — Yugi Muto</i></p>" +
            "</center></html>";

        JOptionPane.showMessageDialog(this, new JLabel(msg), "¡DUELO TERMINADO!",
            JOptionPane.INFORMATION_MESSAGE);

        int r = JOptionPane.showConfirmDialog(this, "¿Jugar otra partida?", "Revancha",
            JOptionPane.YES_NO_OPTION);
        dispose();
        if (r == JOptionPane.YES_OPTION) {
            new MenuInicial().setVisible(true);
        }
    }

    private void agregarLog(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    // ===================== CREADOR DE MAZO =====================

    private ArrayList<Carta> construirMazo() {
        ArrayList<Carta> mazo = new ArrayList<>();
        // 30 monstruos
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
        // 10 magias
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
        // 10 trampas
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
