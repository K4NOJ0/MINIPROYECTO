package reflection;

import modelo.Carta;
import java.lang.reflect.Constructor;

public class InstanciadorDinamico {
    
    public static Carta instanciarCarta(String className, Object... args) {
        try {
            Class<?> clazz = Class.forName(className);
            Class<?>[] parameterTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Integer) {
                    parameterTypes[i] = int.class;
                } else {
                    parameterTypes[i] = args[i].getClass();
                }
            }
            Constructor<?> constructor = clazz.getConstructor(parameterTypes);
            return (Carta) constructor.newInstance(args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
