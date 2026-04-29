public class Monstruo extends Carta {
    private int atk;
    private int def;
    private int nivel;
<<<<<<< HEAD
    private boolean modoAtaque; // true = ATK, false = DEF
=======
    private boolean modoAtaque;
    private boolean yaAtaco;
>>>>>>> e0f5b056de62eab8203dd8f3e2059a3eb6439333

    public Monstruo(String nombre, int atk, int def, int nivel) {
        super(nombre);
        this.atk = atk;
        this.def = def;
        this.nivel = nivel;
<<<<<<< HEAD
        this.modoAtaque = true; // por defecto en ataque
    }

    public int getAtk() { 
        return atk;
     }
    public void setAtk(int atk) {
         this.atk = atk; 
        }
    public int getDef() { 
        return def;
     }
    public int getNivel() { 
        return nivel; 
    }
    public boolean isModoAtaque() {
         return modoAtaque;
         }
    public void setModoAtaque(boolean modoAtaque) {
         this.modoAtaque = modoAtaque; 
        }

    @Override
    public void mostrarInfo() {
       String modo;

        if (modoAtaque) {
            modo = "ATK";
        } else {
            modo = "DEF";
        }
        System.out.println("MONSTRUO: " + nombre + " | ATK: " + atk + " | DEF: " + def + " | NIVEL: " + nivel + " ESTRELLAS | MODO: " + modo);
    }
}
=======
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
    public void mostrarInfo() {
        String modo = modoAtaque ? "ATK" : "DEF";
        System.out.println("MONSTRUO: " + nombre + " | ATK: " + atk + " | DEF: " + def + " | NIVEL: " + nivel + " | MODO: " + modo);
    }

    @Override
    public String getTipo() { return "Monstruo"; }

    @Override
    public String getDescripcion() {
        return "ATK: " + atk + " / DEF: " + def + " / Nivel: " + nivel;
    }
}
>>>>>>> e0f5b056de62eab8203dd8f3e2059a3eb6439333
