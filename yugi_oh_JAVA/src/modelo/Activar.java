package modelo;
public interface Activar {

    default String activarEfecto(Jugador jugador) {
        return "efecto activado ";
    }

    String  activarEfecto(Jugador jugador, Jugador rival);

    String getEfectoDescripcion();
}
