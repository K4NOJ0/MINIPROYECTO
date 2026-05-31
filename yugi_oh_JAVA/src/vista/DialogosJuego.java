package vista;

import javax.swing.JOptionPane;
import java.awt.Component;

public class DialogosJuego {
    
    private Component parent;

    public DialogosJuego(Component parent) {
        this.parent = parent;
    }

    public int preguntarModoInvocacion(String nombreMonstruo) {
        return JOptionPane.showOptionDialog(parent,
            "¿En qué posición invocar a " + nombreMonstruo + "?",
            "Modo de Invocación", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, new String[]{"Modo Ataque", "Modo Defensa"}, "Modo Ataque");
    }

    public void mostrarMensajeFinJuego(String mensaje) {
        JOptionPane.showMessageDialog(parent, mensaje, "¡DUELO TERMINADO!", JOptionPane.INFORMATION_MESSAGE);
    }

    public int preguntarRevancha() {
        return JOptionPane.showConfirmDialog(parent, "¿Jugar otra partida?", "Revancha", JOptionPane.YES_NO_OPTION);
    }
}
