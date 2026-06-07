package modelo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import modelo.MazoVacioException;

public class Jugador {

    private String nombre;

    private Stack<Carta>       mazo;

    private LinkedList<Carta>  mano;

    private int   lp = 8000;
    private Campo campo;
    private boolean wabokuActivo;

    public Jugador(String nombre) {
        this.nombre       = nombre;
        this.mazo         = new Stack<>();
        this.mano         = new LinkedList<>();
        this.campo        = new Campo();
        this.wabokuActivo = false;
    }


    public Campo getCampo()   { return campo; }
    public String getNombre() { return nombre; }
    public int getLp()        { return lp; }

    public void setLp(int lp) { this.lp = Math.max(0, lp); }

    public ArrayList<Carta> getMazo() {
        return new ArrayList<>(mazo);
    }

    public ArrayList<Carta> getMano() {
        return new ArrayList<>(mano);
    }

    public LinkedList<Carta> getManoLinked() { return mano; }

    public Stack<Carta> getMazoStack() { return mazo; }

    public boolean isWabokuActivo()          { return wabokuActivo; }
    public void setWabokuActivo(boolean v)   { this.wabokuActivo = v; }


    public void agregarCarta(Carta carta) {
        mazo.push(carta); 
    }

    public String mostrarMano() {
        StringBuilder sb = new StringBuilder("MANO DE " + nombre + ":\n");
        int i = 0;
        for (Carta c : mano) {
            sb.append(i++).append(". ").append(c.mostrarInfo()).append("\n");
        }
        return sb.toString();
    }

    public String recuperarVida(int cantidad) {
        lp += cantidad;
        return nombre + " recupera " + cantidad + " LP";
    }

    public String restarLP(int daño) {
        lp -= daño;
        if (lp < 0) lp = 0;
        return nombre + " pierde " + daño + " LP";
    }

    public String recibirDano(int dano) {
        if (wabokuActivo) {
            wabokuActivo = false;
            return nombre + " bloqueó el daño con Waboku.";
        }
        return restarLP(dano);
    }


    public String robarCarta() {
        if (mazo.isEmpty()) {
            throw new RuntimeException(new MazoVacioException(
                nombre + " intentó robar carta de un mazo vacío."
            ));
        }
        Carta carta = mazo.pop();  
        mano.addLast(carta);        
        return nombre + " robó " + carta.getNombre();
    }

    public String atacar(Monstruo atacante, Monstruo defensor, Jugador enemigo) {
        if (defensor.isModoAtaque()) {
            if (atacante.getAtk() > defensor.getAtk()) {
                int daño = atacante.getAtk() - defensor.getAtk();
                enemigo.getCampo().getZonaMonstruosLinked().remove(defensor);
                enemigo.restarLP(daño);
                return defensor.getNombre() + " FUE DESTRUIDO.";
            } else if (atacante.getAtk() < defensor.getAtk()) {
                int daño = defensor.getAtk() - atacante.getAtk();
                this.getCampo().getZonaMonstruosLinked().remove(atacante);
                this.restarLP(daño);
                return atacante.getNombre() + " FUE DESTRUIDO.";
            } else {
                enemigo.getCampo().getZonaMonstruosLinked().remove(defensor);
                this.getCampo().getZonaMonstruosLinked().remove(atacante);
                return "AMBOS MONSTRUOS FUERON DESTRUIDOS";
            }
        } else {
            if (atacante.getAtk() > defensor.getDef()) {
                enemigo.getCampo().getZonaMonstruosLinked().remove(defensor);
                return defensor.getNombre() + " FUE DESTRUIDO EN DEFENSA.";
            } else if (atacante.getAtk() < defensor.getDef()) {
                int daño = defensor.getDef() - atacante.getAtk();
                this.restarLP(daño);
                return atacante.getNombre() + " NO PUDO DESTRUIR AL DEFENSOR.";
            } else {
                return "EMPATE";
            }
        }
    }

    public String atacarDirecto(Monstruo atacante, Jugador enemigo) {
        enemigo.restarLP(atacante.getAtk());
        return "ATAQUE DIRECTO DE " + atacante.getNombre();
    }

    public String sacrificarMonstruo() {
        if (this.getCampo().getZonaMonstruosLinked().isEmpty()) return "NO HAY MONSTRUOS";
        Monstruo eliminado = this.getCampo().getZonaMonstruosLinked().removeFirst();
        return "EL JUGADOR " + this.getNombre() + " SACRIFICA " + eliminado.getNombre();
    }

    public String destruirMonstruoEnemigo(Jugador enemigo) {
        if (enemigo.getCampo().getZonaMonstruosLinked().isEmpty()) return "EL ENEMIGO NO TIENE MONSTRUOS";
        Monstruo destruido = enemigo.getCampo().getZonaMonstruosLinked().removeFirst();
        return destruido.getNombre() + " FUE DESTRUIDO";
    }

    public void resetTurno() {
        for (Monstruo m : campo.getZonaMonstruos()) {
            m.setYaAtaco(false);
        }
    }
}
