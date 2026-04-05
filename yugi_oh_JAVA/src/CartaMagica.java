public class CartaMagica extends Carta implements Activar {
    private String efecto;

    public CartaMagica(String nombre, String efecto) {
        super(nombre);
        this.efecto = efecto;
    }

    public String getEfecto() { return efecto; }

    @Override
    public void activarEfecto(Jugador jugador) {
        switch (efecto) {
            case "robar":
                System.out.println("EFECTO: Robas 2 cartas adicionales del mazo.");
                jugador.robarCarta();
                jugador.robarCarta();
                break;
            case "recuperar":
                System.out.println("EFECTO: Recuperas 1500 LP.");
                jugador.recuperarVida(1500);
                break;
            case "destruir":
                System.out.println("EFECTO: Destruyes un monstruo enemigo del campo.");
                // Se maneja desde Juego.java porque necesita al enemigo
                break;
            case "boost":
                System.out.println("EFECTO: Un monstruo tuyo gana 500 ATK este turno.");
                // Se maneja desde Juego.java porque necesita al jugador actual
                break;
            default:
                System.out.println("Efecto desconocido.");
        }
    }

    @Override
    public void mostrarInfo() {
        System.out.println("CARTA MAGICA: " + nombre + " | EFECTO: " + efecto);
    }
}