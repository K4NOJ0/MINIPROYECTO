public class CartaTrampa extends Carta implements Activar {
    private String efecto;
    private String condicion;
    private boolean activa;

    public CartaTrampa(String nombre, String efecto, String condicion) {
        super(nombre);
        this.efecto = efecto;
        this.condicion = condicion;
        this.activa = false;
    }


    public String getEfecto() { return efecto; }


    public String getIdEfecto() { return efecto; }

    public String getCondicion() { return condicion; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean v) { this.activa = v; }


    @Override
    public void activarEfecto(Jugador jugador) {
        activarEfecto(jugador, null);
    }


    @Override
    public void activarEfecto(Jugador jugador, Jugador rival) {
        System.out.println("TRAMPA ACTIVADA: " + nombre);
        if (rival == null) return;
        switch (efecto) {
            case "agujero_trampa":
                if (!rival.getCampo().getZonaMonstruos().isEmpty()) {
                    Monstruo ultimo = rival.getCampo().getZonaMonstruos()
                        .get(rival.getCampo().getZonaMonstruos().size() - 1);
                    if (ultimo.getNivel() >= 4) {
                        rival.getCampo().getZonaMonstruos().remove(ultimo);
                        System.out.println(ultimo.getNombre() + " DESTRUIDO POR " + nombre);
                    }
                }
                break;
            case "fuerza_espejo":
                rival.getCampo().getZonaMonstruos().clear();
                System.out.println("TODOS LOS MONSTRUOS ATACANTES DESTRUIDOS POR " + nombre);
                break;
            case "negar_ataque":
                System.out.println("ATAQUE CANCELADO POR " + nombre);
                break;
            case "cilindro_magico":
                System.out.println("DAÑO REDIRIGIDO POR " + nombre);
                break;
            case "tributo_torrencial":
                rival.getCampo().getZonaMonstruos().clear();
                jugador.getCampo().getZonaMonstruos().clear();
                System.out.println("TODOS LOS MONSTRUOS DESTRUIDOS POR " + nombre);
                break;
            case "agujero_sin_fondo":
                if (!rival.getCampo().getZonaMonstruos().isEmpty()) {
                    Monstruo ultimo = rival.getCampo().getZonaMonstruos()
                        .get(rival.getCampo().getZonaMonstruos().size() - 1);
                    if (ultimo.getAtk() >= 1500) {
                        rival.getCampo().getZonaMonstruos().remove(ultimo);
                        System.out.println(ultimo.getNombre() + " DESTRUIDO POR " + nombre);
                    }
                }
                break;
            case "evacuacion_forzada":
                if (!rival.getCampo().getZonaMonstruos().isEmpty()) {
                    Monstruo m = rival.getCampo().getZonaMonstruos().remove(0);
                    rival.getMazo().add(0, m);
                    System.out.println(m.getNombre() + " DEVUELTO AL MAZO POR " + nombre);
                }
                break;
            case "blindaje_sakuretsu":
                if (!rival.getCampo().getZonaMonstruos().isEmpty()) {
                    Monstruo m = rival.getCampo().getZonaMonstruos().remove(0);
                    System.out.println(m.getNombre() + " DESTRUIDO POR " + nombre);
                }
                break;
            case "proteccion_waboku":
                jugador.setWabokuActivo(true);
                System.out.println("WABOKU ACTIVO: " + jugador.getNombre() + " no recibe daño este turno");
                break;
            case "tornado_polvo":
                if (!rival.getCampo().getZonaTrampas().isEmpty()) {
                    CartaTrampa destruida = rival.getCampo().getZonaTrampas().remove(0);
                    System.out.println(destruida.getNombre() + " DESTRUIDA POR " + nombre);
                }
                break;
        }
    }

    @Override
    public String getEfectoDescripcion() {
        switch (efecto) {
            case "agujero_trampa":     return "Destruye monstruo rival nivel 4+";
            case "fuerza_espejo":      return "Destruye todos los monstruos atacantes";
            case "negar_ataque":       return "Niega un ataque rival";
            case "cilindro_magico":    return "Redirige daño al rival";
            case "tributo_torrencial": return "Destruye todos los monstruos en campo";
            case "agujero_sin_fondo":  return "Destruye monstruo rival con ATK 1500+";
            case "evacuacion_forzada": return "Devuelve monstruo rival al mazo";
            case "blindaje_sakuretsu": return "Destruye un monstruo atacante";
            case "proteccion_waboku":  return "Anula el daño de combate este turno";
            case "tornado_polvo":      return "Destruye una trampa del rival";
            default:                   return efecto;
        }
    }

    @Override
    public void mostrarInfo() {
        System.out.println("CARTA TRAMPA: " + nombre + " | EFECTO: " + efecto + " | CONDICION: " + condicion);
    }

    @Override
    public String getTipo() { return "Trampa"; }

    @Override
    public String getDescripcion() { return "Efecto: " + getEfectoDescripcion() + " (Condición: " + condicion + ")"; }
}
