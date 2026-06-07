package modelo;

import vista.Observador;
import java.util.ArrayList;
import java.util.List;

public class Observable {
    private List<Observador> observadores = new ArrayList<>();

    public void agregarObservador(Observador o) {
        observadores.add(o);
    }

    public void eliminarObservador(Observador o) {
        observadores.remove(o);
    }

    public void notificarObservadores() {
        for (Observador o : observadores) {
            o.actualizar();
        }
    }
}
