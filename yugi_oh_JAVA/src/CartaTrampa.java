public class CartaTrampa extends Carta implements Activar {
    private String efecto;
    private String condicion;

    public CartaTrampa(String nombre, String efecto, String condicion) {
        super(nombre);
        this.efecto = efecto;
        this.condicion = condicion;
    }

    public String getEfecto() { return efecto; }
    public String getCondicion() { return condicion; }

    @Override
    public void activarEfecto(Jugador jugador) {
        
        System.out.println("TRAMPA ACTIVADA: " + nombre);
    }

    @Override
    public void mostrarInfo() {
        System.out.println("CARTA TRAMPA: " + nombre + " | EFECTO: " + efecto + " | CONDICION: " + condicion);
    }
}