# Fondo de Bikini: ciudad interactiva con OpenGL

Ampliación del proyecto de clase *Ciudad y conducción por etapas*. La ciudad pasa de 7 × 7 a **11 × 11 celdas** y se convierte en **Fondo de Bikini**: Bob Esponja reparte Cangreburgers en su botemóvil por un vecindario con casas, parques, tráfico y personajes que reaccionan al verlo pasar.

Base técnica: Java 17, Maven, LWJGL 3.3.3, GLFW, OpenGL 3.3 Core, shaders GLSL, VAO y VBO. Toda la geometría se genera con código a partir de un único cubo; no hay imágenes, modelos ni bibliotecas externas además de LWJGL.

El documento de entrega está en [`docs/Documento_Fondo_de_Bikini.docx`](docs/Documento_Fondo_de_Bikini.docx) (versión PDF para leer en el navegador: [`docs/Documento_Fondo_de_Bikini.pdf`](docs/Documento_Fondo_de_Bikini.pdf)) y las capturas en [`docs/capturas`](docs/capturas).

## Ejecutar

**Requisito:** un JDK 17 o superior, por ejemplo [Eclipse Temurin 17](https://adoptium.net/temurin/releases/?version=17). Para comprobarlo: `java -version`. No hace falta instalar Maven: el proyecto incluye Maven Wrapper, que lo descarga la primera vez junto con las dependencias (se necesita internet solo esa vez).

**1. Descargar el proyecto**

```sh
git clone https://github.com/AndyQdev/Prog-Prafica-Ciudad.git
cd Prog-Prafica-Ciudad
```

También sirve el botón **Code → Download ZIP** de GitHub; después hay que descomprimirlo y entrar a la carpeta.

**2. Ejecutar** desde una terminal abierta en la carpeta donde está `pom.xml`:

**Comando exacto (Windows, PowerShell o CMD):**

```sh
.\mvnw.cmd compile exec:exec
```

**macOS / Linux:**

```sh
./mvnw compile exec:exec
```

En Windows también se puede hacer **doble clic en `ejecutar.cmd`**, que ejecuta ese mismo comando. Desde VS Code también se puede abrir `src/main/java/com/graphics/Main.java` y pulsar **Run**. El juego empieza en el menú **FONDO DE BIKINI**: presiona **P** para jugar. ESC cierra la ventana. En macOS el perfil Maven agrega `-XstartOnFirstThread`; usa `exec:exec`, no `exec:java`.

## Cómo está organizado el código

Cada clase es un **objeto con una sola responsabilidad**. Los paquetes agrupan las responsabilidades:

```text
com.graphics
├── Main ................ punto de entrada: crea el Juego y lo ejecuta
├── Juego ............... el director: crea los objetos, atiende las teclas y repite el ciclo de cada cuadro
│
├── motor/ .............. CÓMO se dibuja (no sabe nada de Bob Esponja)
│   ├── Ventana          ventana y teclado (GLFW)
│   ├── Renderizador     el "pincel": sube un cubo a la GPU y lo dibuja con caja(); estado del cuadro (noche, minimapa, tiempo)
│   ├── Shaders          código GLSL: proyección (perspectiva, minimapa, pantalla) e iluminación
│   ├── Camara           cámara de seguimiento o aérea
│   ├── Texto · Fuente   letras de 5 × 7 cuadritos dibujadas con cajas
│
├── ciudad/ ............. QUÉ hay en la ciudad
│   ├── Mapa             la matriz 11 × 11, coordenadas y colisiones (teorema del eje separador)
│   ├── Ciudad           recorre el Mapa y le pide a cada objeto que se dibuje en su celda
│   ├── Calles           líneas, pasos peatonales, bordillos, bordes y alcantarillas
│   ├── Edificio         torres del centro con ventanas, tienda, mobiliario y techo
│   ├── Parque · Arbol   césped, árboles de tres niveles y bancos
│   ├── Barrio · Casa    manzanas residenciales con cuatro casas de cuatro estilos
│   ├── Farola           faroles; sus posiciones también alimentan la iluminación
│   ├── Semaforo         semáforos de dos direcciones con ciclo y luz de color
│   └── LugaresBikini    Crustáceo, Balde de Carnada, piña, moai y roca, con letreros
│
├── vehiculos/
│   ├── Auto             botemóvil del jugador: conducción, colisión, deslizamiento y dibujo
│   ├── Vehiculo         un bote autónomo: sigue su ruta por el carril derecho
│   └── Trafico          los cuatro botes; quién frena y con quién choca el jugador
│
├── personajes/
│   ├── Bob              Bob Esponja al volante
│   ├── Personajes       Don Cangrejo, Plankton, Patricio y Calamardo, que reaccionan al auto
│   └── VidaMarina       medusas y burbujas
│
├── juego/
│   ├── Iluminacion      día/noche (N), faros (F) y envío de todas las luces al shader
│   └── Entregas         las cinco Cangreburgers, el cronómetro y la marca dorada
│
└── interfaz/
    ├── Minimapa         la misma ciudad dibujada desde arriba en un recuadro (M)
    └── Panel            velocidad, luces y entregas en pantalla; menú de inicio y pausa (P)
```

### Qué pasa en cada cuadro

`Juego` repite este ciclo unas 60 veces por segundo:

1. **Actualizar** con `deltaTime`, los segundos transcurridos desde el cuadro anterior, para que la velocidad no dependa de la frecuencia de dibujo:
   `auto.actualizar()` → `entregas.actualizar()` → `trafico.actualizar()` → avanza `render.tiempo`.
   En pausa, `deltaTime` vale 0 y nada se mueve.
2. **Dibujar** en tres pasadas:
   - **Vista principal:** `camara.enviar()` y `luz.enviar()`, y después `dibujarEscena()`, que dibuja la ciudad, el auto, el destino, el tráfico y la vida marina.
   - **Minimapa:** la misma `dibujarEscena()` en un recuadro con proyección desde arriba.
   - **Panel:** texto y menú en píxeles de pantalla.

Todo objeto visible tiene un método `dibujar(Renderizador r, ...)` que arma su forma con `r.caja(...)`. Los objetos que giran (auto, botes, personajes) usan `r.origen(x, z, giro)` y después `r.parte(...)`, de modo que sus piezas se escriben en coordenadas locales.

### Dónde quedó el proyecto base del ingeniero

El proyecto original estaba en cuatro etapas encadenadas por herencia (`clase1` → `clase4`). Su contenido se conservó, pero repartido por responsabilidad:

| Etapa original | Ahora está en |
|---|---|
| `clase1`: ventana, shaders, cubo, mapa y cámara orbital | `Ventana`, `Renderizador`, `Shaders`, `Mapa`, `Ciudad`, `Calles`, `Camara` |
| `clase2`: auto, conducción, colisiones y cámara de seguimiento | `Auto`, `Mapa.puedeOcupar()`, `Camara` |
| `clase3`: día/noche, farolas, faros y shader de iluminación | `Iluminacion`, `Farola`, `Shaders` |
| `clase4`: ventanas, parques, pasos peatonales, semáforos, entregas y minimapa | `Edificio`, `Parque`, `Calles`, `Semaforo`, `Entregas`, `Minimapa` |

## La ciudad

```text
      0  1  2  3  4  5  6  7  8  9  10
 0    .  .  .  .  .  .  .  .  .  .  .    ← Calle Concha
 1    .  V  .  V  .  B  .  C  .  R  .    V = barrio (4 casas), B = piña de Bob, C = Calamardo, R = Patricio
 2    .  .  .  .  .  .  .  .  .  .  .
 3    .  V  .  E  .  E  .  P  .  V  .    E = edificios altos del centro, P = parque
 4    .  .  .  .  .  .  .  .  .  .  .
 5    .  V  .  P  .  E  .  E  .  V  .    filas y columnas 2 a 8 = ciudad original 7 x 7
 6    .  .  .  .  .  .  .  .  .  .  .
 7    .  V  .  E  .  P  .  E  .  P  .
 8    .  .  ▲  .  .  .  .  .  .  .  .    ▲ = inicio del auto (-30, 30)
 9    .  K  .  L  .  V  .  V  .  V  .    K = Crustáceo Cascarudo, L = Balde de Carnada
10    .  .  .  .  .  .  .  .  .  .  .
```

- **25 manzanas:** el centro conserva 6 edificios altos y lo rodean 10 barrios con 40 casas, 4 parques y 5 lugares de la serie. Hay 16 manzanas edificadas, desde casas de un piso hasta edificios de 13 unidades de alto, con colores variados.
- **Todo se adapta al Mapa:** `LIMITE = MAPA.length * CELDA / 2`. El suelo, los bordes, las colisiones, la cámara aérea, el encuadre del minimapa (`uLimite`), las 25 farolas (16 en los sectores nuevos) y los 25 semáforos se calculan a partir de la matriz. Para cambiar la ciudad basta con editar `Mapa.MAPA`.

## Funciones

- **Colisión con la forma real del auto:** el auto es un rectángulo de 2 × 2.8 que gira con él. Llega justo al bordillo, se desliza a lo largo de la acera en vez de frenar en seco y gira sobre su eje trasero.
- **Iluminación:**
  - Luz ambiental y direccional calculadas con las normales (Lambert).
  - 25 farolas con atenuación por la distancia.
  - Faros con un haz central y relleno, con alcance limitado a 24–32 unidades.
  - Luces de color de los semáforos.
  - Ventanas y luces de porche que se encienden de noche.
  - Luces traseras que se encienden al frenar.
- **Semáforos:** dos cabezales por cruce, con luces en ambas caras. Ciclo de 14 s: verde 5, amarillo 2, rojo 7. Nunca hay dos direcciones en verde.
- **Minimapa:** muestra toda la ciudad, el auto con su orientación, el destino y el tráfico, con el norte arriba. Se ajusta al redimensionar la ventana.
- **Entregas:** cinco Cangreburgers que se entregan en orden: Bob → Calamardo → Patricio → Balde → Crustáceo. Hay que frenar sobre la marca dorada.
- **Bonificaciones:**
  - Cuatro botes autónomos que siguen sus rutas.
  - Panel en pantalla con menú de inicio y pausa.
  - Letreros con nombres escritos con cajas.

## Controles

| Tecla | Acción |
|---|---|
| P | Empezar / pausar y ver la ayuda |
| W / S o flechas arriba / abajo | Acelerar / frenar y retroceder |
| A / D o flechas izquierda / derecha | Girar mientras el auto se mueve |
| Espacio | Freno |
| C | Cámara de seguimiento / aérea |
| N | Día / noche |
| F | Encender / apagar faros |
| M | Mostrar / ocultar minimapa |
| R | Reiniciar auto, entregas, cronómetro y tráfico |
| ESC | Salir |

## Verificación

```sh
.\mvnw.cmd test
```

Doce pruebas, sin abrir ventanas:
- el mapa cumple 11 × 11 con al menos 12 manzanas edificadas y 4 parques;
- una búsqueda por calles confirma que todos los destinos son accesibles desde el inicio;
- las calles y los bordes son transitables y las manzanas bloquean;
- el rectángulo del auto llega a 5 cm del bordillo de costado y de frente, y ocupa más en diagonal;
- hay al menos 9 farolas en los sectores nuevos;
- cada bote circula 100 s sin tocar manzanas ni salir del mapa;
- el jugador no atraviesa el tráfico;
- una entrega se completa al frenar en el destino y no al pasar rápido;
- la fuente está completa;
- el ciclo de los semáforos es correcto;
- R reinicia el auto, las entregas y el cronómetro.

Arranque gráfico breve: `-Ddemo.frames=30` se pasa a la **JVM del juego** (no a Maven) y cierra la ventana tras ese número de cuadros. Así se comprueba la compilación de shaders y la ausencia de errores OpenGL.

## Limitaciones

- La iluminación es local y sin sombras: una farola puede iluminar a través de un edificio. Los dos faros juntos pueden saturar de blanco a un personaje muy cercano.
- Los semáforos son visuales, como permite el enunciado: el tráfico no se detiene en ellos. En los barrios quedan dentro de la cerca.
- Los botes del tráfico solo esperan a los que van en su mismo sentido; dos botes que se cruzan en una intersección pueden atravesarse un instante.
- Las burbujas atraviesan los edificios y las medusas brillan, pero no iluminan el entorno.
- La colisión usa la manzana completa: el auto choca con el bordillo, no con cada casa, cerca o árbol.
- La fuente no incluye Ñ ni tildes (se escribe CAMARA, DIA).

## Recursos externos

No se usaron fragmentos de código, modelos, texturas ni fuentes externas. Toda la geometría (personajes, edificios, letras) está construida con cajas en el propio código. El proyecto base (ciudad, conducción, iluminación, decoración y minimapa) fue entregado por el ingeniero del curso y se reorganizó por objetos (ver la tabla de arriba). Los personajes y lugares son referencias a la serie *Bob Esponja* (Nickelodeon), recreadas con fines educativos.
