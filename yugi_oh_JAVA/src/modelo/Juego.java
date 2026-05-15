package modelo;
import java.util.ArrayList;
import java.util.Collections;

public class Juego {

    private Jugador jugadorActual;
    private int turnoActual;

    public ArrayList<Carta> construirMazo() {

        ArrayList<Carta> mazo = new ArrayList<>();

        mazo.add(new Monstruo(
            "Dragon Blanco",
            3000,
            2500,
            8
        ));

        mazo.add(new Monstruo(
            "Kuriboh",
            300,
            200,
            1
        ));

        mazo.add(new CartaMagica(
            "Olla de la Codicia",
            "robar"
        ));

        mazo.add(new CartaMagica(
            "Curacion",
            "recuperar"
        ));

        mazo.add(new CartaTrampa(
            "Negar Ataque",
            "negar_ataque",
            "cuando_atacan"
        ));

        mazo.add(new CartaTrampa(
            "Fuerza Espejo",
            "fuerza_espejo",
            "cuando_atacan"
        ));

        return mazo;
    }

    public void barajar(ArrayList<Carta> mazo) {

        Collections.shuffle(mazo);
    }

    public void repartirCartas(
        Jugador j1,
        Jugador j2,
        ArrayList<Carta> mazo
    ) {

        ArrayList<Carta> mazoBarajado =
            new ArrayList<>(mazo);

        Collections.shuffle(mazoBarajado);

        for (int i = 0; i < 25; i++) {

            j1.getMazo().add(
                mazoBarajado.get(i)
            );
        }

        for (int i = 25; i < 50; i++) {

            j2.getMazo().add(
                mazoBarajado.get(i)
            );
        }

        for (int i = 0; i < 5; i++) {

            j1.robarCarta();
            j2.robarCarta();
        }
    }

    public void iniciarJuego(
        Jugador j1,
        Jugador j2
    ) {

        jugadorActual = j1;

        turnoActual = 1;

        ArrayList<Carta> mazo =
            construirMazo();

        repartirCartas(j1, j2, mazo);
    }

    public String robarCarta(
        Jugador jugador
    ) {

        return jugador.robarCarta();
    }

    public String invocarMonstruo(
        Jugador jugador,
        Monstruo monstruo
    ) {

        boolean invocado =
            jugador.getCampo()
                    .invocarMonstruo(monstruo);

        if (invocado) {

            jugador.getMano()
                    .remove(monstruo);

            return monstruo.getNombre() +
                   " fue invocado";
        }

        return "No hay espacio";
    }

    public String colocarTrampa(
        Jugador jugador,
        CartaTrampa trampa
    ) {

        boolean colocada =
            jugador.getCampo()
                    .colocarTrampa(trampa);

        if (colocada) {

            jugador.getMano()
                    .remove(trampa);

            return "Trampa colocada";
        }

        return "No hay espacio";
    }

    public void pasarTurno(
        Jugador j1,
        Jugador j2
    ) {

        if (jugadorActual == j1) {

            jugadorActual = j2;

        } else {

            jugadorActual = j1;
        }

        turnoActual++;
    }

    public boolean chequearFinDePartida(
        Jugador j1,
        Jugador j2
    ) {

        return j1.getLp() <= 0 ||
               j2.getLp() <= 0;
    }

