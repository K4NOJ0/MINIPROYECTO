public class Monstruo extends Carta {
    private int atk;
    private int def;
    private int nivel;
    private boolean modoAtaque; // true = ATK, false = DEF

    public Monstruo(String nombre, int atk, int def, int nivel) {
        super(nombre);
        this.atk = atk;
        this.def = def;
        this.nivel = nivel;
        this.modoAtaque = true; // por defecto en ataque
    }

    public int getAtk() { return atk; }
    public void setAtk(int atk) { this.atk = atk; }
    public int getDef() { return def; }
    public int getNivel() { return nivel; }
    public boolean isModoAtaque() { return modoAtaque; }
    public void setModoAtaque(boolean modoAtaque) { this.modoAtaque = modoAtaque; }

    @Override
    public void mostrarInfo() {
        String modo = modoAtaque ? "ATK" : "DEF";
        System.out.println("MONSTRUO: " + nombre + " | ATK: " + atk + " | DEF: " + def + " | NIVEL: " + nivel + " ESTRELLAS | MODO: " + modo);
    }
}