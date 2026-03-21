public class Monstruo {
    private String nombre;
    private int atk;
    private int def;
    private int nivel;

    public Monstruo(String nombre, int atk, int def, int nivel) {
        this.nombre = nombre;
        this.atk = atk;
        this.def = def;
        this.nivel = nivel;
    }

    // Getter para atk
    public int getAtk() {
        return atk;
    }

    // Getter para def
    public int getDef() {
        return def;
    }

    // Getter para nivel
    public int getNivel() {
        return nivel;
    }

    // Método para mostrar la información
    public void mostrarInfo() {
        System.out.println("Monstruo: " + nombre +
                " | ATK: " + atk +
                " | DEF: " + def +
                " | Nivel: " + nivel);
    }
}