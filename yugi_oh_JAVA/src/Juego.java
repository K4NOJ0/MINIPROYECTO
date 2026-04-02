import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
public class Juego {
private boolean primerTurno = true;
    

public void Menu(){
    System.out.println("====BIENVENIDO A YU-GI-OH====");
    System.out.println("QUE DESEA REALIZAR ");
    System.out.println("1.INICIAR PARTIDA ");
    System.out.println("2.SALIR");
    System.out.println("=============================");
}

public void menuJuego(Jugador turno){
    System.out.println("=======================");
    System.out.println("JUEGA ["+turno.getNombre()+"]");
    System.out.println("QUE DESEA REALIZAR");
    System.out.println("1. ATACAR AL ENEMIGO");
    System.out.println("2. INVOCAR MONSTRUO");
    System.out.println("3. USAR CARTA MAGICA");
    System.out.println("4. PASAR TURNO");
}

 public void mostrarInfoJugadores(Jugador j1,Jugador j2){
    System.out.println("===========================ESTADO DE LOS JUGADORES============================");
    System.out.println("JUGADOR ["+j1.getNombre()+"]");
    System.out.println("PUNTOS DE LP ["+j1.getLp()+"]");
    j1.mostrarMano();
    System.out.println("monstruos en el campo: ");
    if(j1.getCampo().getZonaMonstruos().isEmpty()){
        System.out.println("EL JUGADOR NO TIENE MOSTRUOS EN EL CAMPO");
    }else{
    for (Monstruo m : j1.getCampo().getZonaMonstruos()) {
    System.out.println("- " + m.getNombre() + " ATK: " + m.getAtk());
    }
   
    }
    System.out.println("-------------------------------------------------------------------------------");
    System.out.println("JUGADOR ["+j2.getNombre()+"]");
    System.out.println("PUNTOS DE LP ["+j2.getLp()+"]");
    j2.mostrarMano();
    System.out.println("monstruos en el campo: ");
    if(j2.getCampo().getZonaMonstruos().isEmpty()){
        System.out.println("EL JUGADOR NO TIENE MOSTRUOS EN EL CAMPO");
    }else{
        for (Monstruo m : j2.getCampo().getZonaMonstruos()) {
        System.out.println("- " + m.getNombre() + " ATK: " + m.getAtk());
    }
    
}
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
        System.out.println(actual.getNombre() + " NO TIENE CARTAS EN EL MAZO");
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
                    System.out.println("NO SE PUEDE ATACAR EN EL PRIMER TURNO ESCOJA OTRA OPCION");
                } else {
                    if (actual.getCampo().getZonaMonstruos().isEmpty()) {
                        System.out.println("NO TIENES MOSTRUOS EN EL CAMPO PARA ATACAR");
                    } else {
                         System.out.println("TUS MONSTRUOS:");
                        for (int i = 0; i < actual.getCampo().getZonaMonstruos().size(); i++) {
                            Monstruo m = actual.getCampo().getZonaMonstruos().get(i);
                            System.out.println(i + ". " + m.getNombre() + " ATK: " + m.getAtk());
                        }
                        // ataque 
                        Monstruo atacante = actual.getCampo().getZonaMonstruos().get(0);

                        if (enemigo.getCampo().getZonaMonstruos().isEmpty()) {
                            enemigo.restarLP(atacante.getAtk());
                            System.out.println("ATAQUE DIRECTO!");
                        } else {
                            Monstruo defensor = enemigo.getCampo().getZonaMonstruos().get(0);
                            actual.atacar(atacante, defensor, enemigo);
                        }
                    }
                }
                break;
                case 2: // INVOCAR MONSTRUO

    if (yaJugoCarta) {
        System.out.println("YA INVOCASTE ESTE TURNO");
        break;
    }

    // mostrar solo monstruos
    System.out.println("ELIGE UN MONSTRUO:");
    for (int i = 0; i < actual.getMano().size(); i++) {
        if (actual.getMano().get(i) instanceof Monstruo) {
            System.out.println(i + ". " + actual.getMano().get(i).getNombre());
        }
    }

    int eleccion = escan.nextInt();

    if (eleccion < 0 || eleccion >= actual.getMano().size() ||
        !(actual.getMano().get(eleccion) instanceof Monstruo)) {
        System.out.println("SELECCION INVALIDA");
        break;
    }

    Monstruo m = (Monstruo) actual.getMano().remove(eleccion);

    if (actual.getCampo().invocarMonstruo(m)) {
        System.out.println("INVOCASTE: " + m.getNombre());
        yaJugoCarta = true;
    } else {
        System.out.println("CAMPO LLENO");
        actual.getMano().add(m);
    }

    break;

           

    
        case 3: // USAR CARTA MAGICA

    // mostrar solo mágicas
    System.out.println("ELIGE UNA CARTA MAGICA:");
    for (int i = 0; i < actual.getMano().size(); i++) {
        if (actual.getMano().get(i) instanceof CartaMagica) {
            System.out.println(i + ". " + actual.getMano().get(i).getNombre());
        }
    }

    int eleccionMagica = escan.nextInt();

    if (eleccionMagica < 0 || eleccionMagica >= actual.getMano().size() ||
        !(actual.getMano().get(eleccionMagica) instanceof CartaMagica)) {
        System.out.println("SELECCION INVALIDA");
        break;
    }

    CartaMagica magia = (CartaMagica) actual.getMano().remove(eleccionMagica);
    magia.activarEfecto(actual);

    System.out.println("ACTIVASTE: " + magia.getNombre());

    break;

    case 4:
    System.out.println("FIN DEL TURNO");
    break;


            default:
                System.out.println("OPCION INVALIDA");
        }

    } while (opcion != 4);

    primerTurno = false;

    // 🔹 VERIFICAR SI ALGUIEN PERDIÓ POR LP
if (enemigo.getLp() <= 0) {
    System.out.println("=================================");
    System.out.println("EL GANADOR ES: " + actual.getNombre());
    System.out.println("=================================");
    return null;
}

if (actual.getLp() <= 0) {
    System.out.println("=================================");
    System.out.println("EL GANADOR ES: " + enemigo.getNombre());
    System.out.println("=================================");
    return null;
}

    //  CAMBIO DE TURNO
    return enemigo;
}


}
