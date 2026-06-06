package modelo;

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
    public String activarEfecto(Jugador jugador) {
        return activarEfecto(jugador, null);
    }

    @Override
    public String activarEfecto(Jugador jugador, Jugador rival) {

        if (rival == null) {
            return "NO HAY RIVAL";
        }

        switch (efecto) {

            case "agujero_trampa":
                if (!rival.getCampo().getZonaMonstruosLinked().isEmpty()) {
                    Monstruo ultimo = rival.getCampo()
                            .getZonaMonstruosLinked()
                            .getLast();
                    if (ultimo.getNivel() >= 4) {
                        rival.getCampo().getZonaMonstruosLinked().remove(ultimo);
                        return ultimo.getNombre() + " DESTRUIDO POR " + nombre;
                    }
                }
                return "NO SE PUDO ACTIVAR";

            case "fuerza_espejo":
                rival.getCampo().getZonaMonstruosLinked().clear();
                return "TODOS LOS MONSTRUOS DESTRUIDOS";

            case "negar_ataque":
                return "ATAQUE CANCELADO";

            case "cilindro_magico":
                return "DAÑO REDIRIGIDO";

            case "tributo_torrencial":
                rival.getCampo().getZonaMonstruosLinked().clear();
                jugador.getCampo().getZonaMonstruosLinked().clear();
                return "TODOS LOS MONSTRUOS DESTRUIDOS";

            case "agujero_sin_fondo":
                if (!rival.getCampo().getZonaMonstruosLinked().isEmpty()) {
                    Monstruo ultimo = rival.getCampo()
                            .getZonaMonstruosLinked()
                            .getLast();
                    if (ultimo.getAtk() >= 1500) {
                        rival.getCampo().getZonaMonstruosLinked().remove(ultimo);
                        return ultimo.getNombre() + " DESTRUIDO";
                    }
                }
                return "NO SE PUDO ACTIVAR";

            case "evacuacion_forzada":
                if (!rival.getCampo().getZonaMonstruosLinked().isEmpty()) {
                    Monstruo m = rival.getCampo().getZonaMonstruosLinked().removeFirst();
                    rival.getMazoStack().push(m);
                    return m.getNombre() + " DEVUELTO AL MAZO";
                }
                return "NO HAY MONSTRUOS";

            case "blindaje_sakuretsu":
                if (!rival.getCampo().getZonaMonstruosLinked().isEmpty()) {
                    Monstruo m = rival.getCampo().getZonaMonstruosLinked().removeFirst();
                    return m.getNombre() + " DESTRUIDO";
                }
                return "NO HAY MONSTRUOS";

            case "proteccion_waboku":
                jugador.setWabokuActivo(true);
                return "WABOKU ACTIVADO";

            case "tornado_polvo":
                if (!rival.getCampo().getZonaTrampasLinked().isEmpty()) {
                    CartaTrampa destruida = rival.getCampo().getZonaTrampasLinked().removeFirst();
                    return destruida.getNombre() + " DESTRUIDA";
                }
                return "NO HAY TRAMPAS";
        }

        return "EFECTO DESCONOCIDO";
    }

    @Override
    public String getEfectoDescripcion() {
        switch (efecto) {
            case "agujero_trampa":      return "Destruye monstruo rival nivel 4+";
            case "fuerza_espejo":       return "Destruye todos los monstruos atacantes";
            case "negar_ataque":        return "Niega un ataque rival";
            case "cilindro_magico":     return "Redirige daño al rival";
            case "tributo_torrencial":  return "Destruye todos los monstruos en campo";
            case "agujero_sin_fondo":   return "Destruye monstruo rival con ATK 1500+";
            case "evacuacion_forzada":  return "Devuelve monstruo rival al mazo";
            case "blindaje_sakuretsu":  return "Destruye un monstruo atacante";
            case "proteccion_waboku":   return "Anula el daño de combate este turno";
            case "tornado_polvo":       return "Destruye una trampa del rival";
            default:                    return efecto;
        }
    }

    @Override
    public String mostrarInfo() {
        return "CARTA TRAMPA: " + nombre +
               " | EFECTO: " + efecto +
               " | CONDICION: " + condicion;
    }

    @Override
    public String getTipo() { return "Trampa"; }

    @Override
    public String getDescripcion() {
        return "Efecto: " + getEfectoDescripcion() + " (Condición: " + condicion + ")";
    }
}
