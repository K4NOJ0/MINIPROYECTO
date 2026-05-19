package vista;

import javax.swing.*;
import java.awt.*;
import controlador.ControladorJuego;

public class MenuInicial extends JFrame {
    public MenuInicial() {
        setTitle("Yu-Gi-Oh! — Menú Inicial");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420,200);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8,8));

        JPanel center = new JPanel(new GridLayout(2,2,8,8));
        center.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        center.add(new JLabel("Jugador 1:"));
        JTextField tf1 = new JTextField("Jugador 1");
        center.add(tf1);
        center.add(new JLabel("Jugador 2:"));
        JTextField tf2 = new JTextField("Jugador 2");
        center.add(tf2);

        JButton btnStart = new JButton("Comenzar partida");
        btnStart.addActionListener(e -> {
            String n1 = tf1.getText().trim();
            String n2 = tf2.getText().trim();
            if (n1.isEmpty()) n1 = "Jugador 1";
            if (n2.isEmpty()) n2 = "Jugador 2";
            
            ControladorJuego controlador = new ControladorJuego(n1, n2, null);
            TableroJuego vista = new TableroJuego(controlador);
            controlador.setVista(vista);
            vista.setVisible(true);
            dispose();
        });

        add(center, BorderLayout.CENTER);
        JPanel bottom = new JPanel();
        bottom.add(btnStart);
        add(bottom, BorderLayout.SOUTH);
    }
}
