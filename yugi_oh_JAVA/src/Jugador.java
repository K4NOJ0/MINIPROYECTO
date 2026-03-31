import java.util.ArrayList;

public class Jugador {
    private String nombre;
    private ArrayList<Carta> mazo;
    private ArrayList<Carta> mano;
    private int lp = 8000;
    private Campo campo;


    
    

    public Jugador(String nombre) {
        this.nombre = nombre;
        this.mazo = new ArrayList<>();
        this.mano = new ArrayList<>();
        this.campo=new Campo();
    }


    public Campo getCampo() {
        return campo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getLp() {
        return lp;
    }
    

    public ArrayList<Carta> getMazo() {
        return mazo;
    }

    public ArrayList<Carta> getMano() {
        return mano;
    }

    public void agregarCarta(Carta carta) {
     mazo.add(carta);
    }
    public void mostrarMano() {
        System.out.println("MANO DE " + nombre + " :");
        for (Carta carta : mano) {
            carta.mostrarInfo();
        }
      
    }


//metodos de las cartas magicas 
public void recuperarVida(int cantidad) {
        lp += cantidad;
        System.out.println(nombre + " recupera " + cantidad + " lp");

    }


public void restarLP(int daño){
   lp-=daño;
   System.out.println(nombre+ "pierde "+daño +" lp"); 
}

public void destruirMonstruo() {
        for (int i = 0; i < mano.size(); i++) {
            Carta c = mano.get(i);
            if (c instanceof Monstruo) {
                mano.remove(i);
                System.out.println(nombre + " destruye " + c.getNombre());
                return;
            }
        }
        System.out.println(nombre + " no tenia monstruo para destruir");
}

public void robarCarta() {
        if (mazo.isEmpty()) {
            System.out.println(nombre + " no puede robar carta, pierde la partida");
            return;
        }
        Carta carta = mazo.remove(0);
        mano.add(carta);
        System.out.println(nombre + " roba " + carta.getNombre());
    }


    
    public void atacar(Monstruo atacante, Monstruo defensor, Jugador enemigo) {

    if (defensor == null) {
        enemigo.restarLP(atacante.getAtk());
        System.out.println("Ataque directo!");
        return;
    }

    if (atacante.getAtk() > defensor.getAtk()) {
        enemigo.getCampo().getZonaMonstruos().remove(defensor);
        int daño = atacante.getAtk() - defensor.getAtk();
        enemigo.restarLP(daño);
        System.out.println("Monstruo destruido");
    } else if (atacante.getAtk() < defensor.getAtk()) {
        int daño = defensor.getAtk() - atacante.getAtk();
        this.restarLP(daño);
        System.out.println("Tu monstruo es destruido");
    } else {
        enemigo.getCampo().getZonaMonstruos().remove(defensor);
        this.getCampo().getZonaMonstruos().remove(atacante);
        System.out.println("Ambos monstruos destruidos");
    }
}


public void defender(){


}


}