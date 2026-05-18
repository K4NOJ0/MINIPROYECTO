import javax.swing.SwingUtilities;
import vista.MenuInicial;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new MenuInicial().setVisible(true);
        });
    }
}
