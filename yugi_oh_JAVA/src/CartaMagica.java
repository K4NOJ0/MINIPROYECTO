public class CartaMagica extends Carta implements Activar {
    private String efecto;

    public CartaMagica(String nombre, String efecto) {
        super(nombre);
        this.efecto = efecto;
    }

    @Override
    public void activarEfecto(Jugador jugador) {
        switch (efecto) {
            case "robar carta":
            case "robar":
                System.out.println("efecto: robar carta");
                jugador.robarCarta();
                break;
            case "recuperar lp":
            case "recuperar":
                System.out.println("efecto: recuperar lp");
                jugador.recuperarVida(500);
                break;
            case "destruir monstruo":
            case "destruir":
                System.out.println("efecto: destruir monstruo");
                jugador.destruirMonstruo();
                break;
            default:
                System.out.println("efecto desconocido en carta magica");
        }
    }

    @Override
    public void mostrarInfo() {
        System.out.println("carta magica: " + nombre + " | efecto: " + efecto);
    }
}