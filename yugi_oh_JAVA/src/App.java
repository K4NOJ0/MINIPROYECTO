import java.util.ArrayList;
import java.util.Random;

public class App {
    public static void main(String[] args) {
        Jugador j1 = new Jugador("yugi");
        Jugador j2 = new Jugador("kaiba");

        ArrayList<Carta> mazo = new ArrayList<>();

       
        mazo.add(new Monstruo("dragon blanco de ojos azules", 3000, 2500, 8));
        mazo.add(new Monstruo("mago oscuro", 2500, 2100, 7));
        mazo.add(new Monstruo("montana de 4 cabezas", 2600, 2200, 8));
        mazo.add(new Monstruo("salamandra", 1200, 1500, 5));
        mazo.add(new Monstruo("cabeza de cuernos", 1500, 1200, 4));
        mazo.add(new Monstruo("toro de guerra", 1700, 1600, 5));
        mazo.add(new Monstruo("dragon blanco alterno", 2500, 2100, 7));
        mazo.add(new Monstruo("caballero oscuro", 1900, 1700, 6));
        mazo.add(new Monstruo("sombra esparcida", 1600, 1800, 4));
        mazo.add(new Monstruo("guardia supremo", 2700, 2100, 7));
        mazo.add(new Monstruo("kolossus", 2300, 2000, 6));
        mazo.add(new Monstruo("fuego fatuo", 1000, 1200, 3));
        mazo.add(new Monstruo("titania", 2600, 1800, 7));
        mazo.add(new Monstruo("tortuga fortificada", 1200, 2000, 4));
        mazo.add(new Monstruo("jinzo", 2400, 1500, 6));
        mazo.add(new Monstruo("caballero mago", 2500, 2100, 7));
        mazo.add(new Monstruo("dracoserpiente", 2300, 1800, 6));
        mazo.add(new Monstruo("gallina del terror", 100, 500, 1));
        mazo.add(new Monstruo("rana gigante", 800, 900, 2));
        mazo.add(new Monstruo("mago de la espada", 1800, 1500, 5));
        mazo.add(new Monstruo("anciano de la sabana", 500, 2000, 4));
        mazo.add(new Monstruo("lagarto oscuro", 1600, 1200, 4));
        mazo.add(new Monstruo("caballo de batalla", 1200, 1500, 4));
        mazo.add(new Monstruo("gobernador del mar", 1000, 2100, 4));
        mazo.add(new Monstruo("araza", 1600, 1300, 4));
        mazo.add(new Monstruo("dragon de metal mago", 2100, 1700, 5));
        mazo.add(new Monstruo("fantasma marino", 1700, 1500, 5));
        mazo.add(new Monstruo("soldado valiente", 1200, 1000, 4));
        mazo.add(new Monstruo("garou del desierto", 1800, 1200, 4));
        mazo.add(new Monstruo("perro guardián", 1900, 1600, 5));
        mazo.add(new Monstruo("morroid", 2450, 1200, 6));

        // 10 cartas magicas
        mazo.add(new CartaMagica("huevo del dragon", "robar carta"));
        mazo.add(new CartaMagica("polvo magico", "robar carta"));
        mazo.add(new CartaMagica("flecha del destino", "robar carta"));
        mazo.add(new CartaMagica("escudo mistico", "recuperar lp"));
        mazo.add(new CartaMagica("curacion triple", "recuperar lp"));
        mazo.add(new CartaMagica("renacer del espiritu", "recuperar lp"));
        mazo.add(new CartaMagica("energia de honor", "recuperar lp"));
        mazo.add(new CartaMagica("lluvia de truenos", "destruir monstruo"));
        mazo.add(new CartaMagica("trampa de araña", "destruir monstruo"));
        mazo.add(new CartaMagica("giro del caos", "destruir monstruo"));

        
        Random rnd = new Random();
        for (int i = mazo.size() - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            Carta tmp = mazo.get(i);
            mazo.set(i, mazo.get(j));
            mazo.set(j, tmp);
        }

   
        for (int i = 0; i < mazo.size(); i++) {
            if (i % 2 == 0) j1.agregarCarta(mazo.get(i));
            else j2.agregarCarta(mazo.get(i));
        }

     
        for (int i = 0; i < 5; i++) {
            j1.robarCarta();
            j2.robarCarta();
        }

        j1.mostrarMano();
        System.out.println("-----");
        j2.mostrarMano();
    }
}
