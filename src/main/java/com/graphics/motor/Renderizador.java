package com.graphics.motor; // Piezas técnicas que no saben nada de la ciudad: ventana, dibujo y cámara.

import java.util.HashMap; // Permite guardar las ubicaciones de las variables del shader.
import java.util.Map; // Define el tipo del diccionario de ubicaciones.
import static org.lwjgl.opengl.GL33.*; // Importa las funciones OpenGL hasta la versión 3.3.

/**
 * RENDERIZADOR: el "pincel" de todo el programa.
 * Guarda en la GPU un único cubo y lo dibuja escalado, girado y coloreado con caja(). Todos los
 * objetos de la ciudad (edificios, personajes, letras) son combinaciones de esa misma caja.
 * También conoce el estado del cuadro que todos los dibujos consultan: si es de noche, si se está
 * dibujando el minimapa y el tiempo de las animaciones.
 */
public class Renderizador {

    // ==================== 1. ESTADO ====================
    private int programa; // Identificador del programa que une ambos shaders.
    private int vao; // Identificador del VAO: describe cómo leer los vértices.
    private int vbo; // Identificador del VBO: contiene los vértices en la GPU.
    private final Map<String, Integer> uniforms = new HashMap<>(); // Evita buscar repetidamente el mismo uniform.
    public boolean noche = true; // Ambiente actual; lo copia Iluminacion en cada cuadro.
    public boolean vistaMapa = false; // true mientras se dibuja el minimapa: los objetos omiten sus detalles.
    public float tiempo = 0; // Segundos de animación; se detienen en pausa.
    private float origenX = 0; // Centro X del objeto que se está armando con parte().
    private float origenZ = 0; // Centro Z del objeto que se está armando con parte().
    private float origenGiro = 0; // Orientación de ese objeto; cero mira hacia el norte (-Z).
    private static final float OCTAVO = (float) Math.PI / 4; // 45 grados: girar una caja así forma un octógono.

    // ==================== 2. CREAR Y LIBERAR ====================

    /** Compila los shaders y sube el cubo a la GPU; luces y colores fijan el tamaño de los arreglos GLSL. */
    public void crear(int cantidadLuces, int cantidadColores) {
        glEnable(GL_DEPTH_TEST); // Hace que las superficies cercanas oculten las lejanas.
        crearPrograma(Shaders.vertices(), Shaders.fragmentos(cantidadLuces, cantidadColores)); // Compila y enlaza.
        crearCubo(); // Guarda en la GPU el cubo que servirá para todos los objetos.
    }

    /** Activa el programa y el cubo antes de dibujar un cuadro. */
    public void usar() {
        glUseProgram(programa); // Activa los shaders.
        glBindVertexArray(vao); // Selecciona los atributos del cubo compartido.
    }

    /** Libera únicamente los recursos que llegaron a crearse. */
    public void liberar() {
        if (programa != 0) { // Comprueba si existe un programa en la GPU.
            glDeleteProgram(programa); // Libera el programa de shaders.
        }
        if (vbo != 0) { // Comprueba si se reservó el buffer de vértices.
            glDeleteBuffers(vbo); // Libera los datos de geometría de la GPU.
        }
        if (vao != 0) { // Comprueba si se creó la descripción de atributos.
            glDeleteVertexArrays(vao); // Libera la configuración del cubo.
        }
    }

    // ==================== 3. DIBUJAR CAJAS ====================

    /** Dibuja una caja sin giro: posición XYZ (centro), tamaño XYZ y color RGB entre 0 y 1. */
    public void caja(float x, float y, float z, float sx, float sy, float sz, float r, float g, float b) {
        cajaGirada(x, y, z, sx, sy, sz, r, g, b, 0); // Reutiliza el dibujo general con un ángulo de cero.
    }

    /** Dibuja el cubo unitario con escala, giro alrededor de Y, posición y color. */
    public void cajaGirada(float x, float y, float z, float sx, float sy, float sz, float r, float g, float b, float angulo) {
        vector("uPos", x, y, z); // Envía la posición del centro de la caja en el mundo.
        vector("uEscala", sx, sy, sz); // Envía el ancho, alto y profundidad de la caja.
        vector("uColor", r, g, b); // Envía las intensidades roja, verde y azul del material.
        decimal("uGiro", angulo); // Envía la orientación en radianes.
        glDrawArrays(GL_TRIANGLES, 0, 36); // Dibuja 12 triángulos: dos por cada una de las seis caras.
    }

