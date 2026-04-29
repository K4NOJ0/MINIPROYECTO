public interface Activar {

    default void activarEfecto(Jugador jugador) {}

    void activarEfecto(Jugador jugador, Jugador rival);

    String getEfectoDescripcion();
}
