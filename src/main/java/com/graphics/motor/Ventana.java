package com.graphics.motor; // Piezas técnicas que no saben nada de la ciudad: ventana, dibujo y cámara.

import java.util.function.IntConsumer; // Función que recibe el código de una tecla.
import org.lwjgl.glfw.Callbacks; // Permite liberar los callbacks de la ventana al cerrar.
import org.lwjgl.glfw.GLFWErrorCallback; // Muestra los errores de GLFW en la consola.
import org.lwjgl.opengl.GL; // Carga las funciones OpenGL disponibles en el equipo.
import static org.lwjgl.glfw.GLFW.*; // Importa las funciones y constantes de ventanas y teclado.

/**
 * VENTANA: todo lo que hace GLFW.
 * Crea la ventana con su contexto OpenGL 3.3, entrega las teclas presionadas, informa el tamaño
 * en píxeles y presenta cada imagen terminada. El resto del programa no llama a GLFW directamente.
 */
public class Ventana {

    private long id; // Identificador que GLFW asigna a la ventana.
    private GLFWErrorCallback errores; // Manejador que imprime los errores de GLFW.
    public int ancho; // Ancho actual del área de dibujo, en píxeles.
    public int alto; // Alto actual del área de dibujo, en píxeles.

    /** Guarda el tamaño inicial; la ventana se abre recién con crear(). */
    public Ventana(int ancho, int alto) {
        this.ancho = ancho; // Ancho inicial.
        this.alto = alto; // Alto inicial.
    }

    /** Inicia GLFW, abre la ventana y avisa cada tecla presionada a la función recibida. */
    public void crear(String titulo, IntConsumer alPresionar) {
        errores = GLFWErrorCallback.createPrint(System.err); // Prepara mensajes de error en consola.
        errores.set(); // Instala el manejador de errores de GLFW.
        if (!glfwInit()) { // Intenta iniciar la biblioteca que administra ventanas y entrada.
            throw new IllegalStateException("No se pudo iniciar GLFW"); // Detiene el arranque si GLFW falla.
        }
        glfwDefaultWindowHints(); // Restablece las opciones de ventana antes de personalizarlas.
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3); // Solicita la versión mayor de OpenGL.
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3); // Solicita OpenGL 3.3.
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE); // Usa el perfil moderno basado en shaders.
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE); // Solicita compatibilidad con contextos modernos de macOS.
        id = glfwCreateWindow(ancho, alto, titulo, 0, 0); // Crea una ventana normal.
        if (id == 0) { // GLFW devuelve cero cuando no consigue crear la ventana.
            throw new IllegalStateException("No se pudo crear la ventana OpenGL"); // Informa el fallo de creación.
        }
        glfwMakeContextCurrent(id); // Selecciona la ventana que recibirá las llamadas OpenGL.
        glfwSwapInterval(1); // Sincroniza la presentación con la pantalla para reducir cortes de imagen.
        GL.createCapabilities(); // Carga las funciones OpenGL del contexto actual.
        glfwSetKeyCallback(id, (ventanaEvento, key, scancode, action, mods) -> { // Registra la función que recibe el teclado.
            if (action == GLFW_PRESS) { // Responde una sola vez al presionar, no a las repeticiones de la tecla.
                alPresionar.accept(key); // Entrega la tecla al juego.
            }
        }); // Termina el registro del callback de teclado.
    }

    /** Consulta si una tecla sigue presionada durante el cuadro actual (conducción continua). */
    public boolean pulsada(int key) {
        return glfwGetKey(id, key) == GLFW_PRESS; // Devuelve true mientras el usuario mantiene la tecla.
    }

    /** Indica si la ventana sigue abierta. */
    public boolean abierta() {
        return !glfwWindowShouldClose(id); // GLFW marca la ventana cuando se pide cerrar.
    }

    /** Solicita cerrar la ventana; el ciclo del juego termina en la próxima vuelta. */
    public void cerrar() {
        glfwSetWindowShouldClose(id, true); // Usa el mismo camino que el botón de cierre.
    }

    /** Procesa teclado, redimensionamiento y botón de cierre. */
    public void procesarEventos() {
        glfwPollEvents(); // Atiende los eventos pendientes del sistema operativo.
    }

    /** Segundos transcurridos desde que se inició GLFW. */
    public double segundos() {
        return glfwGetTime(); // Reloj de alta precisión de GLFW.
    }

    /** Actualiza ancho y alto con los píxeles reales, también en pantallas Retina. */
    public void actualizarTamano() {
        int[] anchoReal = new int[1]; // Espacio para que GLFW escriba el ancho.
        int[] altoReal = new int[1]; // Espacio para que GLFW escriba el alto.
        glfwGetFramebufferSize(id, anchoReal, altoReal); // Consulta el tamaño del área de dibujo.
        ancho = anchoReal[0]; // Copia el ancho actual.
        alto = altoReal[0]; // Copia el alto actual.
    }

    /** Muestra la imagen terminada intercambiando los buffers. */
    public void mostrar() {
        glfwSwapBuffers(id); // Presenta el cuadro dibujado.
    }

    /** Cambia el texto de la barra de título. */
    public void titulo(String texto) {
        glfwSetWindowTitle(id, texto); // Publica el texto en la barra superior.
    }

    /** Libera la ventana, sus callbacks y GLFW. */
    public void destruir() {
        if (id != 0) { // Evita destruir una ventana que no pudo crearse.
            Callbacks.glfwFreeCallbacks(id); // Libera el callback de teclado registrado.
            glfwDestroyWindow(id); // Destruye la ventana y su contexto OpenGL.
        }
        glfwTerminate(); // Finaliza GLFW y sus recursos globales.
        glfwSetErrorCallback(null); // Desconecta el manejador antes de liberar su memoria.
        if (errores != null) { // Solo si llegó a crearse.
            errores.free(); // Libera el callback de errores.
        }
    }
}