    /** Fija el centro y la orientación que usará parte(): un auto, un personaje o un bote del tráfico. */
    public void origen(float x, float z, float giro) {
        origenX = x; // Guarda el centro X.
        origenZ = z; // Guarda el centro Z.
        origenGiro = giro; // Guarda la orientación.
    }

    /** Dibuja una pieza en coordenadas locales del objeto actual: gira con él y se traslada a su posición. */
    public void parte(float x, float y, float z, float sx, float sy, float sz, float r, float g, float b) {
        float coseno = (float) Math.cos(origenGiro); // Coseno de la orientación del objeto.
        float seno = (float) Math.sin(origenGiro); // Seno de la misma orientación.
        float mundoX = origenX + coseno * x + seno * z; // Gira la posición local y la traslada.
        float mundoZ = origenZ - seno * x + coseno * z; // Completa la transformación en Z.
        cajaGirada(mundoX, y, mundoZ, sx, sy, sz, r, g, b, origenGiro); // Dibuja la pieza orientada.
    }

    /** Une una caja normal y otra girada 45 grados para formar un prisma de ocho puntas. */
    public void octogono(float x, float y, float z, float ancho, float alto, float r, float g, float b) {
        caja(x, y, z, ancho, alto, ancho, r, g, b); // Caja alineada con los ejes.
        cajaGirada(x, y, z, ancho, alto, ancho, r, g, b, OCTAVO); // Misma caja girada: sus esquinas cortan las anteriores.
    }

    /** Cuatro cajas giradas de 22.5 en 22.5 grados: sus esquinas forman un contorno casi redondo. */
    public void cilindro(float x, float y, float z, float ancho, float alto, float r, float g, float b) {
        for (int paso = 0; paso < 4; paso++) { // Cuatro copias de la misma caja.
            cajaGirada(x, y, z, ancho, alto, ancho, r, g, b, paso * OCTAVO / 2); // Cada una gira un dieciseisavo de vuelta más.
        }
    }

    /** Dibuja un vidrio celeste de día y una ventana encendida de noche. */
    public void vidrio(float x, float y, float z, float sx, float sy, float sz) {
        if (noche) { // De noche la ventana aparenta una habitación iluminada.
            entero("uEmision", 1); // Su color no depende de las luces cercanas.
            caja(x, y, z, sx, sy, sz, 0.95f, 0.80f, 0.40f); // Luz cálida.
            entero("uEmision", 0); // Restablece la iluminación normal.
        } else { // De día solo refleja el agua.
            caja(x, y, z, sx, sy, sz, 0.55f, 0.78f, 0.90f); // Vidrio celeste.
        }
    }

    // ==================== 4. ENVIAR DATOS AL SHADER ====================

    /** Busca un uniform una vez y guarda su ubicación para los siguientes dibujos. */
    public int uniform(String nombre) {
        if (!uniforms.containsKey(nombre)) { // Revisa si esta variable todavía no fue localizada.
            int ubicacion = glGetUniformLocation(programa, nombre); // Pregunta a OpenGL por la dirección del uniform.
            uniforms.put(nombre, ubicacion); // Guarda la respuesta; -1 significa que el shader no usa esa variable.
        }
        return uniforms.get(nombre); // Recupera la ubicación guardada; OpenGL ignora envíos a -1.
    }

    /** Envía tres números reales a una variable vec3 del shader. */
    public void vector(String nombre, float x, float y, float z) {
        glUniform3f(uniform(nombre), x, y, z); // Escribe las tres componentes en el programa activo.
    }

    /** Envía dos números reales a una variable vec2 del shader. */
    public void vector2(String nombre, float x, float y) {
        glUniform2f(uniform(nombre), x, y); // Escribe las dos componentes.
    }

    /** Envía un número real a una variable float del shader. */
    public void decimal(String nombre, float valor) {
        glUniform1f(uniform(nombre), valor); // Actualiza el escalar identificado por su nombre.
    }

