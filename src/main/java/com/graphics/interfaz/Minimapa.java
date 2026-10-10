package com.graphics.interfaz; // Lo que se dibuja encima de la escena: minimapa y panel.

import com.graphics.motor.Renderizador; // Pincel con el que se dibuja cada caja.
import com.graphics.vehiculos.Auto; // Posición y orientación del jugador.
import static org.lwjgl.opengl.GL33.*; // Viewport, recorte y buffers de dibujo.

/**
 * MINIMAPA: la misma ciudad dibujada otra vez desde arriba, en un recuadro de la esquina.
 * Usa glViewport para dibujar solo en ese recuadro, glScissor para borrar solo ese recuadro y la
 * proyección ortográfica del shader (uMapa = 1) con el norte hacia arriba. La tecla M lo muestra u oculta.
 */
public class Minimapa {

    public boolean visible = true; // Se muestra desde el inicio.

    /** Muestra u oculta el minimapa. */
    public void alternar() {
        visible = !visible; // Invierte la visibilidad.
    }

    /** Dibuja el recuadro; escena es la función que dibuja la ciudad (la misma de la vista principal). */
    public void dibujar(Renderizador r, int ancho, int alto, Runnable escena, Auto auto) {
        if (!visible) { // El usuario lo ocultó con M.
            return; // Conserva únicamente la imagen principal.
        }
        int dimensionMenor = Math.min(ancho, alto); // Busca la dimensión que limita el espacio disponible.
        int lado = Math.min(260, dimensionMenor / 3); // Limita el mapa a 260 píxeles y a un tercio de la ventana.
        int margen = Math.min(18, dimensionMenor / 20); // Calcula una separación adaptable respecto a los bordes.
        int x = ancho - lado - margen; // Ubica el recuadro cerca del borde derecho.
        int y = alto - lado - margen; // Ubica el recuadro arriba; OpenGL mide Y desde abajo.
        glEnable(GL_SCISSOR_TEST); // Activa el recorte para no borrar el resto de la escena.
        glScissor(x - 3, y - 3, lado + 6, lado + 6); // Selecciona el mapa más un borde de tres píxeles.
        glClearColor(0.8f, 0.87f, 0.94f, 1); // Define un color claro para el marco.
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Borra solo el recuadro exterior gracias al scissor.
        glScissor(x, y, lado, lado); // Reduce el recorte al interior del minimapa.
        glClearColor(0.06f, 0.10f, 0.15f, 1); // Define el fondo oscuro del mapa.
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Limpia color y profundidad dentro del mapa.
        glViewport(x, y, lado, lado); // Redirige la proyección al recuadro cuadrado.
        r.entero("uMapa", 1); // Selecciona la proyección ortográfica del shader.
        r.vistaMapa = true; // Indica a los objetos que omitan sus detalles pequeños.
        try { // Asegura que el estado de dibujo se restaure incluso si el segundo pase falla.
            escena.run(); // Dibuja otra vez la misma ciudad, ahora vista desde arriba.
            dibujarIndicadorAuto(r, auto); // Resalta la posición y el frente del jugador.
        } finally { // El siguiente cuadro debe volver a la configuración de pantalla completa.
            r.vistaMapa = false; // Reactiva los detalles de la escena principal.
            r.entero("uMapa", 0); // Recupera la proyección en perspectiva.
            glDisable(GL_SCISSOR_TEST); // Permite que la próxima limpieza abarque toda la pantalla.
            glViewport(0, 0, ancho, alto); // Recupera el área de dibujo de la ventana completa.
        }
        if (glGetError() != GL_NO_ERROR) { // Comprueba que el pase del mapa no haya generado errores OpenGL.
            throw new IllegalStateException("Error OpenGL en minimapa"); // Expone el error en la consola.
        }
    }

    /** Dibuja una marca cian y una punta blanca por encima de los edificios del minimapa. */
    private void dibujarIndicadorAuto(Renderizador r, Auto auto) {
        r.cajaGirada(auto.x, 25, auto.z, 2.2f, 0.1f, 3.2f, 0.1f, 1, 1, auto.angulo); // Marca la posición con un rectángulo cian orientado.
        float frenteX = -(float) Math.sin(auto.angulo); // Calcula la dirección frontal en el eje X.
        float frenteZ = -(float) Math.cos(auto.angulo); // Calcula la dirección frontal en el eje Z.
        float puntaX = auto.x + frenteX * 2; // Desplaza la punta dos unidades hacia delante en X.
        float puntaZ = auto.z + frenteZ * 2; // Desplaza la punta dos unidades hacia delante en Z.
        r.caja(puntaX, 26, puntaZ, 0.9f, 0.1f, 0.9f, 1, 1, 1); // Dibuja la punta blanca encima del indicador cian.
    }
}
