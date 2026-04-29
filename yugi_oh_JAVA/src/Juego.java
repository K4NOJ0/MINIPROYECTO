import java.util.ArrayList;
import java.util.Collections;

public class Juego {

    public void barajar(ArrayList<Carta> mazo) {
        Collections.shuffle(mazo);
    }

    public void repartirCartas(Jugador j1, Jugador j2, ArrayList<Carta> mazo) {
        ArrayList<Carta> mazoBarajado = new ArrayList<>(mazo);
        Collections.shuffle(mazoBarajado);
        for (int i = 0; i < 25; i++) j1.getMazo().add(mazoBarajado.get(i));
        for (int i = 25; i < 50; i++) j2.getMazo().add(mazoBarajado.get(i));
        for (int i = 0; i < 5; i++) j1.robarCarta();
        for (int i = 0; i < 5; i++) j2.robarCarta();
    }

    public String atacarMonstruo(Monstruo atacante, Monstruo defensor,
                                  Jugador atacanteJugador, Jugador defensorJugador) {
        StringBuilder log = new StringBuilder();


        ArrayList<CartaTrampa> trampas = new ArrayList<>(defensorJugador.getCampo().getZonaTrampas());
        for (CartaTrampa t : trampas) {
            if (!t.isActiva()) continue;
            switch (t.getIdEfecto()) {
                case "negar_ataque":
                    defensorJugador.getCampo().getZonaTrampas().remove(t);
                    atacante.setYaAtaco(true);
                    return "🛡 ¡" + defensorJugador.getNombre() + " activó Negar Ataque! El ataque fue cancelado.";
                case "blindaje_sakuretsu":
                    defensorJugador.getCampo().getZonaTrampas().remove(t);
                    atacanteJugador.getCampo().getZonaMonstruos().remove(atacante);
                    atacante.setYaAtaco(true);
                    return "🛡 ¡Blindaje Sakuretsu destruyó a " + atacante.getNombre() + "!";
                case "fuerza_espejo":
                    defensorJugador.getCampo().getZonaTrampas().remove(t);
                    atacanteJugador.getCampo().getZonaMonstruos().clear();
                    return "🛡 ¡Fuerza Espejo destruyó todos los monstruos de " + atacanteJugador.getNombre() + "!";
                case "cilindro_magico":
                    defensorJugador.getCampo().getZonaTrampas().remove(t);
                    atacanteJugador.recibirDano(atacante.getAtk());
                    atacante.setYaAtaco(true);
                    return "🛡 ¡Cilindro Mágico redirigió " + atacante.getAtk() + " de daño a " + atacanteJugador.getNombre() + "!";
            }
        }

        atacante.setYaAtaco(true);

        if (defensor.isModoAtaque()) {
            if (atacante.getAtk() > defensor.getAtk()) {
                int diff = atacante.getAtk() - defensor.getAtk();
                defensorJugador.getCampo().getZonaMonstruos().remove(defensor);
                defensorJugador.recibirDano(diff);
                log.append("⚔ ").append(atacante.getNombre()).append(" destruyó a ").append(defensor.getNombre())
                   .append("! Daño: ").append(diff).append(" LP a ").append(defensorJugador.getNombre());
            } else if (atacante.getAtk() < defensor.getAtk()) {
                int diff = defensor.getAtk() - atacante.getAtk();
                atacanteJugador.getCampo().getZonaMonstruos().remove(atacante);
                atacanteJugador.recibirDano(diff);
                log.append("⚔ ").append(defensor.getNombre()).append(" destruyó a ").append(atacante.getNombre())
                   .append("! Daño: ").append(diff).append(" LP a ").append(atacanteJugador.getNombre());
            } else {
                defensorJugador.getCampo().getZonaMonstruos().remove(defensor);
                atacanteJugador.getCampo().getZonaMonstruos().remove(atacante);
                log.append(" ¡Ambos monstruos se destruyeron mutuamente!");
            }
        } else {
            if (atacante.getAtk() > defensor.getDef()) {
                defensorJugador.getCampo().getZonaMonstruos().remove(defensor);
                log.append("⚔ ").append(atacante.getNombre()).append(" destruyó a ").append(defensor.getNombre())
                   .append(" (en defensa). Sin daño directo.");
            } else if (atacante.getAtk() < defensor.getDef()) {
                int diff = defensor.getDef() - atacante.getAtk();
                atacanteJugador.recibirDano(diff);
                log.append("🛡 ").append(defensor.getNombre()).append(" bloqueó el ataque! Daño: ")
                   .append(diff).append(" LP a ").append(atacanteJugador.getNombre());
            } else {
                defensorJugador.getCampo().getZonaMonstruos().remove(defensor);
                log.append("⚔ ").append(atacante.getNombre()).append(" destruyó a ").append(defensor.getNombre()).append(" (empate en DEF).");
            }
        }
        return log.toString();
    }

    public String atacarDirecto(Monstruo atacante, Jugador defensorJugador) {
        for (CartaTrampa t : new ArrayList<>(defensorJugador.getCampo().getZonaTrampas())) {
            if (t.isActiva() && t.getIdEfecto().equals("negar_ataque")) {
                defensorJugador.getCampo().getZonaTrampas().remove(t);
                atacante.setYaAtaco(true);
                return "🛡 ¡Negar Ataque canceló el ataque directo!";
            }
        }
        atacante.setYaAtaco(true);
        int dano = atacante.getAtk();
        defensorJugador.recibirDano(dano);
        return "⚔ ¡Ataque directo! " + atacante.getNombre() + " inflige " + dano + " LP a " + defensorJugador.getNombre() + "!";
    }
}
