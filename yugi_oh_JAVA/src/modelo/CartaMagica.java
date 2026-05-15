package modelo;
public class CartaMagica extends Carta implements Activar {
    private String efecto;

    public CartaMagica(String nombre, String efecto) {
        super(nombre);
        this.efecto = efecto;
    }

    public String getEfecto() { return efecto; }

  
    @Override
  public String activarEfecto(Jugador jugador) {
    return activarEfecto(jugador, null);
}

    @Override
public String activarEfecto(Jugador jugador, Jugador rival) {

    switch (efecto) {

        case "robar":

            jugador.robarCarta();
            jugador.robarCarta();

            return "EFECTO: ROBAS DOS CARTAS";

        case "recuperar":

            jugador.recuperarVida(1500);

            return "EFECTO: RECUPERAS 1500 LP";

        case "destruir":

            if (rival != null &&
                !rival.getCampo().getZonaMonstruos().isEmpty()) {

                rival.getCampo()
                     .getZonaMonstruos()
                     .remove(0);

                return "EFECTO: MONSTRUO ENEMIGO DESTRUIDO";
            }

            return "NO HAY MONSTRUOS";

        default:
            return "EFECTO DESCONOCIDO";
    }
}

    @Override
    public String getEfectoDescripcion() {
        switch (efecto) {
            case "robar":    return "Roba 2 cartas del mazo";
            case "recuperar": return "Recupera 1500 LP";
            case "destruir": return "Destruye un monstruo rival";
            case "boost":    return "Un monstruo gana 500 ATK";
            default:         return efecto;
        }
    }

    @Override
public String mostrarInfo() {

    return "CARTA MAGICA: " + nombre +
           " | EFECTO: " + efecto;
}

    @Override
    public String getTipo() { return "Magia"; }

    @Override
    public String getDescripcion() { return "Efecto: " + getEfectoDescripcion(); }
}
