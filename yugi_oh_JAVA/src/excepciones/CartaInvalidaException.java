package excepciones;


public class CartaInvalidaException extends Exception {
    public CartaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
