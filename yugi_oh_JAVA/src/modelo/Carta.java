package modelo;
public abstract class Carta {

    protected String nombre;

    public Carta(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public abstract String mostrarInfo();

    public abstract String getTipo();

    public abstract String getDescripcion();
}