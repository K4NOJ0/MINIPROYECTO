public abstract class Carta {
    protected String nombre;

    public Carta(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() { return nombre; }

    public abstract void mostrarInfo();
    public abstract String getTipo();
    public abstract String getDescripcion();
}
