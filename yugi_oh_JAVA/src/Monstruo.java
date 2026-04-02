public class Monstruo extends Carta {
    private int atk;
    private int def;
    private int nivel;

    public Monstruo(String nombre, int atk, int def, int nivel) {
        super(nombre);
        this.atk = atk;
        this.def = def;
        this.nivel = nivel;
    }

    public int getAtk() {
        return atk;
    }

    public int getDef() {
        return def;
    }

    public int getNivel() {
        return nivel;
    }

    @Override
    public void mostrarInfo() {
        System.out.println("MONSTRUO: " + nombre + " | ATK: " + atk + " | DEF: " + def + " | NIVEL: " + nivel+" ESTRELLAS");
    }
//nueva funcion de verificacion
public boolean Tienemostruo (Jugador turno){
for(Carta carta:turno.getMano()){
    if(carta instanceof Monstruo ){
        return true;
    }
}
return false;
}


}
    

