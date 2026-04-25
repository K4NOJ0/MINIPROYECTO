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
        System.out.println("4. COLOCAR TRAMPA");
        System.out.println("5. PASAR TURNO");
    }

    public void mostrarInfoJugadores(Jugador j1, Jugador j2) {
        System.out.println("\n============== ESTADO DEL CAMPO ==============");
        mostrarEstadoJugador(j1);
        System.out.println("----------------------------------------------");
        mostrarEstadoJugador(j2);
        System.out.println("==============================================");
    }

    private void mostrarEstadoJugador(Jugador j) {
        System.out.println("JUGADOR: " + j.getNombre() + " | LP: " + j.getLp() + " | CARTAS EN MANO: " + j.getMano().size() + " | Cartas en mazo: " + j.getMazo().size());
        if (j.getCampo().getZonaMonstruos().isEmpty()) {
            System.out.println("  CAMPO: (VACIO)");
        } else {
            System.out.println("  CAMPO:");
            for (int i = 0; i < j.getCampo().getZonaMonstruos().size(); i++) {
                Monstruo m = j.getCampo().getZonaMonstruos().get(i);
               String modo;

                    if (m.isModoAtaque()) {
                        modo = "ATK";
                    } else {
                        modo = "DEF";
                    }
                System.out.println("  " + i + ". " + m.getNombre() + " | ATK: " + m.getAtk() + " | DEF: " + m.getDef() + " | " + modo);
            }
        }
        if (j.getCampo().getZonaTrampas().isEmpty()) {
            System.out.println("  TRAMPAS: (VACIO)");
        } else {
            System.out.println("  TRAMPAS:");
            for (int i = 0; i < j.getCampo().getZonaTrampas().size(); i++) {
                System.out.println("  " + i + ". " + j.getCampo().getZonaTrampas().get(i).getNombre());
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
            System.out.println("¡" + actual.getNombre() + "NO TIENE CARTAS EN EL MAZO PIERDE EL DUELO");
            System.out.println(" GANADOR: " + enemigo.getNombre());
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
                        System.out.println(" NO SE PUEDE ATACAR EN EL PRIMER TURNO.");
                        break;
                    }
                    if (yaAtaco) {
                        System.out.println("YA ATACASTE EN ESTE TURNO.");
                        break;
                    }
                    if (actual.getCampo().getZonaMonstruos().isEmpty()) {
                        System.out.println(" NO TIENES MONSTRUOS EN EL CAMPO.");
                        break;
                    }

                    // Elegir monstruo atacante
                    System.out.println("ELIGE EL MOSTRUO QUE ATACA:");
                    for (int i = 0; i < actual.getCampo().getZonaMonstruos().size(); i++) {
                        Monstruo m = actual.getCampo().getZonaMonstruos().get(i);
                        System.out.println(i + ". " + m.getNombre() + " ATK: " + m.getAtk());
                    }
                    int idxAtacante = escan.nextInt();
                    if (idxAtacante < 0 || idxAtacante >= actual.getCampo().getZonaMonstruos().size()) {
                        System.out.println("SELECCION INVALIDA.");
                        break;
                    }
                    Monstruo atacante = actual.getCampo().getZonaMonstruos().get(idxAtacante);

                    // Ataque directo o a monstruo
                    if (enemigo.getCampo().getZonaMonstruos().isEmpty()) {
                        boolean cancelarAtaque = verificarTrampasAtaque(enemigo, atacante, null, escan);
                        if (!cancelarAtaque) {
                            actual.atacarDirecto(atacante, enemigo);
                        }
                    } else {
                        System.out.println("ELIGE EL MOSTRUO ENEMIGO A ATACAR:");
                        for (int i = 0; i < enemigo.getCampo().getZonaMonstruos().size(); i++) {
                            Monstruo m = enemigo.getCampo().getZonaMonstruos().get(i);
                             
                            String modo;
                            if (m.isModoAtaque()) {
                                modo = "ATK";
                            } else {
                                modo = "DEF";
                            }
                            System.out.println(i + ". " + m.getNombre() + " | ATK: " + m.getAtk() + " | DEF: " + m.getDef() + " | " + modo);
                        }
                        int idxDefensor = escan.nextInt();
                        if (idxDefensor < 0 || idxDefensor >= enemigo.getCampo().getZonaMonstruos().size()) {
                            System.out.println(" SELECCION INVALIDA.");
                            break;
                        }
                        Monstruo defensor = enemigo.getCampo().getZonaMonstruos().get(idxDefensor);
                        boolean cancelarAtaque = verificarTrampasAtaque(enemigo, atacante, defensor, escan);
                        if (!cancelarAtaque) {
                            actual.atacar(atacante, defensor, enemigo);
                        }
                    }
                    yaAtaco = true;
                    break;

                case 2: // INVOCAR MONSTRUO
                    if (yaJugoCarta) {
                        System.out.println(" YA JUGASTE UNA CARTA ESTE TURNO.");
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
                        System.out.println("NO TIENES MOSTRUOS EN MANO.");
                        break;
                    }

                    System.out.println("ELIGE EL NUMERO DE MOSTRUO:");
                    int idxMonstruo = escan.nextInt();
                    if (idxMonstruo < 0 || idxMonstruo >= actual.getMano().size() || !(actual.getMano().get(idxMonstruo) instanceof Monstruo)) {
                        System.out.println("SELECCION INVALIDA.");
                        break;
                    }

                    // Elegir modo: ataque o defensa
                    System.out.println("¿EN QUE MODO INVOCAR?");
                    System.out.println("1. MODO ATAQUE");
                    System.out.println("2. MODO DEFENSA");
                    int modoEleccion = escan.nextInt();

                    Monstruo nuevo = (Monstruo) actual.getMano().remove(idxMonstruo);
                    nuevo.setModoAtaque(modoEleccion != 2); // si no elige 2, va a ataque

                    if (nuevo.getNivel() > 4) {

                    if (actual.getCampo().getZonaMonstruos().isEmpty()) {

                        System.out.println("NO PUEDES INVOCAR ESTA ESTA CARTA PORQUE NO TIENES MOSTRUOS EN EL CAMPO PARA SACRIFICIOS");
                        actual.getMano().add(nuevo);
                        break;
                    }

                    actual.sacrificarMonstruo();
                    }

                    if (actual.getCampo().invocarMonstruo(nuevo)) {

                    String modo;

                    if (nuevo.isModoAtaque()) {
                        modo = "ATAQUE";
                    } else {
                        modo = "DEFENSA";
                    }

                    System.out.println("INVOCASTE: " + nuevo.getNombre() + " EN MODO " + modo);

                    yaJugoCarta = true;

                   } else {

                    System.out.println("CAMPO LLENO.");
                    actual.getMano().add(nuevo);
                }

                verificarTrampasInvocacion(enemigo, nuevo, escan);
                break;

                case 3: // USAR CARTA MAGICA
                    if (yaJugoCarta) {
                        System.out.println("YA JUGASTE UNA CARTA ESTE TURNO .");
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
                        System.out.println("NO TIENES CARTAS EN MANO.");
                        break;
                    }

                    System.out.println("ELIGE EL NUMERO DE LA CARTA MAGICA:");
                    int idxMagica = escan.nextInt();
                    if (idxMagica < 0 || idxMagica >= actual.getMano().size() || !(actual.getMano().get(idxMagica) instanceof CartaMagica)) {
                        System.out.println("SELECCION INVALIDA.");
                        break;
                    }

                    CartaMagica magia = (CartaMagica) actual.getMano().remove(idxMagica);

                    // Efectos que necesitan al enemigo o seleccion extra
                    if (magia.getEfecto().equals("DESTRUIR")) {
                        ArrayList<Monstruo> zonaEnemiga = enemigo.getCampo().getZonaMonstruos();
                        if (zonaEnemiga.isEmpty()) {
                            System.out.println("EL ENEMIGO NO TIENE MONSTRUOS EFECTO DESPERDICIADO.");
                        } else {
                            System.out.println("ELIGE QUE MOSTRUO ENEMIGO DESTRUIR:");
                            for (int i = 0; i < zonaEnemiga.size(); i++) {
                                System.out.println(i + ". " + zonaEnemiga.get(i).getNombre());
                            }
                            int idxDestruir = escan.nextInt();
                            if (idxDestruir >= 0 && idxDestruir < zonaEnemiga.size()) {
                                Monstruo destruido = zonaEnemiga.remove(idxDestruir);
                                System.out.println( destruido.getNombre() + " FUE DESTRUIDO POR EFECTO DE CARTA.");
                            } else {
                                System.out.println("ELECCION IVALIDA EFECTO DESPERDICIADO.");
                            }
                        }
                    } else if (magia.getEfecto().equals("boost")) {
                        ArrayList<Monstruo> zonaPropia = actual.getCampo().getZonaMonstruos();
                        if (zonaPropia.isEmpty()) {
                            System.out.println("NO TIENES MOSTRUOS EN CAMPO, EFECTO DESPERDICIADO.");
                        } else {
                            System.out.println("ELIGE QUE MOSTRUO POTENCIAR(+500 ATK ESTE TURNO):");
                            for (int i = 0; i < zonaPropia.size(); i++) {
                                System.out.println(i + ". " + zonaPropia.get(i).getNombre() + " ATK: " + zonaPropia.get(i).getAtk());
                            }
                            int idxBoost = escan.nextInt();
                            if (idxBoost >= 0 && idxBoost < zonaPropia.size()) {
                                Monstruo potenciado = zonaPropia.get(idxBoost);
                                potenciado.setAtk(potenciado.getAtk() + 500);
                                System.out.println( potenciado.getNombre() + "AHORA TIENE " + potenciado.getAtk() + " ATK.");
                            } else {
                                System.out.println("SELECCION IVALIDA EFECTO DESPERDICIADO.");
                            }
                        }
                    } else {
                        magia.activarEfecto(actual);
                    }

                    System.out.println("ACTIVASTE : " + magia.getNombre());
                    yaJugoCarta = true;
                    break;

                case 4: // COLOCAR TRAMPA
                    if (yaJugoCarta) {
                        System.out.println("YA JUGASTE UNA CARTA ESTE TURNO.");
                        break;
                    }

                    boolean hayTrampa = false;
                    for (int i = 0; i < actual.getMano().size(); i++) {
                        if (actual.getMano().get(i) instanceof CartaTrampa) {
                            System.out.println(i + ". " + actual.getMano().get(i).getNombre());
                            hayTrampa = true;
                        }
                    }
                    if (!hayTrampa) {
                        System.out.println("NO TIENES TRAMPAS EN MANO.");
                        break;
                    }

                    System.out.println("ELIGE EL NUMERO DE LA TRAMPA:");
                    int idxTrampa = escan.nextInt();
                    if (idxTrampa < 0 || idxTrampa >= actual.getMano().size() || !(actual.getMano().get(idxTrampa) instanceof CartaTrampa)) {
                        System.out.println("SELECCION INVALIDA.");
                        break;
                    }

                    CartaTrampa trampa = (CartaTrampa) actual.getMano().remove(idxTrampa);

                    if (actual.getCampo().colocarTrampa(trampa)) {
                        System.out.println("COLOCASTE: " + trampa.getNombre() + " EN EL CAMPO.");
                        yaJugoCarta = true;
                    } else {
                        System.out.println("ZONA DE TRAMPAS LLENA.");
                        actual.getMano().add(trampa);
                    }
                    break;

                case 5:
                    System.out.println("FIN DEL TURNO DE  " + actual.getNombre() + ".");
                    break;

                default:
                    System.out.println("OPCION INVALIDA");
            }

        } while (opcion != 5);

        primerTurno = false;

        // Verificar ganador por LP
        if (enemigo.getLp() <= 0) {
            System.out.println("\n ¡EL GANADOR ES: " + actual.getNombre() );
            return null;
        }
        if (actual.getLp() <= 0) {
            System.out.println("\n ¡EL GANADOR ES: " + enemigo.getNombre() );
            return null;
        }

        return enemigo;
    }

    private void verificarTrampasInvocacion(Jugador enemigo, Monstruo invocado, Scanner escan) {
        ArrayList<CartaTrampa> trampasDisponibles = new ArrayList<>();
        for (CartaTrampa trampa : enemigo.getCampo().getZonaTrampas()) {
            if (trampa.getCondicion().equals("invocacion")) {
                trampasDisponibles.add(trampa);
            }
        }
        if (!trampasDisponibles.isEmpty()) {
            System.out.println(enemigo.getNombre() + ", ¿QUIERES ACTIVAR UNA TRAMPA POR INVOCACION?");
            for (int i = 0; i < trampasDisponibles.size(); i++) {
                System.out.println(i + ". " + trampasDisponibles.get(i).getNombre());
            }
            System.out.println(trampasDisponibles.size() + ". NO ACTIVAR");
            int opcion = escan.nextInt();
            if (opcion >= 0 && opcion < trampasDisponibles.size()) {
                CartaTrampa trampaElegida = trampasDisponibles.get(opcion);
                activarTrampa(trampaElegida, enemigo, null, invocado, null);
                enemigo.getCampo().getZonaTrampas().remove(trampaElegida);
            }
        }
    }

    private boolean verificarTrampasAtaque(Jugador enemigo, Monstruo atacante, Monstruo defensor, Scanner escan) {
        ArrayList<CartaTrampa> trampasDisponibles = new ArrayList<>();
        for (CartaTrampa trampa : enemigo.getCampo().getZonaTrampas()) {
            if (trampa.getCondicion().equals("ataque")) {
                trampasDisponibles.add(trampa);
            }
        }
        if (!trampasDisponibles.isEmpty()) {
            System.out.println(enemigo.getNombre() + ", ¿QUIERES ACTIVAR UNA TRAMPA POR ATAQUE?");
            for (int i = 0; i < trampasDisponibles.size(); i++) {
                System.out.println(i + ". " + trampasDisponibles.get(i).getNombre());
            }
            System.out.println(trampasDisponibles.size() + ". NO ACTIVAR");
            int opcion = escan.nextInt();
            if (opcion >= 0 && opcion < trampasDisponibles.size()) {
                CartaTrampa trampaElegida = trampasDisponibles.get(opcion);
                boolean cancelar = activarTrampa(trampaElegida, enemigo, null, atacante, defensor);
                enemigo.getCampo().getZonaTrampas().remove(trampaElegida);
                return cancelar;
            }
        }
        return false;
    }

    private boolean activarTrampa(CartaTrampa trampa, Jugador propietario, Jugador oponente, Monstruo atacante, Monstruo defensor) {
        trampa.activarEfecto(propietario);
        switch (trampa.getEfecto()) {
            case "agujero_trampa":
                if (atacante != null && atacante.getAtk() > 1000) {
                    oponente.getCampo().getZonaMonstruos().remove(atacante);
                    System.out.println(atacante.getNombre() + " DESTRUIDO POR " + trampa.getNombre() + ".");
                }
                break;
            case "fuerza_espejo":
                if (atacante != null) {
                    oponente.getCampo().getZonaMonstruos().clear();
                    System.out.println("TODOS LOS MONSTRUOS ATACANTES DESTRUIDOS POR " + trampa.getNombre() + ".");
                }
                break;
            case "negar_ataque":
                System.out.println("ATAQUE CANCELADO POR " + trampa.getNombre() + ".");
                return true;
            case "cilindro_magico":
                if (atacante != null) {
                    oponente.restarLP(atacante.getAtk());
                    System.out.println(trampa.getNombre() + ": " + oponente.getNombre() + " PIERDE " + atacante.getAtk() + " LP.");
                }
                break;
            case "tributo_torrencial":
                propietario.getCampo().getZonaMonstruos().clear();
                oponente.getCampo().getZonaMonstruos().clear();
                System.out.println(trampa.getNombre() + ": TODOS LOS MONSTRUOS DESTRUIDOS.");
                break;
            case "agujero_sin_fondo":
                if (atacante != null && atacante.getAtk() >= 1500) {
                    oponente.getCampo().getZonaMonstruos().remove(atacante);
                    System.out.println(atacante.getNombre() + " DESTRUIDO POR " + trampa.getNombre() + ".");
                }
                break;
            case "evacuacion_forzada":
                if (!oponente.getCampo().getZonaMonstruos().isEmpty()) {
                    Monstruo destruido = oponente.getCampo().getZonaMonstruos().remove(0);
                    System.out.println(destruido.getNombre() + " DEVUELTO AL MAZO POR " + trampa.getNombre() + ".");
                }
                break;
            case "blindaje_sakuretsu":
                if (atacante != null && atacante.getAtk() <= 1500) {
                    oponente.getCampo().getZonaMonstruos().remove(atacante);
                    System.out.println(atacante.getNombre() + " DESTRUIDO POR " + trampa.getNombre() + ".");
                }
                break;
            case "proteccion_waboku":
                for (Monstruo m : oponente.getCampo().getZonaMonstruos()) {
                    m.setModoAtaque(false);
                }
                System.out.println(trampa.getNombre() + ": TODOS LOS MONSTRUOS CAMBIAN A DEFENSA.");
                break;
            case "tornado_polvo":
                if (!oponente.getCampo().getZonaTrampas().isEmpty()) {
                    CartaTrampa destruida = oponente.getCampo().getZonaTrampas().remove(0);
                    System.out.println(destruida.getNombre() + " DESTRUIDA POR " + trampa.getNombre() + ".");
                }
                break;
        }
        return false;
    }
}