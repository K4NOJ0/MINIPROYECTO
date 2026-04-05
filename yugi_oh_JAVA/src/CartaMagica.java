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
                System.out.println("EFECTO: ROBAS DOS CARTAS ADICIONALES DEL MAZO.");
                jugador.robarCarta();
                jugador.robarCarta();
                break;
            case "recuperar":
                System.out.println("EFECTO: RECUPERAS 1500 LP.");
                jugador.recuperarVida(1500);
                break;
            case "destruir":
                System.out.println("EFECTO: DESTRUYES UN MOSTRUO ENEMIGO DEL CAMPO .");
                // Se maneja desde Juego.java porque necesita al enemigo
                break;
            case "boost":
                System.out.println("EFECTO:UN MOSTRUO TUYO GANA 500 ATK EN ESTE TURNO.");
                // Se maneja desde Juego.java porque necesita al jugador actual
                break;
            default:
                System.out.println("EFECTO DESCONOCIDO.");
        }
    }

    @Override
    public void mostrarInfo() {
        System.out.println("CARTA MAGICA: " + nombre + " | EFECTO: " + efecto);
    }
}