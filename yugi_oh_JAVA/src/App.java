import java.util.ArrayList;
import java.util.Random;

public class App {
    public static void main(String[] args) {

        ArrayList<String> todas = new ArrayList<>();

        
        for (int i = 1; i <= 30; i++) {
            todas.add("Monstruo " + i);
        }

        
        for (int i = 1; i <= 10; i++) {
            todas.add("Magia " + i);
        }

        
        Random rand = new Random();
        for (int i = todas.size() - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            String temp = todas.get(i);
            todas.set(i, todas.get(j));
            todas.set(j, temp);
        }

        
        ArrayList<String> j1 = new ArrayList<>();
        ArrayList<String> j2 = new ArrayList<>();

        
        for (int i = 0; i < 20; i++) {
            j1.add(todas.get(i));
            j2.add(todas.get(i + 20));
        }

        
        System.out.println("Jugador 1: " + j1);
        System.out.println("----------------------");
        System.out.println("Jugador 2: " + j2);
    }
}
