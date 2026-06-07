package modelo;

import java.util.Stack;

public class GestorMementos {
    private final Stack<EstadoPartida> historial = new Stack<>();

    public void guardarEstado(EstadoPartida estado) {
        historial.push(estado);
    }

    public EstadoPartida deshacer() {
        if (!historial.isEmpty()) {
            return historial.pop();
        }
        return null;
    }
    
    public boolean tieneHistorial() {
        return !historial.isEmpty();
    }
}