    /** Envía un número entero a una variable int del shader. */
    public void entero(String nombre, int valor) {
        glUniform1i(uniform(nombre), valor); // Actualiza un entero, usado también para interruptores 0/1.
    }

    // ==================== 5. SHADERS Y GEOMETRÍA ====================

    /** Compila un shader y comunica el mensaje del controlador si hay un error GLSL. */
    private int compilar(int tipo, String fuente) {
        int shader = glCreateShader(tipo); // Reserva un shader del tipo vértice o fragmento.
        glShaderSource(shader, fuente); // Entrega a OpenGL el texto GLSL.
        glCompileShader(shader); // Solicita la compilación para esta GPU.
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) { // Consulta si la compilación falló.
            String error = glGetShaderInfoLog(shader); // Recupera el detalle del error antes de borrar el objeto.
            glDeleteShader(shader); // Libera el shader que no pudo compilarse.
            throw new IllegalStateException(error); // Detiene la aplicación con el mensaje de diagnóstico.
        }
        return shader; // Devuelve el identificador del shader compilado.
    }

    /** Une los shaders en un programa ejecutable y libera los objetos intermedios. */
    private void crearPrograma(String fuenteVertices, String fuenteFragmentos) {
        int shaderVertices = compilar(GL_VERTEX_SHADER, fuenteVertices); // Compila las transformaciones geométricas.
        int shaderFragmentos = 0; // Permite saber si el segundo shader llegó a crearse.
        try { // Garantiza que ambos objetos intermedios se liberen incluso ante un fallo.
            shaderFragmentos = compilar(GL_FRAGMENT_SHADER, fuenteFragmentos); // Compila el cálculo del color.
            programa = glCreateProgram(); // Crea el contenedor que enlazará ambos shaders.
            glAttachShader(programa, shaderVertices); // Adjunta el shader que calcula posiciones.
            glAttachShader(programa, shaderFragmentos); // Adjunta el shader que calcula colores.
            glLinkProgram(programa); // Enlaza las entradas y salidas de ambos shaders.
            if (glGetProgrami(programa, GL_LINK_STATUS) == GL_FALSE) { // Comprueba que el enlace haya sido válido.
                throw new IllegalStateException(glGetProgramInfoLog(programa)); // Muestra incompatibilidades del programa.
            }
        } finally { // Ya no hacen falta objetos shader separados después del enlace.
            glDeleteShader(shaderVertices); // Libera el objeto del shader de vértices.
            if (shaderFragmentos != 0) { // Comprueba que el segundo objeto exista.
                glDeleteShader(shaderFragmentos); // Libera el objeto del shader de fragmentos.
            }
        }
    }

    /** Crea un cubo explícito, con posición XYZ y normal XYZ por vértice, como en los ejemplos de clase. */
    private void crearCubo() {
        float[] vertices = { // Cada línea describe un vértice: X, Y, Z, normalX, normalY, normalZ.
            -0.5f, -0.5f, 0.5f, 0, 0, 1, // Cara frontal: esquina inferior izquierda, normal hacia +Z.
            0.5f, -0.5f, 0.5f, 0, 0, 1, // Cara frontal: esquina inferior derecha.
            0.5f, 0.5f, 0.5f, 0, 0, 1, // Cara frontal: esquina superior derecha; termina el primer triángulo.
            0.5f, 0.5f, 0.5f, 0, 0, 1, // Cara frontal: repite la esquina para el segundo triángulo.
            -0.5f, 0.5f, 0.5f, 0, 0, 1, // Cara frontal: esquina superior izquierda.
            -0.5f, -0.5f, 0.5f, 0, 0, 1, // Cara frontal: cierra el segundo triángulo.

            -0.5f, -0.5f, -0.5f, 0, 0, -1, // Cara trasera: primer vértice, normal hacia -Z.
            -0.5f, 0.5f, -0.5f, 0, 0, -1, // Cara trasera: segundo vértice.
            0.5f, 0.5f, -0.5f, 0, 0, -1, // Cara trasera: termina el primer triángulo.
            0.5f, 0.5f, -0.5f, 0, 0, -1, // Cara trasera: inicia el segundo triángulo.
            0.5f, -0.5f, -0.5f, 0, 0, -1, // Cara trasera: esquina inferior derecha.
            -0.5f, -0.5f, -0.5f, 0, 0, -1, // Cara trasera: cierra la cara.

            -0.5f, 0.5f, 0.5f, -1, 0, 0, // Cara izquierda: primer vértice, normal hacia -X.
            -0.5f, 0.5f, -0.5f, -1, 0, 0, // Cara izquierda: segundo vértice.
            -0.5f, -0.5f, -0.5f, -1, 0, 0, // Cara izquierda: termina el primer triángulo.
            -0.5f, -0.5f, -0.5f, -1, 0, 0, // Cara izquierda: inicia el segundo triángulo.
            -0.5f, -0.5f, 0.5f, -1, 0, 0, // Cara izquierda: esquina inferior frontal.
            -0.5f, 0.5f, 0.5f, -1, 0, 0, // Cara izquierda: cierra la cara.

            0.5f, 0.5f, 0.5f, 1, 0, 0, // Cara derecha: primer vértice, normal hacia +X.
            0.5f, -0.5f, 0.5f, 1, 0, 0, // Cara derecha: segundo vértice.
            0.5f, -0.5f, -0.5f, 1, 0, 0, // Cara derecha: termina el primer triángulo.
            0.5f, -0.5f, -0.5f, 1, 0, 0, // Cara derecha: inicia el segundo triángulo.
            0.5f, 0.5f, -0.5f, 1, 0, 0, // Cara derecha: esquina superior trasera.
            0.5f, 0.5f, 0.5f, 1, 0, 0, // Cara derecha: cierra la cara.

            -0.5f, 0.5f, -0.5f, 0, 1, 0, // Cara superior: primer vértice, normal hacia +Y.
            -0.5f, 0.5f, 0.5f, 0, 1, 0, // Cara superior: segundo vértice.
            0.5f, 0.5f, 0.5f, 0, 1, 0, // Cara superior: termina el primer triángulo.
            0.5f, 0.5f, 0.5f, 0, 1, 0, // Cara superior: inicia el segundo triángulo.
            0.5f, 0.5f, -0.5f, 0, 1, 0, // Cara superior: esquina trasera derecha.
            -0.5f, 0.5f, -0.5f, 0, 1, 0, // Cara superior: cierra la cara.

            -0.5f, -0.5f, -0.5f, 0, -1, 0, // Cara inferior: primer vértice, normal hacia -Y.
            0.5f, -0.5f, -0.5f, 0, -1, 0, // Cara inferior: segundo vértice.
            0.5f, -0.5f, 0.5f, 0, -1, 0, // Cara inferior: termina el primer triángulo.
            0.5f, -0.5f, 0.5f, 0, -1, 0, // Cara inferior: inicia el segundo triángulo.
            -0.5f, -0.5f, 0.5f, 0, -1, 0, // Cara inferior: esquina frontal izquierda.
            -0.5f, -0.5f, -0.5f, 0, -1, 0 // Cara inferior: cierra los 36 vértices del cubo.
        };
        vao = glGenVertexArrays(); // Reserva el objeto que recordará la disposición de los atributos.
        glBindVertexArray(vao); // Activa ese VAO para configurarlo.
        vbo = glGenBuffers(); // Reserva el buffer que almacenará los vértices.
        glBindBuffer(GL_ARRAY_BUFFER, vbo); // Selecciona el VBO como destino de datos de vértices.
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW); // Copia los datos a la GPU; el cubo no cambiará.
        int bytesPorVertice = 6 * Float.BYTES; // Cada vértice contiene seis float de cuatro bytes.
        glVertexAttribPointer(0, 3, GL_FLOAT, false, bytesPorVertice, 0L); // Describe XYZ desde el primer byte del vértice.
        glEnableVertexAttribArray(0); // Habilita la posición que recibe aPos.
        glVertexAttribPointer(1, 3, GL_FLOAT, false, bytesPorVertice, 3L * Float.BYTES); // Describe la normal después de XYZ.
        glEnableVertexAttribArray(1); // Habilita la normal que recibe aNormal.
        glBindBuffer(GL_ARRAY_BUFFER, 0); // Deja de seleccionar el VBO una vez configurado.
        glBindVertexArray(0); // Finaliza la configuración del VAO.
    }
}
