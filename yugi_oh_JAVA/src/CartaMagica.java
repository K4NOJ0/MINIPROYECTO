public class CartaMagica extends Carta implements Activar {
    private String efecto;

    public CartaMagica(String nombre, String efecto) {
        super(nombre);
        this.efecto = efecto;
    }

    public String getEfecto() { return efecto; }

  
    @Override
    public void activarEfecto(Jugador jugador) {
        activarEfecto(jugador, null);
    }


    @Override
    public void activarEfecto(Jugador jugador, Jugador rival) {
        switch (efecto) {
            case "robar":
                System.out.println("EFECTO: ROBAS DOS CARTAS ADICIONALES DEL MAZO.");
                if (!jugador.getMazo().isEmpty()) jugador.robarCarta();
                if (!jugador.getMazo().isEmpty()) jugador.robarCarta();
                break;
            case "recuperar":
                System.out.println("EFECTO: RECUPERAS 1500 LP.");
                jugador.recuperarVida(1500);
                break;
            case "destruir":
                System.out.println("EFECTO: DESTRUYES UN MONSTRUO ENEMIGO DEL CAMPO.");
                if (rival != null && !rival.getCampo().getZonaMonstruos().isEmpty()) {
                    rival.getCampo().getZonaMonstruos().remove(0);
                }
                break;
            case "boost":
                System.out.println("EFECTO: UN MONSTRUO TUYO GANA 500 ATK.");
                if (!jugador.getCampo().getZonaMonstruos().isEmpty()) {
                    Monstruo m = jugador.getCampo().getZonaMonstruos().get(0);
                    m.setAtk(m.getAtk() + 500);
                }
                break;
            default:
                System.out.println("EFECTO DESCONOCIDO.");
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
    public void mostrarInfo() {
        System.out.println("CARTA MAGICA: " + nombre + " | EFECTO: " + efecto);
    }

    @Override
    public String getTipo() { return "Magia"; }

    @Override
    public String getDescripcion() { return "Efecto: " + getEfectoDescripcion(); }
}
