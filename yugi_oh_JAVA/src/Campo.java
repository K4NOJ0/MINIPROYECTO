import java.util.ArrayList;

public class Campo {
    private ArrayList<Monstruo> zonaMonstruos;

    public Campo() {
        zonaMonstruos = new ArrayList<>();
    }

    public boolean invocarMonstruo(Monstruo monstruo) {
        if (zonaMonstruos.size() < 5) {
            zonaMonstruos.add(monstruo);
            return true;
        }
        return false;
    }

    public ArrayList<Monstruo> getZonaMonstruos() {
        return zonaMonstruos;
    }
}