import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner escan = new Scanner(System.in);
        Juego juego = new Juego();
        Jugador j1 = new Jugador("KEMPACHI");
        Jugador j2 = new Jugador("SHINRA");

        Random rnd = new Random();
        Jugador turno;
        
        if (rnd.nextBoolean()) {
          turno = j2;
        } else {
          turno = j1;
          }
        

        
        int opc = 0;
        ArrayList<Carta> mazo = new ArrayList<>();

        // 30 monstruos
        mazo.add(new Monstruo("Dragón Blanco de Ojos Azules", 3000, 2500, 8));
        mazo.add(new Monstruo("Mago Oscuro", 2500, 2100, 7));
        mazo.add(new Monstruo("Calavera Invocada", 2500, 1200, 6));
        mazo.add(new Monstruo("Dragón Negro de Ojos Rojos", 2400, 2000, 7));
        mazo.add(new Monstruo("Jinzo", 2400, 1500, 6));
        mazo.add(new Monstruo("Destructor de Espadas", 2600, 2300, 7));
        mazo.add(new Monstruo("Chica Maga Oscura", 2000, 1700, 6));
        mazo.add(new Monstruo("La Jinn", 1800, 1000, 4));
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

        // 10 cartas mágicas 
        mazo.add(new CartaMagica("Olla de la Codicia", "robar"));
        mazo.add(new CartaMagica("Polvo del Cosmos", "robar"));
        mazo.add(new CartaMagica("Escudo Místico", "recuperar"));
        mazo.add(new CartaMagica("Expansión Astral", "recuperar"));
        mazo.add(new CartaMagica("Renacer del Espíritu", "recuperar"));
        mazo.add(new CartaMagica("Universo Atómico", "destruir"));
        mazo.add(new CartaMagica("Lluvia de Relámpagos", "destruir"));
        mazo.add(new CartaMagica("Trampa de Araña", "destruir"));
        mazo.add(new CartaMagica("Terraformación", "boost"));
        mazo.add(new CartaMagica("Poder Oscuro", "boost"));

        // 10 cartas trampa
        mazo.add(new CartaTrampa("Agujero Trampa", "agujero_trampa", "invocacion"));
        mazo.add(new CartaTrampa("Fuerza Espejo", "fuerza_espejo", "ataque"));
        mazo.add(new CartaTrampa("Negar Ataque", "negar_ataque", "ataque"));
        mazo.add(new CartaTrampa("Cilindro Mágico", "cilindro_magico", "ataque"));
        mazo.add(new CartaTrampa("Tributo Torrencial", "tributo_torrencial", "invocacion"));
        mazo.add(new CartaTrampa("Agujero Trampa Sin Fondo", "agujero_sin_fondo", "invocacion"));
        mazo.add(new CartaTrampa("Evacuación Forzada", "evacuacion_forzada", "ataque"));
        mazo.add(new CartaTrampa("Blindaje Sakuretsu", "blindaje_sakuretsu", "ataque"));
        mazo.add(new CartaTrampa("Protección Waboku", "proteccion_waboku", "ataque"));
        mazo.add(new CartaTrampa("Tornado de Polvo", "tornado_polvo", "ataque"));

        do {
            juego.barajar(mazo);
            
            j1.getMazo().clear();
            j1.getMano().clear();
            j2.getMazo().clear();
            j2.getMano().clear();

            juego.repartirCartas(j1, j2, mazo);
            juego.menu();
            opc = escan.nextInt();

            switch (opc) {
                case 1:
                    juego.iniciarJuego(j1, j2, turno);
                    while (j1.getLp() > 0 && j2.getLp() > 0) {
                        if (turno == j1) {
                            turno = juego.ejecutarTurno(j1, j2, escan);
                        } else {
                            turno = juego.ejecutarTurno(j2, j1, escan);
                        }
                        if (turno == null) break;
                    }
                    break;
                case 2:
                    System.out.println("SALIENDO... ");
                    break;
                default:
                    System.out.println("OPCION NO VALIDA.");
            }
        } while (opc != 2);

        escan.close();
    }
}