package patrones;

import modelo.Carta;
import modelo.Monstruo;
import modelo.CartaMagica;
import modelo.CartaTrampa;
import reflection.InstanciadorDinamico;

public class CartaFactory {

    public static Carta crearCarta(String tipo, String nombre, String extra1, String extra2) {
    
        switch (tipo.toUpperCase()) {
            case "MAGIA":
                return new CartaMagica(nombre, extra1);
            case "TRAMPA":
                return new CartaTrampa(nombre, extra1, extra2);
            default:
                return null;
        }
    }

    public static Monstruo crearMonstruo(String nombre, int atk, int def, int nivel) {
        return new Monstruo(nombre, atk, def, nivel);
    }
    
    public static Carta crearCartaDinamica(String className, Object... args) {
        return InstanciadorDinamico.instanciarCarta(className, args);
    }
}
