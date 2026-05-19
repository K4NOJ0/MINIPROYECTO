package vista;

import javax.swing.*;
import javax.swing.border.*;

import modelo.Carta;
import modelo.CartaMagica;
import modelo.CartaTrampa;
import modelo.Monstruo;
import controlador.ControladorJuego;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class TableroJuego extends JFrame {
    private ControladorJuego controlador;
    private JPanel panelCampoRival, panelCampoJugador;
    private JPanel panelTrampasRival, panelTrampasJugador;
    private JPanel panelManoJugador, panelManoRival;
    private JLabel lblLpJ1, lblLpJ2, lblTurno, lblMazoJ1, lblMazoJ2;
    private JTextArea logArea;
    private CartaPanel cartaSeleccionadaMano;

    private static final Color BG_DARK    = new Color(8, 18, 8);
    private static final Color BG_CAMPO   = new Color(15, 45, 15);
    private static final Color BG_RIVAL   = new Color(40, 10, 10);
    private static final Color GOLD       = new Color(212, 175, 55);
    private static final Color GREEN_LINE = new Color(50, 180, 50);
    private static final Color RED_HP     = new Color(220, 60, 60);
    private static final Color BLUE_HP    = new Color(80, 160, 255);

    public TableroJuego(ControladorJuego controlador) {
        this.controlador = controlador;
        setTitle("Yu-Gi-Oh! — Duelo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(1000, 700));

        initUI();
        actualizarTablero();
        agregarLog("🎴 ¡El duelo comienza! Primer turno: " + controlador.getTurnoActual().getNombre());
        agregarLog("📜 Turno de " + controlador.getTurnoActual().getNombre() + ". Haz clic en 'Robar Carta' para comenzar.");
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

        JPanel hud = createHUD();
        root.add(hud, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(5, 1, 3, 3));
        centro.setOpaque(false);

        panelManoRival = createZonePanel("🂠 Mano del Rival", BG_RIVAL, false);
        panelTrampasRival = createZonePanel("⬇ Zona Trampa Rival", new Color(30, 10, 10), false);
        panelCampoRival = createZonePanel("⚔ Campo Rival", BG_CAMPO, false);
        panelCampoJugador = createZonePanel("🛡 Tu Campo", BG_CAMPO, true);
        panelTrampasJugador = createZonePanel("⬇ Tu Zona Trampa", new Color(10, 30, 10), true);

        centro.add(panelManoRival);
        centro.add(panelTrampasRival);
        centro.add(panelCampoRival);
        centro.add(panelCampoJugador);
        centro.add(panelTrampasJugador);

        root.add(centro, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(5, 0));
        bottom.setOpaque(false);
        bottom.setPreferredSize(new Dimension(0, 200));

        panelManoJugador = createZonePanel("🃏 Tu Mano", new Color(10, 30, 10), true);
        panelManoJugador.setPreferredSize(new Dimension(600, 180));
        bottom.add(panelManoJugador, BorderLayout.CENTER);

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

        JPanel panelBotones = createBotonPanel();
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

        lblLpJ1 = createHUDLabel("❤ J1: 8000 LP", RED_HP);
        lblMazoJ1 = createHUDLabel("🂠 Mazo: 20", Color.LIGHT_GRAY);
        lblTurno = createHUDLabel("TURNO", GOLD);
        lblMazoJ2 = createHUDLabel("🂠 Mazo: 20", Color.LIGHT_GRAY);
        lblLpJ2 = createHUDLabel("❤ J2: 8000 LP", BLUE_HP);

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
        btnRobar.addActionListener(e -> controlador.robarCarta());

        JButton btnJugar = createBtn("▶ Jugar Carta", new Color(60, 150, 60));
        btnJugar.addActionListener(e -> controlador.jugarCartaSeleccionada());

        JButton btnAtacarDirecto = createBtn("⚡ Ataque Directo", new Color(180, 80, 0));
        btnAtacarDirecto.addActionListener(e -> controlador.atacarDirecto());

        JButton btnTerminarTurno = createBtn("⏭ Terminar Turno", new Color(120, 50, 120));
        btnTerminarTurno.addActionListener(e -> controlador.terminarTurno());

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

    public void actualizarTablero() {
        lblLpJ1.setText(" " + controlador.getJ1().getNombre() + ": " + controlador.getJ1().getLp() + " LP");
        lblLpJ2.setText(" " + controlador.getJ2().getNombre() + ": " + controlador.getJ2().getLp() + " LP");
        lblMazoJ1.setText(" Mazo " + controlador.getJ1().getNombre() + ": " + controlador.getJ1().getMazo().size());
        lblMazoJ2.setText(" Mazo " + controlador.getJ2().getNombre() + ": " + controlador.getJ2().getMazo().size());
        lblTurno.setText("TURNO: " + controlador.getTurnoActual().getNombre().toUpperCase());

        actualizarZonaMonstruos(panelCampoJugador, controlador.getTurnoActual().getCampo().getZonaMonstruos(), true);
        actualizarZonaTrampas(panelTrampasJugador, controlador.getTurnoActual().getCampo().getZonaTrampas(), true);
        actualizarMano(panelManoJugador, controlador.getTurnoActual().getMano(), true);

        actualizarZonaMonstruos(panelCampoRival, controlador.getRival().getCampo().getZonaMonstruos(), false);
        actualizarZonaTrampas(panelTrampasRival, controlador.getRival().getCampo().getZonaTrampas(), false);
        actualizarManoRival(panelManoRival, controlador.getRival().getMano().size());

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
                            agregarLog("🃏 Seleccionada: " + carta.getNombre());
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

    public void resaltarCampoRival(boolean activar) {
        Color border = activar ? new Color(255, 100, 50, 200) : new Color(50, 120, 50, 80);
        panelCampoRival.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(border, activar ? 3 : 1),
            BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        panelCampoRival.repaint();
    }

    public void agregarLog(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
