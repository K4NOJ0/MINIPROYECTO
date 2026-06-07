package vista;

import modelo.AnalizadorEstadisticas;
import modelo.AnalizadorEstadisticas.RegistroPartida;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VistaEstadisticas extends JFrame {

    private static final Color BG_DARK  = new Color(8, 18, 8);
    private static final Color GOLD     = new Color(212, 175, 55);
    private static final Color GREEN    = new Color(50, 180, 50);
    private static final Color FG_WHITE = Color.WHITE;

    private final AnalizadorEstadisticas analizador;

    public VistaEstadisticas() {
        analizador = new AnalizadorEstadisticas();
        analizador.cargar();

        setTitle("Yu-Gi-Oh! — Estadísticas Históricas");
        setSize(860, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        root.add(crearPanelTitulo(),   BorderLayout.NORTH);
        root.add(crearPanelCentral(),  BorderLayout.CENTER);
        root.add(crearPanelResumen(),  BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel crearPanelTitulo() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setOpaque(false);

        JLabel titulo = new JLabel("★  ESTADÍSTICAS DEL DUELO  ★");
        titulo.setFont(new Font("Serif", Font.BOLD, 26));
        titulo.setForeground(GOLD);
        p.add(titulo);
        return p;
    }

    private JPanel crearPanelCentral() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setOpaque(false);

        String[] columnas = { "#", "Fecha", "Ganador", "LP Finales" };
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<RegistroPartida> partidas = analizador.getPartidas();
        if (partidas.isEmpty()) {
            modelo.addRow(new Object[]{ "—", "Sin datos", "—", "—" });
        } else {
            int i = 1;
            for (RegistroPartida rp : partidas) {
                modelo.addRow(new Object[]{ i++, rp.getFecha(), rp.getGanador(), rp.getLpFinal() + " LP" });
            }
        }

        JTable tablaHistorial = new JTable(modelo);
        estilizarTabla(tablaHistorial);

        JScrollPane scroll = new JScrollPane(tablaHistorial);
        scroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(GREEN, 1),
            " Historial de partidas ",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Serif", Font.BOLD, 13), GREEN
        ));
        scroll.setBackground(BG_DARK);
        scroll.getViewport().setBackground(new Color(12, 28, 12));

        p.add(scroll, BorderLayout.CENTER);

        p.add(crearTablaVictorias(), BorderLayout.EAST);

        return p;
    }

    private JPanel crearTablaVictorias() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(220, 0));

        String[] cols = { "Jugador", "Victorias" };
        DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        HashMap<String, Integer> victorias = analizador.getVictoriasPorJugador();
        if (victorias.isEmpty()) {
            modelo.addRow(new Object[]{ "Sin datos", 0 });
        } else {
            // Ordenar por victorias descendente
            victorias.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(e -> modelo.addRow(new Object[]{ e.getKey(), e.getValue() }));
        }

        JTable tablaVictorias = new JTable(modelo);
        estilizarTabla(tablaVictorias);

        JScrollPane scroll = new JScrollPane(tablaVictorias);
        scroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(GOLD, 1),
            " Victorias por jugador ",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Serif", Font.BOLD, 12), GOLD
        ));
        scroll.setBackground(BG_DARK);
        scroll.getViewport().setBackground(new Color(20, 18, 5));

        p.add(scroll, BorderLayout.CENTER);
        return p;
    }


    private JPanel crearPanelResumen() {
        JPanel p = new JPanel(new GridLayout(1, 3, 10, 0));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, GOLD));
        p.setPreferredSize(new Dimension(0, 80));

        String masVictorias = analizador.jugadorMasVictorias();
        int total = analizador.totalPartidas();

        RegistroPartida mejorPartida = analizador.partidaConMasLP();
        String mejorLP = (mejorPartida != null)
            ? mejorPartida.getGanador() + " (" + mejorPartida.getLpFinal() + " LP)"
            : "Sin datos";

        p.add(crearTarjetaResumen("Total de partidas", String.valueOf(total), GREEN));
        p.add(crearTarjetaResumen("Más victorias", masVictorias, GOLD));
        p.add(crearTarjetaResumen("Mayor LP final", mejorLP, new Color(100, 180, 255)));

        return p;
    }

    private JPanel crearTarjetaResumen(String etiqueta, String valor, Color color) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBackground(new Color(15, 30, 15));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JLabel lblEtiqueta = new JLabel(etiqueta, JLabel.CENTER);
        lblEtiqueta.setFont(new Font("Serif", Font.PLAIN, 11));
        lblEtiqueta.setForeground(Color.LIGHT_GRAY);

        JLabel lblValor = new JLabel(valor, JLabel.CENTER);
        lblValor.setFont(new Font("Serif", Font.BOLD, 14));
        lblValor.setForeground(color);

        card.add(lblEtiqueta);
        card.add(lblValor);
        return card;
    }


    private void estilizarTabla(JTable tabla) {
        tabla.setBackground(new Color(12, 28, 12));
        tabla.setForeground(FG_WHITE);
        tabla.setFont(new Font("Monospaced", Font.PLAIN, 12));
        tabla.setRowHeight(24);
        tabla.setGridColor(new Color(40, 80, 40));
        tabla.setSelectionBackground(new Color(50, 120, 50));
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setShowHorizontalLines(true);
        tabla.setShowVerticalLines(false);

        tabla.getTableHeader().setBackground(new Color(25, 50, 25));
        tabla.getTableHeader().setForeground(GOLD);
        tabla.getTableHeader().setFont(new Font("Serif", Font.BOLD, 13));

        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(JLabel.CENTER);
        centrado.setBackground(new Color(12, 28, 12));
        centrado.setForeground(FG_WHITE);
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(centrado);
        }
    }

    public static void mostrar() {
        SwingUtilities.invokeLater(() -> new VistaEstadisticas().setVisible(true));
    }
}
