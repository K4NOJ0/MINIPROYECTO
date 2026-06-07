package modelo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import modelo.CampoLlenoException;


public class Campo {

    private LinkedList<Monstruo>    zonaMonstruos;
    private LinkedList<CartaTrampa> zonaTrampas;

    private Stack<Carta> historialJugadas;

    public Campo() {
        zonaMonstruos   = new LinkedList<>();
        zonaTrampas     = new LinkedList<>();
        historialJugadas = new Stack<>();
    }

    public boolean invocarMonstruo(Monstruo monstruo) {
        if (zonaMonstruos.size() >= 5) {
            return false;   
        }
        zonaMonstruos.addLast(monstruo);
        historialJugadas.push(monstruo);   
        return true;
    }


    public boolean colocarTrampa(CartaTrampa trampa) {
        if (zonaTrampas.size() >= 5) {
            return false;
        }
        zonaTrampas.addLast(trampa);
        historialJugadas.push(trampa);
        return true;
    }

    public ArrayList<Monstruo> getZonaMonstruos() {
        return new ArrayList<>(zonaMonstruos);
    }

    public ArrayList<CartaTrampa> getZonaTrampas() {
        return new ArrayList<>(zonaTrampas);
    }

    public LinkedList<Monstruo> getZonaMonstruosLinked() {
        return zonaMonstruos;
    }

    public LinkedList<CartaTrampa> getZonaTrampasLinked() {
        return zonaTrampas;
    }

    public Carta ultimaCartaJugada() {
        return historialJugadas.isEmpty() ? null : historialJugadas.peek();
    }

    public Carta deshacerUltimaJugada() {
        return historialJugadas.isEmpty() ? null : historialJugadas.pop();
    }

    public void limpiar() {
        zonaMonstruos.clear();
        zonaTrampas.clear();
        historialJugadas.clear();
    }
}
