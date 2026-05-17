import javax.swing.*;
import javax.swing.border.*;
import modelo.Carta;
import modelo.CartaMagica;
import modelo.CartaTrampa;
import modelo.Monstruo;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * PARTE 2 — VISTA PURA
 *
 * TableroJuego es responsable exclusivamente de:
 *   - Construir y mostrar todos los componentes Swing.
 *   - Exponer métodos de actualización visual: actualizarHUD(), actualizarMano(),
 *     actualizarCampo(), mostrarMensaje(), agregarLog().
 *   - Capturar eventos del usuario (clics, botones) y delegarlos al Controlador
 *     a través de la interfaz TableroListener.
 *
 * NO contiene ninguna regla de juego, validaciones de turno ni lógica de estado.
 */
public class TableroJuego extends JFrame {

    // ════════════════════════════════════════════════════════════════════════
    //  INTERFAZ PARA EL CONTROLADOR
    // ════════════════════════════════════════════════════════════════════════

    /**
     * El Controlador (Persona C) implementa esta interfaz para recibir
     * cada acción del usuario desde la vista.
     */
    public interface TableroListener {
        void onRobarCarta();
        void onJugarCarta(Carta carta);
        void onAtacarConMonstruo(Monstruo atacante);
        void onSeleccionarObjetivo(Monstruo objetivo);
        void onAtacarDirecto();
        void onTerminarTurno();
        void onActivarTrampa(CartaTrampa trampa);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  ESTADO SOLO DE PRESENTACIÓN
    // ════════════════════════════════════════════════════════════════════════

    private TableroListener listener;

    /** Carta de la mano actualmente seleccionada (solo UI). */
    private CartaPanel cartaSeleccionadaMano;

    /** Si está esperando que el jugador elija un objetivo de ataque. */
    private boolean esperandoObjetivo = false;

    // ── Componentes ─────────────────────────────────────────────────────────
    private JPanel panelCampoRival, panelCampoJugador;
    private JPanel panelTrampasRival, panelTrampasJugador;
    private JPanel panelManoJugador, panelManoRival;
    private JLabel lblLpJ1, lblLpJ2, lblTurno, lblMazoJ1, lblMazoJ2;
    private JTextArea logArea;
    private JButton btnTerminarTurno, btnAtacarDirecto;

    // ── Paleta ───────────────────────────────────────────────────────────────
    private static final Color BG_DARK    = new Color(8, 18, 8);
    private static final Color BG_CAMPO   = new Color(15, 45, 15);
    private static final Color BG_RIVAL   = new Color(40, 10, 10);
    private static final Color GOLD       = new Color(212, 175, 55);
    private static final Color GREEN_LINE = new Color(50, 180, 50);
    private static final Color RED_HP     = new Color(220, 60, 60);
    private static final Color BLUE_HP    = new Color(80, 160, 255);

    // ── Nombres guardados para etiquetas (no hay referencia a Jugador) ───────
    private final String nombreJ1;
    private final String nombreJ2;

