

import javax.swing.SwingUtilities;
import controlador.ControladorJuego;
import vista.MenuInicial;
import vista.TableroJuego;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            new MenuInicial((nombre1, nombre2) -> {
                TableroJuego tablero = new TableroJuego(nombre1, nombre2);
                ControladorJuego controlador = new ControladorJuego(nombre1, nombre2, tablero);
                tablero.setTableroListener(controlador);
                tablero.setVisible(true);
            }).setVisible(true);
        });
    }
}