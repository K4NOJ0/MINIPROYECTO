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
        this.campo = new Campo();
    }

    public Campo getCampo() { 
        return campo;
     }
    public String getNombre(){ 
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
        System.out.println("MANO DE " + nombre + ":");
        for (int i = 0; i < mano.size(); i++) {
            System.out.print(i + ". ");
            mano.get(i).mostrarInfo();
        }

    }

    public void recuperarVida(int cantidad) {
        lp += cantidad;
        System.out.println(nombre + " RECUPERA " + cantidad + " LP. TOTAL: " + lp);
    }

    public void restarLP(int daño) {
        lp -= daño;
        if (lp < 0) lp = 0;
        System.out.println(nombre + " PIERDE " + daño + " LP. LP RESTANTE: " + lp);
    }

    // Destruye un monstruo del campo del ENEMIGO (para cartas mágicas)
    public void destruirMonstruoEnemigo(Jugador enemigo) {
        ArrayList<Monstruo> zona = enemigo.getCampo().getZonaMonstruos();
        if (zona.isEmpty()) {
            System.out.println("EL ENEMIGO NO TIENE MOSTRUOS EN EL CAMPO.");
            return;
        }
        System.out.println("ELIGE QUE MOSTRUO ENEMIGO DESTRUIR:");
        for (int i = 0; i < zona.size(); i++) {
            System.out.println(i + ". " + zona.get(i).getNombre());
        }
    }

    

    public void robarCarta() {
        if (mazo.isEmpty()) {
            System.out.println(nombre + " NO PUEDE ROBAR CARTA PIERDE EL DUELO");
            return;
        }
        Carta carta = mazo.remove(0);
        mano.add(carta);
        System.out.println(nombre + " ROBA: " + carta.getNombre());
    }

    // RF3: Combate completo con modo ataque/defensa
    public void atacar(Monstruo atacante, Monstruo defensor, Jugador enemigo) {
        System.out.println("\n " + atacante.getNombre() + " (ATK: " + atacante.getAtk() + ") ATACA A " + defensor.getNombre());

        if (defensor.isModoAtaque()) {
            // Ataque vs Ataque
            if (atacante.getAtk() > defensor.getAtk()) {
                int daño = atacante.getAtk() - defensor.getAtk();
                enemigo.getCampo().getZonaMonstruos().remove(defensor);
                enemigo.restarLP(daño);
                System.out.println(defensor.getNombre() + " FUE DESTRUIDO.");
            } else if (atacante.getAtk() < defensor.getAtk()) {
                int daño = defensor.getAtk() - atacante.getAtk();
                this.getCampo().getZonaMonstruos().remove(atacante);
                this.restarLP(daño);
                System.out.println(atacante.getNombre() + " FUE DESTRUIDO.");
            } else {
                enemigo.getCampo().getZonaMonstruos().remove(defensor);
                this.getCampo().getZonaMonstruos().remove(atacante);
                System.out.println("¡AMBOS MOSTRUOS FUERON DESTRUDOS!");
            }
        } else {
            // Ataque vs Defensa
            if (atacante.getAtk() > defensor.getDef()) {
                // Destruye al defensor pero NO hay daño de LP (RF3: "si estaba en ataque")
                enemigo.getCampo().getZonaMonstruos().remove(defensor);
                System.out.println(defensor.getNombre() + " FUE DESTRUIDO EN MODO DEFENSA SIN DAÑO DE LP.");
            } else if (atacante.getAtk() < defensor.getDef()) {
                // El atacante no destruye, pero el dueño del atacante recibe daño de diferencia
                int daño = defensor.getDef() - atacante.getAtk();
                this.restarLP(daño);
                System.out.println(atacante.getNombre() + " NO PUDO DESTRUIR AL DEFENSOR RECIBES  " + daño + "  DE DAÑO.");
            } else {
                // Empate en modo defensa: nadie recibe daño, nadie se destruye
                System.out.println("EMPATE NADIE ES DESTRUIDO.");
            }
        }
    }

    // Ataque directo al jugador enemigo
    public void atacarDirecto(Monstruo atacante, Jugador enemigo) {
        System.out.println("\n ¡ATAQUE DIRECTO DE " + atacante.getNombre() + "!");
        enemigo.restarLP(atacante.getAtk());
    }

    public void sacrificarMonstruo(){
       
        Monstruo eliminado= this.getCampo().getZonaMonstruos().remove(0);
        
         System.out.println("EL JUGADOR  "+this.getNombre()+" SACRIFICA " +eliminado.getNombre());

   }
   
    }
