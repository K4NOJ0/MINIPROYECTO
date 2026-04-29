import java.util.ArrayList;

public class Jugador {
    private String nombre;
    private ArrayList<Carta> mazo;
    private ArrayList<Carta> mano;
    private int lp = 8000;
    private Campo campo;
    private boolean wabokuActivo;

    public Jugador(String nombre) {
        this.nombre = nombre;
        this.mazo = new ArrayList<>();
        this.mano = new ArrayList<>();
        this.campo = new Campo();
        this.wabokuActivo = false;
    }

    public Campo getCampo() { return campo; }
    public String getNombre() { return nombre; }
    public int getLp() { return lp; }
    public void setLp(int lp) { this.lp = Math.max(0, lp); }
    public ArrayList<Carta> getMazo() { return mazo; }
    public ArrayList<Carta> getMano() { return mano; }
    public boolean isWabokuActivo() { return wabokuActivo; }
    public void setWabokuActivo(boolean v) { this.wabokuActivo = v; }

    public void agregarCarta(Carta carta) { mazo.add(carta); }

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

    // Nombre original consola
    public void restarLP(int daño) {
        lp -= daño;
        if (lp < 0) lp = 0;
        System.out.println(nombre + " PIERDE " + daño + " LP. LP RESTANTE: " + lp);
    }

    // Alias para la GUI (respeta waboku)
    public void recibirDano(int dano) {
        if (wabokuActivo) {
            wabokuActivo = false;
            System.out.println(nombre + " bloqueó el daño con Waboku.");
            return;
        }
        restarLP(dano);
    }

    public boolean robarCarta() {
        if (mazo.isEmpty()) {
            System.out.println(nombre + " NO PUEDE ROBAR CARTA, PIERDE EL DUELO");
            return false;
        }
        Carta carta = mazo.remove(0);
        mano.add(carta);
        System.out.println(nombre + " ROBA: " + carta.getNombre());
        return true;
    }

    public void atacar(Monstruo atacante, Monstruo defensor, Jugador enemigo) {
        System.out.println("\n" + atacante.getNombre() + " (ATK: " + atacante.getAtk() + ") ATACA A " + defensor.getNombre());
        if (defensor.isModoAtaque()) {
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
                System.out.println("¡AMBOS MONSTRUOS FUERON DESTRUIDOS!");
            }
        } else {
            if (atacante.getAtk() > defensor.getDef()) {
                enemigo.getCampo().getZonaMonstruos().remove(defensor);
                System.out.println(defensor.getNombre() + " FUE DESTRUIDO EN MODO DEFENSA.");
            } else if (atacante.getAtk() < defensor.getDef()) {
                int daño = defensor.getDef() - atacante.getAtk();
                this.restarLP(daño);
                System.out.println(atacante.getNombre() + " NO PUDO DESTRUIR AL DEFENSOR, RECIBES " + daño + " DE DAÑO.");
            } else {
                System.out.println("EMPATE, NADIE ES DESTRUIDO.");
            }
        }
    }

    public void atacarDirecto(Monstruo atacante, Jugador enemigo) {
        System.out.println("\n¡ATAQUE DIRECTO DE " + atacante.getNombre() + "!");
        enemigo.restarLP(atacante.getAtk());
    }

    public void sacrificarMonstruo() {
        Monstruo eliminado = this.getCampo().getZonaMonstruos().remove(0);
        System.out.println("EL JUGADOR " + this.getNombre() + " SACRIFICA " + eliminado.getNombre());
    }

    public void destruirMonstruoEnemigo(Jugador enemigo) {
        ArrayList<Monstruo> zona = enemigo.getCampo().getZonaMonstruos();
        if (zona.isEmpty()) {
            System.out.println("EL ENEMIGO NO TIENE MONSTRUOS EN EL CAMPO.");
            return;
        }
        System.out.println("ELIGE QUÉ MONSTRUO ENEMIGO DESTRUIR:");
        for (int i = 0; i < zona.size(); i++) {
            System.out.println(i + ". " + zona.get(i).getNombre());
        }
    }

    // Alias para la GUI
    public void resetTurno() {
        for (Monstruo m : campo.getZonaMonstruos()) {
            m.setYaAtaco(false);
        }
    }
}
