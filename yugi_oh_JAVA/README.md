# Yu-Gi-Oh! Simulador de Duelo

Un simulador de juego de cartas Yu-Gi-Oh implementado en Java, que permite a dos jugadores participar en duelos de estrategia con monstruos, cartas mágicas y mecánicas de combate realistas.

##  Descripción del Proyecto

Este proyecto implementa un juego de cartas tipo Yu-Gi-Oh con las siguientes características principales:

### Componentes Principales:
- **Jugadores**: Cada jugador comienza con 8000 puntos de vida (LP)
- **Cartas**: Sistema de dos tipos de cartas
  - **Monstruos**: 30 cartas con atributos de ataque (ATK), defensa (DEF) y nivel
  - **Cartas Mágicas**: 10 cartas con efectos especiales (robar, recuperar, destruir, boost)
- **Campo de Juego**: Zona de monstruos con límite de 5 invocaciones por jugador
- **Modos de Combate**: Ataque directo y combate monstruo a monstruo

### Mecánicas de Juego:
- **Robo de cartas**: Al inicio del turno
- **Invocación de monstruos**: En modo ataque (ATK) o defensa (DEF)
- **Sistema de ataque**: 
  - Ataque directo (si el enemigo no tiene monstruos)
  - Ataque a monstruo enemigo en modo ataque o defensa
- **Cartas Mágicas**:
  - *Robar*: Permite robar 2 cartas adicionales
  - *Recuperar*: Restaura 1500 LP
  - *Destruir*: Destruye un monstruo enemigo
  - *Boost*: Aumenta ATK de un monstruo 500 puntos
- **Condiciones de Victoria**: 
  - Reducir LP del enemigo a 0
  - Oponente sin cartas en el mazo

## Estructura del Proyecto

```
yugi_oh_JAVA/
├── src/
│   ├── App.java              # Punto de entrada del programa
│   ├── Juego.java            # Lógica principal del juego
│   ├── Jugador.java          # Clase del jugador
│   ├── Carta.java            # Clase abstracta de cartas
│   ├── Monstruo.java         # Clase de monstruos
│   ├── CartaMagica.java      # Clase de cartas mágicas
│   ├── Campo.java            # Zona de monstruos del jugador
│   └── Activar.java          # Interfaz para efectos
├── README.md                 # Este archivo
└── LICENSE                   # Licencia del proyecto
```

##  Requisitos

- **Java 21* 
- Editor de texto o IDE (VS Code, IntelliJ IDEA, Eclipse, etc.)

##  Instrucciones de Ejecución

### Opción 1: Compilación Manual
```bash
# Navegar al directorio del proyecto
cd yugi_oh_JAVA

# Compilar el código
javac src/*.java -d bin/

# Ejecutar la aplicación
java -cp bin/ App
```

### Opción 2: Desde VS Code
1. Abre el proyecto en VS Code
2. En la vista del Explorador, haz clic derecho en `App.java`
3. Selecciona "Run" o presiona `Ctrl+F5`
4. Sigue las instrucciones en la consola

### Opción 3: Desde el IDE (IntelliJ/Eclipse)
1. Importa el proyecto
2. Marca `src/` como carpeta de fuentes
3. Ejecuta `App.java` directamente

##  Cómo Jugar

1. **Inicio**: El programa muestra el menú principal
2. **Selecciona opción 1** para iniciar una partida
3. **Cada turno** permite:
   - Atacar con monstruos (opción 1)
   - Invocar un monstruo en ataque/defensa (opción 2)
   - Usar una carta mágica (opción 3)
   - Pasar turno (opción 4)
4. **Victoria**: Reduce los LP del enemigo a 0 o que se quede sin cartas en el mazo

##  Ejemplo de Sesión

```
====== BIENVENIDO A YU-GI-OH! ======
1. INICIAR PARTIDA
2. SALIR
=====================================
Ingresa: 1

--------------------------------------------------
DUELISTAS: [KEMPACHI] VS [SHINRA]
INICIA: [KEMPACHI]
--------------------------------------------------
========== TURNO DE: KEMPACHI ==========
[Se muestran opciones para atacar, invocar, etc.]
```

##  Desarrollado por
Jose Manuel Cuervo  2559905-3743

John Freddy Hurtado 2559863-3743

Jhon David Ceballos 2559724-3743
