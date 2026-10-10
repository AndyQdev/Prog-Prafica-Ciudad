package com.graphics; // Paquete raíz: el juego y su punto de entrada.

import com.graphics.ciudad.Ciudad; // Dibuja el mapa completo.
import com.graphics.ciudad.Farola; // Cantidad de farolas para el shader.
import com.graphics.ciudad.Mapa; // Tamaño de la ciudad para el minimapa.
import com.graphics.ciudad.Semaforo; // Cantidad de luces de semáforo para el shader.
import com.graphics.interfaz.Minimapa; // Vista superior en la esquina.
import com.graphics.interfaz.Panel; // Panel de estado y menú.
import com.graphics.juego.Entregas; // Objetivo del juego.
import com.graphics.juego.Iluminacion; // Día, noche y faros.
import com.graphics.motor.Camara; // Seguimiento o vista aérea.
import com.graphics.motor.Renderizador; // Pincel de OpenGL.
import com.graphics.motor.Ventana; // Ventana y teclado de GLFW.
import com.graphics.personajes.Personajes; // Don Cangrejo, Plankton, Patricio y Calamardo.
import com.graphics.personajes.VidaMarina; // Medusas y burbujas.
import com.graphics.vehiculos.Auto; // Botemóvil del jugador.
import com.graphics.vehiculos.Trafico; // Botes autónomos.
import static org.lwjgl.glfw.GLFW.*; // Constantes de las teclas.
import static org.lwjgl.opengl.GL33.*; // Limpiar la pantalla al comenzar cada cuadro.

/**
 * JUEGO: el director de la obra.
 * Crea todos los objetos, atiende las teclas y repite el ciclo de cada cuadro:
 *   1. actualizar(deltaTime): mover el auto, el tráfico, las entregas y el tiempo de animación.
 *   2. dibujarCuadro(): vista principal en perspectiva, luego el minimapa y por último el panel.
 * Cada objeto sabe actualizarse y dibujarse a sí mismo; el Juego solo decide el orden.
 */
public class Juego {

    // ==================== 1. OBJETOS DEL JUEGO ====================
    protected final Ventana ventana = new Ventana(1100, 760); // Ventana de 1100 x 760 píxeles al iniciar.
    protected final Renderizador render = new Renderizador(); // Dibuja cajas con OpenGL.
    protected final Camara camara = new Camara(); // Decide desde dónde se mira.
    protected final Iluminacion luz = new Iluminacion(); // Día, noche y faros.
    protected final Auto auto = new Auto(); // Botemóvil del jugador.
    protected final Trafico trafico = new Trafico(); // Cuatro botes autónomos.
    protected final Entregas entregas = new Entregas(); // Cinco Cangreburgers por entregar.
    protected final Personajes personajes = new Personajes(auto, entregas); // Vecinos que reaccionan al auto.
    protected final Ciudad ciudad = new Ciudad(personajes); // Mapa, manzanas y señales.
    protected final Minimapa minimapa = new Minimapa(); // Vista superior.
    protected final Panel panel = new Panel(); // Panel y menú.
    protected boolean pausado = true; // El juego empieza detenido mostrando el menú de inicio.
    protected boolean iniciado = false; // Cambia a true la primera vez que se presiona P.

    /** Conecta el auto con el tráfico para que también choque con los botes. */
    public Juego() {
        auto.usarTrafico(trafico); // El auto consulta al tráfico antes de moverse.
    }

    // ==================== 2. CICLO DE VIDA ====================

    /** Abre la ventana, repite el ciclo hasta cerrarla y libera los recursos al terminar. */
    public void run() {
        try { // Protege el arranque y el ciclo para poder liberar recursos si fallan.
            ventana.crear("Fondo de Bikini", this::tecla); // Crea la ventana y conecta el teclado.
            render.crear(Farola.POSICIONES.length, Semaforo.cantidadLuces()); // Compila shaders y sube el cubo.
            ciclo(); // Actualiza y dibuja hasta que se cierre la ventana.
        } finally { // Se ejecuta tanto al salir normalmente como al fallar.
            render.liberar(); // Libera la GPU.
            ventana.destruir(); // Libera la ventana y GLFW.
        }
    }

    /** Ciclo de cada cuadro: eventos, actualización con el tiempo transcurrido, dibujo y presentación. */
    private void ciclo() {
        double tiempoAnterior = ventana.segundos(); // Instante inicial para calcular el tiempo entre cuadros.
        int cuadros = 0; // Cuenta los cuadros dibujados para la comprobación automática opcional.
        int maxCuadros = Integer.getInteger("demo.frames", 0); // Cero permite jugar; otro valor limita el arranque de prueba.
        while (ventana.abierta()) { // Repite mientras no se haya solicitado cerrar.
            ventana.procesarEventos(); // Procesa teclado, redimensionamiento y botón de cierre.
            double tiempoActual = ventana.segundos(); // Consulta el tiempo actual en segundos.
            float deltaTime = (float) Math.min(tiempoActual - tiempoAnterior, 0.05); // Evita saltos de más de 50 ms.
            tiempoAnterior = tiempoActual; // Conserva el instante de este cuadro para la próxima vuelta.
            actualizar(deltaTime); // Mueve todo lo que cambia con el tiempo.
            ventana.actualizarTamano(); // Obtiene los píxeles reales del área de dibujo.
            if (ventana.ancho > 0 && ventana.alto > 0) { // Evita dibujar cuando la ventana está minimizada.
                dibujarCuadro(); // Genera la imagen de este cuadro.
            }
            ventana.mostrar(); // Muestra la imagen terminada.
            cuadros++; // Registra que se completó una vuelta del ciclo.
            if (maxCuadros > 0 && cuadros >= maxCuadros) { // Detecta el final de un arranque automático limitado.
                ventana.cerrar(); // Sale por el mismo camino que un cierre normal.
            }
        }
    }

