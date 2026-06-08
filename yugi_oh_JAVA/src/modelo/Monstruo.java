package modelo;
public class Monstruo extends Carta {
    private int atk;
    private int def;
    private int nivel;
    private boolean modoAtaque;
    private boolean yaAtaco;

    public Monstruo(String nombre, int atk, int def, int nivel) {
        super(nombre);
        this.atk = atk;
        this.def = def;
        this.nivel = nivel;
        this.modoAtaque = true;
        this.yaAtaco = false;
        
    }

    public int getAtk() { return atk; }
    public void setAtk(int atk) { this.atk = atk; }
    public int getDef() { return def; }
    public int getNivel() { return nivel; }


    public boolean isModoAtaque() { return modoAtaque; }
    public void setModoAtaque(boolean modoAtaque) { this.modoAtaque = modoAtaque; }


    public boolean isEnModoAtaque() { return modoAtaque; }
    public void setEnModoAtaque(boolean modo) { this.modoAtaque = modo; }

    public boolean isYaAtaco() { return yaAtaco; }
    public void setYaAtaco(boolean v) { this.yaAtaco = v; }

    @Override
public String mostrarInfo() {

    String modo = modoAtaque ? "ATK" : "DEF";

    return "MONSTRUO: " + nombre +
           " | ATK: " + atk +
           " | DEF: " + def +
           " | NIVEL: " + nivel +
           " | MODO: " + modo;
}

    @Override
    public String getTipo() { return "Monstruo"; }

    @Override
    public String getDescripcion() {
        return "ATK: " + atk + " / DEF: " + def + " / Nivel: " + nivel;
    }
}
