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
- **`GestorArchivos.java`**: Administra la persistencia del juego. Maneja el archivo de historial `resultados.txt` y almacena los 3 estados de juego guardados en un único archivo estructurado `partidas.txt`.
- **`PartidaSerializer.java`**: Serializa y deserializa el estado de la partida (`EstadoPartida`), preservando cartas y sus estadísticas completas (ATK, DEF, Nivel) de la mano, mazo y campo.

### Paquete `vista`
Se encarga de la Interfaz Gráfica de Usuario (GUI). Define lo que el usuario ve en la pantalla usando componentes de `javax.swing`.
- **`MenuInicial.java`**: La pantalla de inicio del juego, donde se puede comenzar una nueva partida.
- **`TableroJuego.java`**: La interfaz principal donde se lleva a cabo el duelo. Permite guardar y cargar partidas seleccionando entre tres posiciones de guardado ("Guardado 1", "Guardado 2", "Guardado 3").
- **`CartaPanel.java`**: Un componente visual personalizado para representar una carta individual en la interfaz.

### Paquete `controlador`
Actúa como puente entre la `vista` y el `modelo`. 
- **`ControladorJuego.java`**: Recibe las interacciones de la interfaz, gestiona la persistencia a través de slots delegados, y mantiene sincronizados el estado lógico de los jugadores y la representación en pantalla.

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
1. Abre una terminal y navega hasta la carpeta raíz del proyecto (`yugi_oh_JAVA`):
   ```bash
   cd ruta/al/proyecto/yugi_oh_JAVA
   ```
2. Compila todos los archivos `.java` (por ejemplo, compilando al directorio de clases correspondiente):
   ```bash
   javac -d . src/App.java src/modelo/*.java src/vista/*.java src/controlador/*.java
   ```
3. Ejecuta la clase principal:
   ```bash
   java App
   ```

## Guardado y Carga de Partidas
El juego incluye un sistema integrado para persistir el estado actual del duelo:
- **Guardado Unificado**: Se ofrecen 3 perfiles denominados **"Guardado 1"**, **"Guardado 2"** y **"Guardado 3"**. Todos ellos se registran de manera organizada en un único archivo local llamado `partidas.txt`.
- **Integridad de Estadísticas**: Al cargar una partida, los monstruos y cartas del campo, mano y mazo mantienen intactas sus estadísticas originales de ataque (ATK), defensa (DEF), nivel, modos de juego y efectos, evitando que se reinicien en cero.

---

## Justificación de Estructuras de Datos

El proyecto incorpora de forma justificada las siguientes estructuras de datos, evitando el uso arbitrario de `ArrayList`:

- **`Stack<Carta>` — Mazo del jugador** (`Jugador.java`): El mazo funciona bajo el principio LIFO (último en entrar, primero en salir), exactamente como ocurre en el juego real: la última carta mezclada es la primera en robarse. Se usa `push()` al construir el mazo y `pop()` al robar una carta.

- **`Stack<Carta>` — Historial de jugadas del campo** (`Campo.java`): Las cartas jugadas en el campo se apilan en orden cronológico. La carta jugada más recientemente queda en la cima, lo que permite rastrear el historial de jugadas de forma natural con semántica LIFO.

- **`Stack<EstadoPartida>` — Historial de estados guardados** (`GestorMementos.java`): Cada vez que se guarda la partida se empuja un nuevo `EstadoPartida` al stack. El estado más reciente siempre queda en la cima, facilitando la restauración con `pop()`. Esto también habilita la funcionalidad de deshacer.

- **`LinkedList<Carta>` — Mano del jugador** (`Jugador.java`): La mano requiere inserción al final al robar cartas y eliminación en cualquier posición al jugar una carta. `LinkedList` realiza ambas operaciones eficientemente sin necesidad de reorganizar índices, a diferencia de `ArrayList`.

- **`LinkedList<Monstruo>` y `LinkedList<CartaTrampa>` — Zonas del campo** (`Campo.java`): Las zonas de monstruos y trampas del campo necesitan agregar y eliminar cartas en cualquier posición (cuando un monstruo es destruido o una trampa se activa). `LinkedList` es ideal para estas operaciones frecuentes de inserción y eliminación.

- **`Queue<String>` — Cola de eventos del duelo** (`Juego.java`): Los eventos del duelo (ataques, efectos activados, daño recibido) deben procesarse en el orden exacto en que ocurren, siguiendo el principio FIFO (primero en entrar, primero en salir). Se usa `offer()` para encolar un evento y `poll()` para procesarlo.

- **`HashMap<String, Carta>` — Registro de cartas** (`Juego.java`): Permite buscar cualquier carta por su nombre en tiempo O(1). Esto es fundamental para el sistema de Reflection, donde se carga una clase dinámicamente desde un archivo de texto usando el nombre de la carta como clave.

- **`HashMap<String, Integer>` — Victorias por jugador** (`AnalizadorEstadisticas.java`): Asocia el nombre de cada jugador con su conteo de victorias históricas leídas desde `resultados.txt`. La búsqueda por nombre en O(1) hace eficiente el análisis de estadísticas.

John Freddy Hurtado 2559863-3743

Jhon David Ceballos 2559724-3743

Jose Emanuel Cuervo 2559905-3743
