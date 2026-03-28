import java.util.ArrayList;

public class Campo {

    private ArrayList<Monstruo> zonaMonstruos;
    private ArrayList<Carta> zonaMagiasTrampas;

    public Campo() {
        zonaMonstruos = new ArrayList<>();
        zonaMagiasTrampas = new ArrayList<>();
    }

    // Agregar monstruo
    public boolean invocarMonstruo(Monstruo monstruo) {
        if (zonaMonstruos.size() < 5) {
            zonaMonstruos.add(monstruo);
            return true;
        }
        return false; // campo lleno
    }

    public boolean invocarMagicas(CartaMagica cartaMagica) {
        if (zonaMagiasTrampas.size() < 5) {
            zonaMagiasTrampas.add(cartaMagica);
            return true;
        }
        return false; // campo lleno
    }

    // Obtener monstruos
    public ArrayList<Monstruo> getZonaMonstruos() {
        return zonaMonstruos;
    }
 
}
