public interface Activar {
<<<<<<< HEAD
    void activarEfecto(Jugador jugador);
}
=======

    default void activarEfecto(Jugador jugador) {}

    void activarEfecto(Jugador jugador, Jugador rival);

    String getEfectoDescripcion();
}
>>>>>>> e0f5b056de62eab8203dd8f3e2059a3eb6439333
