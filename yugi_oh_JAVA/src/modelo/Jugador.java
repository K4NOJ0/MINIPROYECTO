package modelo;
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

    public Campo getCampo() {
        return campo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getLp() {
        return lp;
    }

    public void setLp(int lp) {
        this.lp = Math.max(0, lp);
    }

    public ArrayList<Carta> getMazo() {
        return mazo;
    }

    public ArrayList<Carta> getMano() {
        return mano;
    }

    public boolean isWabokuActivo() {
        return wabokuActivo;
    }

    public void setWabokuActivo(boolean v) {
        this.wabokuActivo = v;
    }

    public void agregarCarta(Carta carta) {
        mazo.add(carta);
    }

    public String mostrarMano() {

        String resultado = "MANO DE " + nombre + ":\n";

        for (int i = 0; i < mano.size(); i++) {

            resultado += i + ". " +
                         mano.get(i).mostrarInfo() +
                         "\n";
        }

        return resultado;
    }

    public String recuperarVida(int cantidad) {

        lp += cantidad;

        return nombre +
               " recupera " +
               cantidad +
               " LP";
    }

    public String restarLP(int daño) {

        lp -= daño;

        if (lp < 0) {
            lp = 0;
        }

        return nombre +
               " pierde " +
               daño +
               " LP";
    }

    public String recibirDano(int dano) {

        if (wabokuActivo) {

            wabokuActivo = false;

            return nombre +
                   " bloqueó el daño con Waboku.";
        }

        return restarLP(dano);
    }

    public String robarCarta() {

        if (mazo.isEmpty()) {

            return nombre +
                   " no puede robar carta";
        }

        Carta carta = mazo.remove(0);

        mano.add(carta);

        return nombre +
               " robó " +
               carta.getNombre();
    }

    public String atacar(
        Monstruo atacante,
        Monstruo defensor,
        Jugador enemigo
    ) {

        if (defensor.isModoAtaque()) {

            if (atacante.getAtk() > defensor.getAtk()) {

                int daño =
                    atacante.getAtk() -
                    defensor.getAtk();

                enemigo.getCampo()
                       .getZonaMonstruos()
                       .remove(defensor);

                enemigo.restarLP(daño);

                return defensor.getNombre() +
                       " FUE DESTRUIDO.";

            } else if (
                atacante.getAtk() <
                defensor.getAtk()
            ) {

                int daño =
                    defensor.getAtk() -
                    atacante.getAtk();

                this.getCampo()
                    .getZonaMonstruos()
                    .remove(atacante);

                this.restarLP(daño);

                return atacante.getNombre() +
                       " FUE DESTRUIDO.";

            } else {

                enemigo.getCampo()
                       .getZonaMonstruos()
                       .remove(defensor);

                this.getCampo()
                    .getZonaMonstruos()
                    .remove(atacante);

                return "AMBOS MONSTRUOS FUERON DESTRUIDOS";
            }

        } else {

            if (atacante.getAtk() > defensor.getDef()) {

                enemigo.getCampo()
                       .getZonaMonstruos()
                       .remove(defensor);

                return defensor.getNombre() +
                       " FUE DESTRUIDO EN DEFENSA.";

            } else if (
                atacante.getAtk() <
                defensor.getDef()
            ) {

                int daño =
                    defensor.getDef() -
                    atacante.getAtk();

                this.restarLP(daño);

                return atacante.getNombre() +
                       " NO PUDO DESTRUIR AL DEFENSOR.";

            } else {

                return "EMPATE";
            }
        }
    }

    public String atacarDirecto(
        Monstruo atacante,
        Jugador enemigo
    ) {

        enemigo.restarLP(atacante.getAtk());

        return "ATAQUE DIRECTO DE " +
               atacante.getNombre();
    }

    public String sacrificarMonstruo() {

        if (
            this.getCampo()
                .getZonaMonstruos()
                .isEmpty()
        ) {

            return "NO HAY MONSTRUOS";
        }

        Monstruo eliminado =
            this.getCampo()
                .getZonaMonstruos()
                .remove(0);

        return "EL JUGADOR " +
               this.getNombre() +
               " SACRIFICA " +
               eliminado.getNombre();
    }

    public String destruirMonstruoEnemigo(
        Jugador enemigo
    ) {

        ArrayList<Monstruo> zona =
            enemigo.getCampo()
                   .getZonaMonstruos();

        if (zona.isEmpty()) {

            return "EL ENEMIGO NO TIENE MONSTRUOS";
        }

        Monstruo destruido = zona.remove(0);

        return destruido.getNombre() +
               " FUE DESTRUIDO";
    }

    public void resetTurno() {

        for (Monstruo m :
            campo.getZonaMonstruos()) {

            m.setYaAtaco(false);
        }
    }
}