    public String atacarMonstruo(
        Monstruo atacante,
        Monstruo defensor,
        Jugador atacanteJugador,
        Jugador defensorJugador
    ) {

        StringBuilder log =
            new StringBuilder();

        ArrayList<CartaTrampa> trampas =
            new ArrayList<>(
                defensorJugador
                    .getCampo()
                    .getZonaTrampas()
            );

        for (CartaTrampa t : trampas) {

            if (!t.isActiva()) {
                continue;
            }

            switch (t.getIdEfecto()) {

                case "negar_ataque":

                    defensorJugador
                        .getCampo()
                        .getZonaTrampas()
                        .remove(t);

                    atacante.setYaAtaco(true);

                    return "ATAQUE CANCELADO";

                case "blindaje_sakuretsu":

                    defensorJugador
                        .getCampo()
                        .getZonaTrampas()
                        .remove(t);

                    atacanteJugador
                        .getCampo()
                        .getZonaMonstruos()
                        .remove(atacante);

                    atacante.setYaAtaco(true);

                    return atacante.getNombre() +
                           " destruido";

                case "fuerza_espejo":

                    defensorJugador
                        .getCampo()
                        .getZonaTrampas()
                        .remove(t);

                    atacanteJugador
                        .getCampo()
                        .getZonaMonstruos()
                        .clear();

                    return "Todos los monstruos destruidos";

                case "cilindro_magico":

                    defensorJugador
                        .getCampo()
                        .getZonaTrampas()
                        .remove(t);

                    atacanteJugador
                        .recibirDano(
                            atacante.getAtk()
                        );

                    atacante.setYaAtaco(true);

                    return "Daño reflejado";
            }
        }

        atacante.setYaAtaco(true);

        if (defensor.isModoAtaque()) {

            if (
                atacante.getAtk() >
                defensor.getAtk()
            ) {

                int diff =
                    atacante.getAtk() -
                    defensor.getAtk();

                defensorJugador
                    .getCampo()
                    .getZonaMonstruos()
                    .remove(defensor);

                defensorJugador
                    .recibirDano(diff);

                log.append(
                    atacante.getNombre()
                );

                log.append(
                    " destruyó a "
                );

                log.append(
                    defensor.getNombre()
                );

            } else if (
                atacante.getAtk() <
                defensor.getAtk()
            ) {

                int diff =
                    defensor.getAtk() -
                    atacante.getAtk();

                atacanteJugador
                    .getCampo()
                    .getZonaMonstruos()
                    .remove(atacante);

                atacanteJugador
                    .recibirDano(diff);

                log.append(
                    defensor.getNombre()
                );

                log.append(
                    " destruyó a "
                );

                log.append(
                    atacante.getNombre()
                );

            } else {

                defensorJugador
                    .getCampo()
                    .getZonaMonstruos()
                    .remove(defensor);

                atacanteJugador
                    .getCampo()
                    .getZonaMonstruos()
                    .remove(atacante);

                log.append(
                    "Ambos monstruos destruidos"
                );
            }

        } else {

            if (
                atacante.getAtk() >
                defensor.getDef()
            ) {

                defensorJugador
                    .getCampo()
                    .getZonaMonstruos()
                    .remove(defensor);

                log.append(
                    defensor.getNombre()
                );

                log.append(
                    " destruido en defensa"
                );

            } else if (
                atacante.getAtk() <
                defensor.getDef()
            ) {

                int diff =
                    defensor.getDef() -
                    atacante.getAtk();

                atacanteJugador
                    .recibirDano(diff);

                log.append(
                    "Ataque bloqueado"
                );

            } else {

                defensorJugador
                    .getCampo()
                    .getZonaMonstruos()
                    .remove(defensor);

                log.append(
                    "Empate"
                );
            }
        }

        return log.toString();
    }

    public String atacarDirecto(
        Monstruo atacante,
        Jugador defensorJugador
    ) {

        for (
            CartaTrampa t :
            new ArrayList<>(
                defensorJugador
                    .getCampo()
                    .getZonaTrampas()
            )
        ) {

            if (
                t.isActiva() &&
                t.getIdEfecto()
                 .equals("negar_ataque")
            ) {

                defensorJugador
                    .getCampo()
                    .getZonaTrampas()
                    .remove(t);

                atacante.setYaAtaco(true);

                return "ATAQUE CANCELADO";
            }
        }

        atacante.setYaAtaco(true);

        int dano = atacante.getAtk();

        defensorJugador.recibirDano(dano);

        return atacante.getNombre() +
               " hizo ataque directo";
    }
}