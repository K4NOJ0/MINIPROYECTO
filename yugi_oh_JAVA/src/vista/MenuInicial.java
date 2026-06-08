package vista;

import vista.VistaEstadisticas;
import javax.swing.*;
import java.awt.*;

public class MenuInicial extends JFrame {

    public interface OnDueloIniciadoListener {
        void onDueloIniciado(String nombre1, String nombre2);
    }

    private OnDueloIniciadoListener listener;

    public MenuInicial(OnDueloIniciadoListener listener) {
        this.listener = listener;
        initUI();
    }

    public MenuInicial() {
        this(null);
    }

    private void initUI() {
        setTitle("Yu-Gi-Oh! Duelo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = buildBackgroundPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 20, 5, 20);

        JLabel title = new JLabel("YU-GI-OH!");
        title.setFont(new Font("Serif", Font.BOLD, 56));
        title.setForeground(new Color(212, 175, 55));
        panel.add(title, gbc);

        gbc.gridy = 1;
        JLabel subtitle = new JLabel("  SIMULADOR DE DUELO  ");
        subtitle.setFont(new Font("Serif", Font.ITALIC, 18));
        subtitle.setForeground(new Color(180, 150, 255));
        panel.add(subtitle, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(25, 20, 5, 20);
        JLabel lbl1 = new JLabel("Nombre del Duelista 1:");
        lbl1.setFont(new Font("Serif", Font.BOLD, 16));
        lbl1.setForeground(Color.WHITE);
        panel.add(lbl1, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(5, 20, 5, 20);
        JTextField campo1 = createStyledField("KEMPACHI");
        panel.add(campo1, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(12, 20, 5, 20);
        JLabel lbl2 = new JLabel("Nombre del Duelista 2:");
        lbl2.setFont(new Font("Serif", Font.BOLD, 16));
        lbl2.setForeground(Color.WHITE);
        panel.add(lbl2, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(5, 20, 5, 20);
        JTextField campo2 = createStyledField("SHINRA");
        panel.add(campo2, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(25, 20, 8, 20);
        JButton btnDuelar = createGoldButton("  ¡COMENZAR DUELO!  ", new Color(212, 175, 55));
        btnDuelar.addActionListener(e -> comenzarDuelo(campo1.getText(), campo2.getText()));
        panel.add(btnDuelar, gbc);

        gbc.gridy = 7;
        gbc.insets = new Insets(0, 20, 10, 20);
        JButton btnEstadisticas = createGoldButton("  Ver Estadísticas  ", new Color(80, 150, 200));
        btnEstadisticas.addActionListener(e -> VistaEstadisticas.mostrar());
        panel.add(btnEstadisticas, gbc);

        setContentPane(panel);
    }

    private void comenzarDuelo(String n1, String n2) {
        String nombre1 = n1.trim().isEmpty() ? "DUELISTA 1" : n1.trim();
        String nombre2 = n2.trim().isEmpty() ? "DUELISTA 2" : n2.trim();
        dispose();
        if (listener != null) {
            listener.onDueloIniciado(nombre1, nombre2);
        } else {
            TableroJuego tablero = new TableroJuego(nombre1, nombre2);
            tablero.setVisible(true);
        }
    }

    private JPanel buildBackgroundPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(10, 5, 30),
                        getWidth(), getHeight(), new Color(40, 15, 60));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(212, 175, 55, 120));
                g2.setStroke(new java.awt.BasicStroke(3));
                g2.drawRect(15, 15, getWidth() - 30, getHeight() - 30);
                g2.setColor(new Color(212, 175, 55, 60));
                int[][] stars = { { 50, 50 }, { 620, 80 }, { 100, 400 }, { 600, 380 }, { 350, 30 }, { 200, 450 } };
                for (int[] s : stars)
                    drawStar(g2, s[0], s[1], 12, 5);
            }

            private void drawStar(Graphics2D g2, int x, int y, int r, int points) {
                double angle = Math.PI / points;
                int[] xs = new int[points * 2], ys = new int[points * 2];
                for (int i = 0; i < points * 2; i++) {
                    double rad = (i % 2 == 0) ? r : r / 2.5;
                    xs[i] = (int) (x + rad * Math.sin(i * angle));
                    ys[i] = (int) (y - rad * Math.cos(i * angle));
                }
                g2.fillPolygon(xs, ys, points * 2);
            }
        };
    }

    private JTextField createStyledField(String placeholder) {
        JTextField tf = new JTextField(placeholder, 20);
        tf.setFont(new Font("Serif", Font.PLAIN, 15));
        tf.setBackground(new Color(30, 15, 60));
        tf.setForeground(new Color(212, 175, 55));
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(212, 175, 55), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        tf.setHorizontalAlignment(JTextField.CENTER);
        return tf;
    }

    private JButton createGoldButton(String text, Color baseColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed() ? baseColor.darker()
                        : getModel().isRollover() ? baseColor.brighter()
                                : baseColor;
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(10, 5, 30));
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        btn.setFont(new Font("Serif", Font.BOLD, 16));
        btn.setPreferredSize(new Dimension(300, 46));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}