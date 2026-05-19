package modelo;
import java.util.ArrayList;

public class Campo {
    private ArrayList<Monstruo> zonaMonstruos;
    private ArrayList<CartaTrampa> zonaTrampas;

    public Campo() {
        zonaMonstruos = new ArrayList<>();
        zonaTrampas = new ArrayList<>();
    }

    public boolean invocarMonstruo(Monstruo monstruo) {
        if (zonaMonstruos.size() < 5) {
            zonaMonstruos.add(monstruo);
            return true;
        }
        return false;
    }

    public boolean colocarTrampa(CartaTrampa trampa) {
        if (zonaTrampas.size() < 5) {
            zonaTrampas.add(trampa);
            return true;
        }
        return false;
    }

    public ArrayList<Monstruo> getZonaMonstruos() { return zonaMonstruos; }
    public ArrayList<CartaTrampa> getZonaTrampas() { return zonaTrampas; }

    public void limpiar() {
        zonaMonstruos.clear();
        zonaTrampas.clear();
    }
}
