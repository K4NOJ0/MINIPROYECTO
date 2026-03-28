import java.util.ArrayList;
import java.util.Random;
public class Juego {
    

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


public Jugador iniciarJuego(Jugador j1, Jugador j2, Jugador turno){

    System.out.println("----------------------------------------------------------");
    System.out.println("LOS DOS DUELISTAS SON :|["+j1.getNombre()+"] Y ["+j2.getNombre()+"]|");
    System.out.println("EL JUGADOR QUE INICIA LA PARTIDA ES ["+turno.getNombre()+"]");
    System.out.println("----------------------------------------------------------");

 //primeras 5 cartas para cada uno
        
 for(int i = 0; i <10; i++){
        System.out.println("TURNO DEL JUGADOR " + turno.getNombre());
        turno.robarCarta();
        turno= Turnos(j1, j2, turno);
        }
     return turno;
 
}


}
