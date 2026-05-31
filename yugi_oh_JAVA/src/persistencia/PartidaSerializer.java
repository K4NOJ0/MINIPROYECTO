package persistencia;

import patrones.EstadoPartida;
import patrones.EstadoPartida.EstadoJugador;

public class PartidaSerializer {

    public static String serializar(EstadoPartida estado) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("[PARTIDA]\n");
        sb.append("TurnoActual=").append(estado.getNombreTurnoActual()).append("\n\n");
        
        sb.append(serializarJugador(estado.getJ1(), 1));
        sb.append(serializarJugador(estado.getJ2(), 2));
        
        return sb.toString();
    }

    private static String serializarJugador(EstadoJugador j, int numero) {
        StringBuilder sb = new StringBuilder();
        sb.append("[JUGADOR").append(numero).append("]\n");
        sb.append("Nombre=").append(j.getNombre()).append("\n");
        sb.append("LP=").append(j.getLp()).append("\n");
        
        sb.append("Mano=").append(String.join(",", j.getMano())).append("\n");
        sb.append("Mazo=").append(String.join(",", j.getMazo())).append("\n");
        sb.append("CampoMonstruos=").append(String.join(",", j.getCampoMonstruos())).append("\n");
        sb.append("CampoTrampas=").append(String.join(",", j.getCampoTrampas())).append("\n\n");
        
        return sb.toString();
    }
}
