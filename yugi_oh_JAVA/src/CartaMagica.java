public class CartaMagica extends Carta implements Activar {
    private String efecto;

    public CartaMagica(String nombre, String efecto) {
        super(nombre);
        this.efecto = efecto;
    }

    public String getEfecto() { return efecto; }

<<<<<<< HEAD
    @Override
    public void activarEfecto(Jugador jugador) {
        switch (efecto) {
            case "robar":
                System.out.println("EFECTO: ROBAS DOS CARTAS ADICIONALES DEL MAZO.");
                jugador.robarCarta();
                jugador.robarCarta();
=======
  
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
>>>>>>> e0f5b056de62eab8203dd8f3e2059a3eb6439333
                break;
            case "recuperar":
                System.out.println("EFECTO: RECUPERAS 1500 LP.");
                jugador.recuperarVida(1500);
                break;
            case "destruir":
<<<<<<< HEAD
                System.out.println("EFECTO: DESTRUYES UN MOSTRUO ENEMIGO DEL CAMPO .");
                // Se maneja desde Juego.java porque necesita al enemigo
                break;
            case "boost":
                System.out.println("EFECTO:UN MOSTRUO TUYO GANA 500 ATK EN ESTE TURNO.");
                // Se maneja desde Juego.java porque necesita al jugador actual
=======
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
>>>>>>> e0f5b056de62eab8203dd8f3e2059a3eb6439333
                break;
            default:
                System.out.println("EFECTO DESCONOCIDO.");
        }
    }

    @Override
<<<<<<< HEAD
    public void mostrarInfo() {
        System.out.println("CARTA MAGICA: " + nombre + " | EFECTO: " + efecto);
    }
}
=======
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
>>>>>>> e0f5b056de62eab8203dd8f3e2059a3eb6439333
