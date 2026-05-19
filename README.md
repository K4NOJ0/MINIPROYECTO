# Mini Proyecto: Yu-Gi-Oh! en Java

Este proyecto es una implementación básica del juego de cartas Yu-Gi-Oh! utilizando Java y la biblioteca gráfica **Swing** para la interfaz de usuario. El proyecto sigue el patrón de diseño de software **MVC (Modelo-Vista-Controlador)**, lo que permite separar la lógica del juego de la interfaz visual y su control.

##  Estructura del Proyecto

La estructura del código está dividida dentro de la carpeta `src/`, organizada en diferentes paquetes siguiendo el patrón MVC:

- `src/App.java`: Es el punto de entrada de la aplicación. Contiene el método `main` y se encarga de iniciar el juego y mostrar la ventana del menú inicial (`MenuInicial`).

### Paquete `modelo`
Contiene toda la lógica principal, las reglas del juego y la definición de los objetos que participan en él.
- **`Juego.java` y `Campo.java`**: Gestionan el estado general del juego, los turnos, las fases y el tablero.
- **`Jugador.java`**: Representa a un jugador, controlando sus puntos de vida (LP), su mano, su mazo y su cementerio.
- **`Carta.java`, `Monstruo.java`, `CartaMagica.java`, `CartaTrampa.java`**: Clases que representan los diferentes tipos de cartas del juego, con sus atributos (ataque, defensa, tipo, efectos).
- **`Activar.java`**: Interfaz para la activación de efectos de cartas mágicas y de trampa.

### Paquete `vista`
Se encarga de la Interfaz Gráfica de Usuario (GUI). Define lo que el usuario ve en la pantalla usando componentes de `javax.swing`.
- **`MenuInicial.java`**: La pantalla de inicio del juego, donde se puede comenzar una nueva partida.
- **`TableroJuego.java`**: La interfaz principal donde se lleva a cabo el duelo. Muestra las zonas de monstruos, magias/trampas, puntos de vida, mano y cementerio.
- **`CartaPanel.java`**: Un componente visual personalizado para representar una carta individual en la interfaz.

### Paquete `controlador`
Actúa como puente entre la `vista` y el `modelo`. 
- **`ControladorJuego.java`**: Recibe las interacciones del usuario (clics en botones, selección de cartas) desde la vista, actualiza los datos en el modelo según las reglas del juego y, por último, actualiza la vista para reflejar los cambios realizados.

##  Cómo usar y ejecutar el proyecto

Este proyecto es una aplicación estándar de Java sin herramientas de construcción complejas (como Maven o Gradle), por lo que puedes ejecutarlo fácilmente de las siguientes maneras:

### Opción 1: Usar un IDE (Recomendado)
La forma más sencilla de ejecutar el proyecto es utilizando un Entorno de Desarrollo Integrado (IDE) como **Visual Studio Code**, **IntelliJ IDEA**, **Eclipse** o **NetBeans**.
1. Abre tu IDE.
2. Selecciona la opción para **Abrir un proyecto existente** o **Abrir carpeta**.
3. Selecciona la carpeta `yugi_oh_JAVA`.
4. Busca el archivo `src/App.java`.
5. Ejecuta la clase `App.java` (usualmente haciendo clic derecho sobre el archivo y seleccionando "Run", o usando el botón de "Play" de tu IDE).

### Opción 2: Desde la Terminal (Línea de Comandos)
Si prefieres usar la terminal, asegúrate de tener el **JDK** (Java Development Kit) instalado.
1. Abre una terminal y navega hasta la carpeta `src` del proyecto:
   ```bash
   cd ruta/al/proyecto/yugi_oh_JAVA/src
   ```
2. Compila todos los archivos `.java`:
   ```bash
   javac App.java modelo/*.java vista/*.java controlador/*.java
   ```
3. Ejecuta la clase principal:
   ```bash
   java App
   ```

## Cómo Jugar
- Al ejecutar la aplicación, verás el **Menú Inicial**.
- Inicia el juego para abrir el **Tablero de Juego**.
- Interactúa con el tablero usando el ratón. El controlador se encargará de validar tus acciones, como invocar monstruos, activar magias o atacar al oponente de acuerdo con las reglas implementadas.


John Freddy Hurtado 2559863-3743

Jhon David Ceballos 2559724-3743

Jose Emanuel Cuervo 2559905-3743