    // ════════════════════════════════════════════════════════════════════════
    //  CONSTRUCTORES
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Constructor MVC: recibe los nombres y el listener del controlador.
     * No crea ni toca objetos de modelo.
     */
    public TableroJuego(String nombre1, String nombre2, TableroListener listener) {
        this.nombreJ1 = nombre1;
        this.nombreJ2 = nombre2;
        this.listener = listener;

        setTitle("Yu-Gi-Oh! — Duelo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(1000, 700));

        initUI();
    }

    /**
     * Constructor de compatibilidad (sin controlador).
     * Permite que el código anterior siga compilando mientras Persona C
     * implementa el controlador.
     */
    public TableroJuego(String nombre1, String nombre2) {
        this(nombre1, nombre2, null);
    }

    // ── Permite inyectar/cambiar el listener en cualquier momento ────────────
    public void setTableroListener(TableroListener listener) {
        this.listener = listener;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CONSTRUCCIÓN DE LA UI  (solo layout y componentes)
    // ════════════════════════════════════════════════════════════════════════

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(5, 5)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(BG_DARK);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        root.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        root.add(createHUD(),    BorderLayout.NORTH);
        root.add(createCampo(),  BorderLayout.CENTER);
        root.add(createBottom(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    // ── HUD ─────────────────────────────────────────────────────────────────
    private JPanel createHUD() {
        JPanel hud = new JPanel(new GridLayout(1, 5, 10, 0));
        hud.setOpaque(false);
        hud.setPreferredSize(new Dimension(0, 60));
        hud.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, GOLD));

        lblLpJ1   = createHUDLabel("❤ " + nombreJ1 + ": 8000 LP", RED_HP);
        lblMazoJ1 = createHUDLabel("🂠 Mazo: 20", Color.LIGHT_GRAY);
        lblTurno  = createHUDLabel("TURNO", GOLD);
        lblMazoJ2 = createHUDLabel("🂠 Mazo: 20", Color.LIGHT_GRAY);
        lblLpJ2   = createHUDLabel("❤ " + nombreJ2 + ": 8000 LP", BLUE_HP);

        hud.add(lblLpJ1); hud.add(lblMazoJ1); hud.add(lblTurno);
        hud.add(lblMazoJ2); hud.add(lblLpJ2);
        return hud;
    }

    private JLabel createHUDLabel(String text, Color color) {
        JLabel lbl = new JLabel(text, JLabel.CENTER);
        lbl.setFont(new Font("Serif", Font.BOLD, 14));
        lbl.setForeground(color);
        return lbl;
    }

    // ── Zonas de campo ───────────────────────────────────────────────────────
    private JPanel createCampo() {
        JPanel centro = new JPanel(new GridLayout(5, 1, 3, 3));
        centro.setOpaque(false);

        panelManoRival     = createZonePanel("🂠 Mano del Rival",       BG_RIVAL);
        panelTrampasRival  = createZonePanel("⬇ Zona Trampa Rival",    new Color(30, 10, 10));
        panelCampoRival    = createZonePanel("⚔ Campo Rival",           BG_CAMPO);
        panelCampoJugador  = createZonePanel("🛡 Tu Campo",              BG_CAMPO);
        panelTrampasJugador= createZonePanel("⬇ Tu Zona Trampa",        new Color(10, 30, 10));

        centro.add(panelManoRival);
        centro.add(panelTrampasRival);
        centro.add(panelCampoRival);
        centro.add(panelCampoJugador);
        centro.add(panelTrampasJugador);
        return centro;
    }

    private JPanel createZonePanel(String title, Color bg) {
        JPanel wrapper = new JPanel(new BorderLayout(2, 2)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(bg);
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
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

    // ── Panel inferior: mano + log + botones ─────────────────────────────────
    private JPanel createBottom() {
        JPanel bottom = new JPanel(new BorderLayout(5, 0));
        bottom.setOpaque(false);
        bottom.setPreferredSize(new Dimension(0, 200));

        panelManoJugador = createZonePanel("🃏 Tu Mano", new Color(10, 30, 10));
        panelManoJugador.setPreferredSize(new Dimension(600, 180));
        bottom.add(panelManoJugador, BorderLayout.CENTER);

        // Panel derecho: log + botones
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
        rightPanel.add(createBotones(), BorderLayout.SOUTH);

        bottom.add(rightPanel, BorderLayout.EAST);
        return bottom;
    }

    private JPanel createBotones() {
        JPanel p = new JPanel(new GridLayout(2, 2, 5, 5));
        p.setOpaque(false);

        JButton btnRobar = createBtn("🃏 Robar Carta",       new Color(50, 100, 200));
        JButton btnJugar = createBtn("▶ Jugar Carta",        new Color(60, 150, 60));
        btnAtacarDirecto = createBtn("⚡ Ataque Directo",    new Color(180, 80, 0));
        btnTerminarTurno = createBtn("⏭ Terminar Turno",    new Color(120, 50, 120));

        // ── Los botones solo notifican al listener ──────────────────────────
        btnRobar.addActionListener(e -> { if (listener != null) listener.onRobarCarta(); });

        btnJugar.addActionListener(e -> {
            if (listener == null) return;
            if (cartaSeleccionadaMano == null) {
                agregarLog("⚠ Selecciona una carta de tu mano primero.");
                return;
            }
            listener.onJugarCarta(cartaSeleccionadaMano.getCarta());
        });

        btnAtacarDirecto.addActionListener(e -> { if (listener != null) listener.onAtacarDirecto(); });
        btnTerminarTurno.addActionListener(e -> { if (listener != null) listener.onTerminarTurno(); });

        p.add(btnRobar); p.add(btnJugar);
        p.add(btnAtacarDirecto); p.add(btnTerminarTurno);
        return p;
    }

    private JButton createBtn(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed() ? color.darker()
                        : getModel().isRollover() ? color.brighter() : color;
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth() - fm.stringWidth(getText())) / 2,
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

    // ════════════════════════════════════════════════════════════════════════
    //  MÉTODOS PÚBLICOS DE ACTUALIZACIÓN VISUAL
    //  (el Controlador los llama; la vista solo refresca la pantalla)
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Actualiza el HUD (LP, tamaño de mazos, nombre del turno).
     *
     * @param lpJ1       Puntos de vida del jugador 1
     * @param lpJ2       Puntos de vida del jugador 2
     * @param mazoJ1     Cartas restantes en el mazo J1
     * @param mazoJ2     Cartas restantes en el mazo J2
     * @param turnoNombre Nombre del jugador cuyo turno es
     */
    public void actualizarHUD(int lpJ1, int lpJ2, int mazoJ1, int mazoJ2, String turnoNombre) {
        lblLpJ1.setText("❤ " + nombreJ1 + ": " + lpJ1 + " LP");
        lblLpJ2.setText("❤ " + nombreJ2 + ": " + lpJ2 + " LP");
        lblMazoJ1.setText("🂠 Mazo " + nombreJ1 + ": " + mazoJ1);
        lblMazoJ2.setText("🂠 Mazo " + nombreJ2 + ": " + mazoJ2);
        lblTurno.setText("TURNO: " + turnoNombre.toUpperCase());
    }

    /**
     * Actualiza la mano del jugador activo.
     * Cada carta genera un CartaPanel clicable que notifica al listener.
     *
     * @param mano Lista de cartas en la mano del jugador activo
     */
    public void actualizarMano(ArrayList<Carta> mano) {
        JPanel cartas = getCartasPanel(panelManoJugador);
        cartas.removeAll();
        for (Carta carta : mano) {
            CartaPanel cp = new CartaPanel(carta);
            cp.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    // Deseleccionar anterior
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
            cartas.add(cp);
        }
        cartas.revalidate();
        cartas.repaint();
    }

    /**
     * Actualiza el campo completo: monstruos, trampas de ambos jugadores
     * y la mano oculta del rival.
     *
     * @param monstruosJugador  Monstruos del jugador activo
     * @param trampasJugador    Trampas del jugador activo
     * @param monstruosRival    Monstruos del rival
     * @param trampasRival      Trampas del rival
     * @param cartasEnManoRival Número de cartas en la mano del rival (se muestran boca abajo)
     */
    public void actualizarCampo(
            ArrayList<Monstruo>    monstruosJugador,
            ArrayList<CartaTrampa> trampasJugador,
            ArrayList<Monstruo>    monstruosRival,
            ArrayList<CartaTrampa> trampasRival,
            int                    cartasEnManoRival) {

        poblarMonstruos(panelCampoJugador, monstruosJugador, true);
        poblarTrampas(panelTrampasJugador,  trampasJugador,   true);
        poblarMonstruos(panelCampoRival,    monstruosRival,   false);
        poblarTrampas(panelTrampasRival,    trampasRival,     false);
        poblarManoRival(cartasEnManoRival);

        revalidate();
        repaint();
    }

    /**
     * Muestra un diálogo de mensaje informativo (fin de juego, etc.).
     *
     * @param titulo  Título del diálogo
     * @param mensaje Cuerpo HTML del mensaje
     */
    public void mostrarMensaje(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this,
            new JLabel("<html><center>" + mensaje + "</center></html>"),
            titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra un diálogo de confirmación y retorna la respuesta.
     *
     * @return true si el usuario eligió SÍ
     */
    public boolean mostrarConfirmacion(String titulo, String mensaje) {
        return JOptionPane.showConfirmDialog(this, mensaje, titulo,
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    /**
     * Muestra un selector de opciones y retorna el índice elegido, o -1 si
     * el usuario cerró el diálogo.
     */
    public int mostrarSelector(String titulo, String mensaje, String[] opciones) {
        return JOptionPane.showOptionDialog(this, mensaje, titulo,
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, opciones, opciones[0]);
    }

    /**
     * Agrega una línea al área de log y hace scroll al final.
     */
    public void agregarLog(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    /**
     * Limpia la selección de carta en mano (llamar tras jugar una carta).
     */
    public void limpiarSeleccion() {
        if (cartaSeleccionadaMano != null) {
            cartaSeleccionadaMano.setSeleccionada(false);
            cartaSeleccionadaMano = null;
        }
    }

    /**
     * Activa o desactiva el resaltado visual del campo rival
     * (mientras se espera selección de objetivo).
     */
    public void resaltarCampoRival(boolean activar) {
        esperandoObjetivo = activar;
        Color border = activar ? new Color(255, 100, 50, 200) : new Color(50, 120, 50, 80);
        panelCampoRival.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(border, activar ? 3 : 1),
            BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        panelCampoRival.repaint();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  HELPERS PRIVADOS DE POBLACIÓN DE ZONAS
    // ════════════════════════════════════════════════════════════════════════

    private void poblarMonstruos(JPanel zone, ArrayList<Monstruo> monstruos, boolean esJugador) {
        JPanel cartas = getCartasPanel(zone);
        cartas.removeAll();
        for (Monstruo m : monstruos) {
            CartaPanel cp = new CartaPanel(m, true);
            cp.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    if (listener == null) return;
                    if (esJugador) {
                        // El jugador clickea su propio monstruo → quiere atacar con él
                        listener.onAtacarConMonstruo(m);
                    } else if (esperandoObjetivo) {
                        // El jugador clickea un monstruo rival → selecciona objetivo
                        listener.onSeleccionarObjetivo(m);
                        resaltarCampoRival(false);
                    }
                }
            });
            cartas.add(cp);
        }
        cartas.revalidate();
        cartas.repaint();
    }

    private void poblarTrampas(JPanel zone, ArrayList<CartaTrampa> trampas, boolean esJugador) {
        JPanel cartas = getCartasPanel(zone);
        cartas.removeAll();
        for (CartaTrampa ct : trampas) {
            CartaPanel cp = new CartaPanel(ct, true);
            if (esJugador) {
                cp.addMouseListener(new MouseAdapter() {
                    @Override public void mouseClicked(MouseEvent e) {
                        if (listener == null) return;
                        // La vista pregunta confirmación y delega al listener
                        int r = JOptionPane.showConfirmDialog(TableroJuego.this,
                            "¿Activar " + ct.getNombre() + "?\n" + ct.getEfectoDescripcion(),
                            "Activar Trampa", JOptionPane.YES_NO_OPTION);
                        if (r == JOptionPane.YES_OPTION) {
                            listener.onActivarTrampa(ct);
                        }
                    }
                });
            }
            cartas.add(cp);
        }
        cartas.revalidate();
        cartas.repaint();
    }

    private void poblarManoRival(int cantidad) {
        JPanel cartas = getCartasPanel(panelManoRival);
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
            dorso.setPreferredSize(new Dimension(CartaPanel.W, CartaPanel.H));
            dorso.setOpaque(false);
            cartas.add(dorso);
        }
        cartas.revalidate();
        cartas.repaint();
    }

    // ── Obtiene el sub-panel de cartas de una zona ───────────────────────────
    private JPanel getCartasPanel(JPanel zone) {
        for (Component c : zone.getComponents()) {
            if (c instanceof JPanel && "cartas".equals(((JPanel) c).getName())) {
                return (JPanel) c;
            }
        }
        return zone;
    }
}