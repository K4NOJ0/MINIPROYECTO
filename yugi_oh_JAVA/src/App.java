

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class App {
    
    public static void main(String[] args) {
      
        Juego juego=new Juego();
        Jugador j1 = new Jugador("CUERVO");
        Jugador j2 = new Jugador("CEBALLOS");
        Random turn = new Random();
        Scanner escan=new Scanner(System.in);
        int opc=0;
        

        //turno al azar
        Jugador turno;
        
        if (turn.nextBoolean()) {
          turno = j1;
        } else {
          turno = j2;
          }
    
        
        ArrayList<Carta> mazo = new ArrayList<>();
       //30 monstruos
        mazo.add(new Monstruo("Dragón Blanco de Ojos Azules", 3000, 2500, 8));
        mazo.add(new Monstruo("Mago Oscuro", 2500, 2100, 7));
        mazo.add(new Monstruo("Calavera Invocada", 2500, 1200, 6));
        mazo.add(new Monstruo("Dragón Negro de Ojos Rojos", 2400, 2000, 7));
        mazo.add(new Monstruo("Jinzo", 2400, 1500, 6));
        mazo.add(new Monstruo("Destructor de Espadas", 2600, 2300, 7));
        mazo.add(new Monstruo("Chica Maga Oscura", 2000, 1700, 6));
        mazo.add(new Monstruo("La Jinn, Genio Místico de la Lámpara", 1800, 1000, 4));
        mazo.add(new Monstruo("Buey de Batalla", 1700, 1000, 4));
        mazo.add(new Monstruo("Neo el Espadachín Mágico", 1700, 1000, 4));
        mazo.add(new Monstruo("Guardián Celta", 1400, 1200, 4));
        mazo.add(new Monstruo("Elfa Géminis", 1900, 900, 4));
        mazo.add(new Monstruo("Soldado Archidemonio", 1900, 1500, 4));
        mazo.add(new Monstruo("Vorcerader", 1900, 1200, 4));
        mazo.add(new Monstruo("Gigante Soldado de Piedra", 1300, 2000, 3));
        mazo.add(new Monstruo("Elfa Mística", 800, 2000, 4));
        mazo.add(new Monstruo("Aqua Madoor", 1200, 2000, 4));
        mazo.add(new Monstruo("Muro de Ilusión", 1000, 1850, 4));
        mazo.add(new Monstruo("Segador del Espíritu", 300, 200, 3));
        mazo.add(new Monstruo("Kuriboh", 300, 200, 1));
        mazo.add(new Monstruo("Sangan", 1000, 600, 3));
        mazo.add(new Monstruo("Bruja del Bosque Negro", 1100, 1200, 4));
        mazo.add(new Monstruo("Insecto Comehombres", 450, 600, 2));
        mazo.add(new Monstruo("Dama Arpía", 1300, 1400, 4));
        mazo.add(new Monstruo("Hermanas Arpía", 1950, 2100, 6));
        mazo.add(new Monstruo("Gearfried el Caballero de Hierro", 1800, 1600, 4));
        mazo.add(new Monstruo("Fuerza Exiliada", 1000, 1000, 4));
        mazo.add(new Monstruo("Hada de Inyección Lily", 400, 1500, 3));
        mazo.add(new Monstruo("Mago del Tiempo", 500, 400, 2));
        mazo.add(new Monstruo("Dragón Bebé", 1200, 700, 3));
       
        // 10 cartas magicas
        mazo.add(new CartaMagica("universo atomico", "destruir mostruo")); 
        mazo.add(new CartaMagica("polvo de cosmos ", "robar carta")); 
        mazo.add(new CartaMagica("flecha del destino", "robar carta")); 
        mazo.add(new CartaMagica("escudo mistico", "recuperar lp")); 
        mazo.add(new CartaMagica("expansion astral ", "recuperar lp")); 
        mazo.add(new CartaMagica("renacer del espiritu", "recuperar lp"));
        mazo.add(new CartaMagica("llama celestial", "recuperar lp")); 
        mazo.add(new CartaMagica("lluvia de relampagos ", "destruir monstruo")); 
        mazo.add(new CartaMagica("trampa de araña", "destruir monstruo"));
        mazo.add(new CartaMagica("retumbar del caos ", "destruir monstruo"));
        
        


do{
 juego.barajar(mazo);
 juego.repartirCartas(j1, j2, mazo);
 juego.Menu();
 opc=escan.nextInt();

  switch (opc) {
    case 1:{
      
         
      juego.iniciarJuego(j1, j2, turno);

     while (j1.getLp() > 0 && j2.getLp() > 0) {

    juego.mostrarInfoJugadores(j1, j2);

    if (turno == j1) {
        turno = juego.ejecutarTurno(j1, j2, escan);
    } else {
        turno = juego.ejecutarTurno(j2, j1, escan);
    }

    if (turno == null) break;
}
       
    } break;
    case 2:
        System.out.println("SALIENDO.........");
        break;
    default:
      System.out.println("!!ERROR OPCION NO VALIDA!!");

  }


}while(opc!=2);

 escan.close(); 
    }
}