    // ==================== 3. TECLADO ====================

    /** Atiende las teclas de una sola pulsación; la conducción continua la lee Auto. */
    protected void tecla(int key) {
        if (key == GLFW_KEY_ESCAPE) { // Salir.
            ventana.cerrar(); // Termina el ciclo principal.
        } else if (key == GLFW_KEY_C) { // Cámara.
            camara.alternar(); // Seguimiento o aérea.
        } else if (key == GLFW_KEY_R) { // Reiniciar.
            reiniciar(); // Comienza un nuevo recorrido.
        } else if (key == GLFW_KEY_N) { // Ambiente.
            luz.alternarNoche(); // Día o noche.
        } else if (key == GLFW_KEY_F) { // Faros.
            luz.alternarFaros(); // Encendidos o apagados.
        } else if (key == GLFW_KEY_M) { // Minimapa.
            minimapa.alternar(); // Visible u oculto.
        } else if (key == GLFW_KEY_P) { // Pausa.
            pausado = !pausado; // Alterna entre jugar y ver el menú.
            iniciado = true; // Después de la primera vez, el menú se llama PAUSA.
        }
    }

    /** Restablece auto, entregas, cronómetro y tráfico para empezar de nuevo. */
    protected void reiniciar() {
        auto.reiniciar(); // Posición, orientación y velocidad iniciales.
        entregas.reiniciar(); // Primera parada y cronómetro en cero.
        trafico.reiniciar(); // Cada bote al inicio de su ruta.
    }

    // ==================== 4. ACTUALIZAR ====================

    /** Mueve todo; en pausa el tiempo no avanza (deltaTime = 0) y nada se mueve. */
    protected void actualizar(float deltaTime) {
        if (pausado) { // Con el menú abierto el tiempo se detiene.
            deltaTime = 0; // Con cero segundos, ninguna fórmula de movimiento cambia nada.
        }
        auto.actualizar(deltaTime, ventana); // Conducción, giros y colisiones.
        entregas.actualizar(deltaTime, auto); // Cronómetro y llegada a los destinos.
        trafico.actualizar(deltaTime, auto); // Botes autónomos.
        render.tiempo += deltaTime; // Reloj de las animaciones: personajes, semáforos, medusas.
        actualizarTitulo(); // Indicador básico en la barra de la ventana.
    }

    /** Compone el texto de la barra de título con los controles y el estado del juego. */
    private void actualizarTitulo() {
        int kilometrosPorHora = Math.round(Math.abs(auto.velocidad) * 3.6f); // Convierte m/s a km/h.
        String titulo = "Fondo de Bikini | WASD/flechas: conducir | Espacio: freno"; // Controles del vehículo.
        titulo += " | C: camara | R: reiniciar | P: pausa | " + kilometrosPorHora + " km/h"; // Más controles y velocidad.
        titulo += luz.estado() + " | M: mapa | " + entregas.estado(); // Luces, minimapa y entregas.
        ventana.titulo(titulo); // Publica el texto en la barra superior.
    }

    // ==================== 5. DIBUJAR ====================

    /** Tres pasadas: vista principal en perspectiva, minimapa en su recuadro y panel encima de todo. */
    protected void dibujarCuadro() {
        int ancho = ventana.ancho; // Ancho actual en píxeles.
        int alto = ventana.alto; // Alto actual en píxeles.
        glViewport(0, 0, ancho, alto); // Utiliza toda el área de la ventana para la escena principal.
        float[] cielo = luz.colorCielo(); // Color de fondo según el ambiente.
        glClearColor(cielo[0], cielo[1], cielo[2], 1); // Define el fondo completamente opaco.
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Borra la imagen y las distancias del cuadro anterior.
        render.usar(); // Activa los shaders y el cubo.
        render.entero("uMapa", 0); // Perspectiva normal.
        render.decimal("uLimite", Mapa.LIMITE); // Tamaño de la ciudad para encuadrar el minimapa.
        camara.enviar(render, auto.x, auto.z, auto.angulo, ancho, alto); // Posición de la cámara.
        luz.enviar(render, auto); // Ambiente, farolas, semáforos y faros.
        dibujarEscena(); // Pasada 1: la ciudad vista desde la cámara.
        if (glGetError() != GL_NO_ERROR) { // Comprueba si OpenGL reportó una operación inválida.
            throw new IllegalStateException("Error OpenGL al dibujar"); // Hace visible el problema en consola.
        }
        minimapa.dibujar(render, ancho, alto, this::dibujarEscena, auto); // Pasada 2: la misma escena desde arriba.
        panel.dibujar(render, ancho, alto, auto, luz, entregas, pausado, iniciado); // Pasada 3: panel y menú.
    }

    /** Todo lo que hay en el mundo; se usa en la vista principal y en el minimapa. */
    protected void dibujarEscena() {
        ciudad.dibujar(render); // Suelo, calles, manzanas, señales y farolas.
        auto.dibujar(render, luz.faros); // Botemóvil con Bob.
        entregas.dibujar(render); // Marca dorada del destino activo.
        trafico.dibujar(render); // Botes autónomos.
        VidaMarina.dibujar(render); // Medusas y burbujas.
    }
}
