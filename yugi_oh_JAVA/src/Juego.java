import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Juego {
    private boolean primerTurno = true;

    public void menu() {
        System.out.println("\n====== BIENVENIDO A YU-GI-OH! ======");
        System.out.println("1. INICIAR PARTIDA");
        System.out.println("2. SALIR");
        System.out.println("=====================================");
    }

    public void menuJuego(Jugador turno) {
        System.out.println("\n=== TURNO DE [" + turno.getNombre() + "] ===");
        System.out.println("1. ATACAR");
        System.out.println("2. INVOCAR MONSTRUO");
        System.out.println("3. USAR CARTA MAGICA");
        System.out.println("4. PASAR TURNO");
    }

    public void mostrarInfoJugadores(Jugador j1, Jugador j2) {
        System.out.println("\n============== ESTADO DEL CAMPO ==============");
        mostrarEstadoJugador(j1);
        System.out.println("----------------------------------------------");
        mostrarEstadoJugador(j2);
        System.out.println("==============================================");
    }

    private void mostrarEstadoJugador(Jugador j) {
        System.out.println("JUGADOR: " + j.getNombre() + " | LP: " + j.getLp() + " | Cartas en mano: " + j.getMano().size() + " | Cartas en mazo: " + j.getMazo().size());
        if (j.getCampo().getZonaMonstruos().isEmpty()) {
            System.out.println("  Campo: (vacío)");
        } else {
            System.out.println("  Campo:");
            for (int i = 0; i < j.getCampo().getZonaMonstruos().size(); i++) {
                Monstruo m = j.getCampo().getZonaMonstruos().get(i);
                String modo = m.isModoAtaque() ? "ATK" : "DEF";
                System.out.println("  " + i + ". " + m.getNombre() + " | ATK: " + m.getAtk() + " | DEF: " + m.getDef() + " | " + modo);
            }
        }
    }

    public void barajar(ArrayList<Carta> mazo) {
        Random rnd = new Random();
        for (int i = mazo.size() - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            Carta tmp = mazo.get(i);
            mazo.set(i, mazo.get(j));
            mazo.set(j, tmp);
        }
    }

    public void repartirCartas(Jugador j1, Jugador j2, ArrayList<Carta> mazo) {
        for (int i = 0; i < mazo.size(); i++) {
            if (i % 2 == 0) j1.agregarCarta(mazo.get(i));
            else j2.agregarCarta(mazo.get(i));
        }
    }

    public void iniciarJuego(Jugador j1, Jugador j2, Jugador turno) {
        System.out.println("\n--------------------------------------------------");
        System.out.println("DUELISTAS: [" + j1.getNombre() + "] VS [" + j2.getNombre() + "]");
        System.out.println("INICIA: [" + turno.getNombre() + "]");
        System.out.println("--------------------------------------------------");
        for (int i = 0; i < 5; i++) {
            j1.robarCarta();
            j2.robarCarta();
        }
    }

    // Retorna el siguiente jugador en turno, o null si alguien ganó
    public Jugador ejecutarTurno(Jugador actual, Jugador enemigo, Scanner escan) {
        System.out.println("\n========== TURNO DE: " + actual.getNombre() + " ==========");

        // RF2: Robar carta al inicio del turno
        if (actual.getMazo().isEmpty()) {
            System.out.println("¡" + actual.getNombre() + " no tiene cartas en el mazo! ¡Pierde el duelo!");
            System.out.println("🏆 GANADOR: " + enemigo.getNombre());
            return null;
        }
        actual.robarCarta();

        boolean yaJugoCarta = false;
        boolean yaAtaco = false;
        int opcion;

        do {
            mostrarInfoJugadores(actual, enemigo);
            actual.mostrarMano();
            menuJuego(actual);

            opcion = escan.nextInt();

            switch (opcion) {

                case 1: // ATACAR
                    if (primerTurno) {
                        System.out.println("❌ No se puede atacar en el primer turno.");
                        break;
                    }
                    if (yaAtaco) {
                        System.out.println("❌ Ya atacaste este turno.");
                        break;
                    }
                    if (actual.getCampo().getZonaMonstruos().isEmpty()) {
                        System.out.println("❌ No tienes monstruos en campo.");
                        break;
                    }

                    // Elegir monstruo atacante
                    System.out.println("Elige tu monstruo atacante:");
                    for (int i = 0; i < actual.getCampo().getZonaMonstruos().size(); i++) {
                        Monstruo m = actual.getCampo().getZonaMonstruos().get(i);
                        System.out.println(i + ". " + m.getNombre() + " ATK: " + m.getAtk());
                    }
                    int idxAtacante = escan.nextInt();
                    if (idxAtacante < 0 || idxAtacante >= actual.getCampo().getZonaMonstruos().size()) {
                        System.out.println("❌ Selección inválida.");
                        break;
                    }
                    Monstruo atacante = actual.getCampo().getZonaMonstruos().get(idxAtacante);

                    // Ataque directo o a monstruo
                    if (enemigo.getCampo().getZonaMonstruos().isEmpty()) {
                        actual.atacarDirecto(atacante, enemigo);
                    } else {
                        System.out.println("Elige el monstruo enemigo a atacar:");
                        for (int i = 0; i < enemigo.getCampo().getZonaMonstruos().size(); i++) {
                            Monstruo m = enemigo.getCampo().getZonaMonstruos().get(i);
                            String modo = m.isModoAtaque() ? "ATK" : "DEF";
                            System.out.println(i + ". " + m.getNombre() + " | ATK: " + m.getAtk() + " | DEF: " + m.getDef() + " | " + modo);
                        }
                        int idxDefensor = escan.nextInt();
                        if (idxDefensor < 0 || idxDefensor >= enemigo.getCampo().getZonaMonstruos().size()) {
                            System.out.println("❌ Selección inválida.");
                            break;
                        }
                        Monstruo defensor = enemigo.getCampo().getZonaMonstruos().get(idxDefensor);
                        actual.atacar(atacante, defensor, enemigo);
                    }
                    yaAtaco = true;
                    break;

                case 2: // INVOCAR MONSTRUO
                    if (yaJugoCarta) {
                        System.out.println("❌ Ya jugaste una carta este turno.");
                        break;
                    }

                    // Mostrar solo monstruos en mano
                    boolean hayMonstruo = false;
                    for (int i = 0; i < actual.getMano().size(); i++) {
                        if (actual.getMano().get(i) instanceof Monstruo) {
                            System.out.println(i + ". " + actual.getMano().get(i).getNombre());
                            hayMonstruo = true;
                        }
                    }
                    if (!hayMonstruo) {
                        System.out.println("❌ No tienes monstruos en mano.");
                        break;
                    }

                    System.out.println("Elige el índice del monstruo:");
                    int idxMonstruo = escan.nextInt();
                    if (idxMonstruo < 0 || idxMonstruo >= actual.getMano().size() || !(actual.getMano().get(idxMonstruo) instanceof Monstruo)) {
                        System.out.println("❌ Selección inválida.");
                        break;
                    }

                    // Elegir modo: ataque o defensa
                    System.out.println("¿En qué modo invocar?");
                    System.out.println("1. MODO ATAQUE");
                    System.out.println("2. MODO DEFENSA");
                    int modoEleccion = escan.nextInt();

                    Monstruo nuevo = (Monstruo) actual.getMano().remove(idxMonstruo);
                    nuevo.setModoAtaque(modoEleccion != 2); // si no elige 2, va a ataque

                    if (actual.getCampo().invocarMonstruo(nuevo)) {
                        System.out.println("✅ Invocaste: " + nuevo.getNombre() + " en modo " + (nuevo.isModoAtaque() ? "ATAQUE" : "DEFENSA"));
                        yaJugoCarta = true;
                    } else {
                        System.out.println("❌ Campo lleno.");
                        actual.getMano().add(nuevo);
                    }
                    break;

                case 3: // USAR CARTA MAGICA
                    if (yaJugoCarta) {
                        System.out.println("❌ Ya jugaste una carta este turno.");
                        break;
                    }

                    boolean hayMagica = false;
                    for (int i = 0; i < actual.getMano().size(); i++) {
                        if (actual.getMano().get(i) instanceof CartaMagica) {
                            System.out.println(i + ". " + actual.getMano().get(i).getNombre());
                            hayMagica = true;
                        }
                    }
                    if (!hayMagica) {
                        System.out.println("❌ No tienes cartas mágicas en mano.");
                        break;
                    }

                    System.out.println("Elige el índice de la carta mágica:");
                    int idxMagica = escan.nextInt();
                    if (idxMagica < 0 || idxMagica >= actual.getMano().size() || !(actual.getMano().get(idxMagica) instanceof CartaMagica)) {
                        System.out.println("❌ Selección inválida.");
                        break;
                    }

                    CartaMagica magia = (CartaMagica) actual.getMano().remove(idxMagica);

                    // Efectos que necesitan al enemigo o selección extra
                    if (magia.getEfecto().equals("destruir")) {
                        ArrayList<Monstruo> zonaEnemiga = enemigo.getCampo().getZonaMonstruos();
                        if (zonaEnemiga.isEmpty()) {
                            System.out.println("El enemigo no tiene monstruos. Efecto desperdiciado.");
                        } else {
                            System.out.println("Elige qué monstruo enemigo destruir:");
                            for (int i = 0; i < zonaEnemiga.size(); i++) {
                                System.out.println(i + ". " + zonaEnemiga.get(i).getNombre());
                            }
                            int idxDestruir = escan.nextInt();
                            if (idxDestruir >= 0 && idxDestruir < zonaEnemiga.size()) {
                                Monstruo destruido = zonaEnemiga.remove(idxDestruir);
                                System.out.println("💥 " + destruido.getNombre() + " fue destruido por efecto de carta.");
                            } else {
                                System.out.println("❌ Selección inválida. Efecto desperdiciado.");
                            }
                        }
                    } else if (magia.getEfecto().equals("boost")) {
                        ArrayList<Monstruo> zonaPropia = actual.getCampo().getZonaMonstruos();
                        if (zonaPropia.isEmpty()) {
                            System.out.println("No tienes monstruos en campo. Efecto desperdiciado.");
                        } else {
                            System.out.println("Elige qué monstruo potenciar (+500 ATK este turno):");
                            for (int i = 0; i < zonaPropia.size(); i++) {
                                System.out.println(i + ". " + zonaPropia.get(i).getNombre() + " ATK: " + zonaPropia.get(i).getAtk());
                            }
                            int idxBoost = escan.nextInt();
                            if (idxBoost >= 0 && idxBoost < zonaPropia.size()) {
                                Monstruo potenciado = zonaPropia.get(idxBoost);
                                potenciado.setAtk(potenciado.getAtk() + 500);
                                System.out.println("⚡ " + potenciado.getNombre() + " ahora tiene " + potenciado.getAtk() + " ATK.");
                            } else {
                                System.out.println("❌ Selección inválida. Efecto desperdiciado.");
                            }
                        }
                    } else {
                        magia.activarEfecto(actual);
                    }

                    System.out.println("✅ Activaste: " + magia.getNombre());
                    yaJugoCarta = true;
                    break;

                case 4:
                    System.out.println("Fin del turno de " + actual.getNombre() + ".");
                    break;

                default:
                    System.out.println("❌ Opción inválida.");
            }

        } while (opcion != 4);

        primerTurno = false;

        // Verificar ganador por LP
        if (enemigo.getLp() <= 0) {
            System.out.println("\n🏆 ¡EL GANADOR ES: " + actual.getNombre() + "! ¡Que el corazón de las cartas te guíe siempre!");
            return null;
        }
        if (actual.getLp() <= 0) {
            System.out.println("\n🏆 ¡EL GANADOR ES: " + enemigo.getNombre() + "! ¡Que el corazón de las cartas te guíe siempre!");
            return null;
        }

        return enemigo;
    }
}