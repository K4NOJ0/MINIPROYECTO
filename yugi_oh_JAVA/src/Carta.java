public abstract class Carta {
    protected String nombre;

    public Carta(String nombre) {
        this.nombre = nombre;
    }

<<<<<<< HEAD
    public String getNombre() {
         return nombre; 
        }
        
    public abstract void mostrarInfo();
}
=======
    public String getNombre() { return nombre; }

    public abstract void mostrarInfo();
    public abstract String getTipo();
    public abstract String getDescripcion();
}
>>>>>>> e0f5b056de62eab8203dd8f3e2059a3eb6439333
