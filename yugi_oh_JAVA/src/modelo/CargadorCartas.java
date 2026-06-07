package modelo;

import modelo.Carta;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class CargadorCartas {

    public static List<Carta> cargarCartas(String archivo) {

        List<Carta> cartas = new ArrayList<>();

        try (BufferedReader br =
                     new BufferedReader(new FileReader(archivo))) {

            String linea;

            while ((linea = br.readLine()) != null) {

                String[] datos = linea.split(";");

                String nombreClase = datos[0];

                Class<?> clase =
                        Class.forName(nombreClase);

                Constructor<?> constructor;

                Object carta;

                if (nombreClase.endsWith("CartaMagica")) {

                    constructor =
                            clase.getDeclaredConstructor(
                                    String.class,
                                    String.class
                            );

                    carta = constructor.newInstance(
                            datos[1],
                            datos[2]
                    );

                } else {

                    constructor =
                            clase.getDeclaredConstructor(
                                    String.class,
                                    String.class,
                                    String.class
                            );

                    carta = constructor.newInstance(
                            datos[1],
                            datos[2],
                            datos[3]
                    );
                }

                cartas.add((Carta) carta);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cartas;
    }
}