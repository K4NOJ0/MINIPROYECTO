import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
public class Juego {
private boolean primerTurno = true;
    

public void Menu(){
    System.out.println("====BIENVENIDO A YU-GI-OH====");
    System.out.println("QUE DESEA REALIZAR ");
    System.out.println("1.INICIAR PARTDIDA ");
    System.out.println("2.SALIR");
    System.out.println("=============================");
}

public void menuJuego(Jugador turno){
    System.out.println("=======================");
    System.out.println("JUEGA ["+turno.getNombre()+"]");
    System.out.println("QUE DESEA REALIZAR");
    System.out.println("1.ATACAR AL ENEMIGO");
    System.out.println("2.USAR CARTA MAGICA");
    System.out.println("3.PASAR TURNO");
    }

 public void mostrarInfoJugadores(Jugador j1,Jugador j2){
    System.out.println("===========================ESTADO DE LOS JUGADORES============================");
    System.out.println("JUGADOR ["+j1.getNombre()+"]");
    System.out.println("PUNTOS DE LP ["+j1.getLp()+"]");
    j1.mostrarMano();
    System.out.println("monstruos en el campo: ");
    System.out.println("-------------------------------------------------------------------------------");
    System.out.println("JUGADOR ["+j2.getNombre()+"]");
    System.out.println("PUNTOS DE LP ["+j2.getLp()+"]");
    j2.mostrarMano();
    System.out.println("============================================================================");
    
}

public void barajar(ArrayList<Carta> mazo){
Random rnd = new Random();
    for (int i = mazo.size() - 1; i > 0; i--) {
        int j = rnd.nextInt(i + 1);
        Carta tmp = mazo.get(i);
        mazo.set(i, mazo.get(j));
        mazo.set(j, tmp);
    }

}

public void repartirCartas(Jugador j1,Jugador j2,ArrayList<Carta> mazo){
    for (int i = 0; i < mazo.size(); i++) {
        if (i % 2 == 0) j1.agregarCarta(mazo.get(i));
        else j2.agregarCarta(mazo.get(i));
    }
}

public Jugador Turnos(Jugador j1, Jugador j2, Jugador turno){
    if (turno == j1) {
        return j2;
    } else {
        return j1;
    }
}


public void iniciarJuego(Jugador j1, Jugador j2, Jugador turno){

    System.out.println("----------------------------------------------------------");
    System.out.println("LOS DOS DUELISTAS SON :|["+j1.getNombre()+"] Y ["+j2.getNombre()+"]|");
    System.out.println("EL JUGADOR QUE INICIA ES ["+turno.getNombre()+"]");
    System.out.println("----------------------------------------------------------");

    // 5 cartas para cada uno
    for(int i = 0; i < 5; i++){
        j1.robarCarta();
        j2.robarCarta();
    }
}
public Jugador ejecutarTurno(Jugador actual, Jugador enemigo, Scanner escan) {

    System.out.println("\n=======================");
    System.out.println("TURNO DE: " + actual.getNombre());

    // 🔹 ROBAR CARTA
    if (actual.getMazo().isEmpty()) {
        System.out.println(actual.getNombre() + " pierde por no tener cartas");
        return null;
    }
    actual.robarCarta();

    boolean yaJugoCarta = false;
    int opcion;

    do {
        menuJuego(actual);
        opcion = escan.nextInt();

        switch (opcion) {

            case 1: // ATACAR
                if (primerTurno) {
                    System.out.println("❌ No se puede atacar en el primer turno");
                } else {
                    if (actual.getCampo().getZonaMonstruos().isEmpty()) {
                        System.out.println("No tienes monstruos para atacar");
                    } else {
                        // ataque 
                        Monstruo atacante = actual.getCampo().getZonaMonstruos().get(0);

                        if (enemigo.getCampo().getZonaMonstruos().isEmpty()) {
                            enemigo.restarLP(atacante.getAtk());
                            System.out.println("Ataque directo!");
                        } else {
                            Monstruo defensor = enemigo.getCampo().getZonaMonstruos().get(0);
                            actual.atacar(atacante, defensor, enemigo);
                        }
                    }
                }
                break;

            case 2: // USAR CARTA
                if (yaJugoCarta) {
                    System.out.println("❌ Ya jugaste una carta este turno");
                    break;
                }

                if (actual.getMano().isEmpty()) {
                    System.out.println("No tienes cartas");
                    break;
                }

                Carta carta = actual.getMano().remove(0);

                if (carta instanceof Monstruo) {
                    boolean invocado = actual.getCampo().invocarMonstruo((Monstruo) carta);
                    if (invocado) {
                        System.out.println("Invocaste: " + carta.getNombre());
                        yaJugoCarta = true;
                    } else {
                        System.out.println("Campo lleno");
                        actual.getMano().add(carta);
                    }

                } else if (carta instanceof CartaMagica) {
                    ((CartaMagica) carta).activarEfecto(actual);
                    yaJugoCarta = true;
                }

                break;

            case 3:
                System.out.println("Fin del turno");
                break;

            default:
                System.out.println("Opción inválida");
        }

    } while (opcion != 3);

    primerTurno = false;

    //  CAMBIO DE TURNO
    return enemigo;
}


}
