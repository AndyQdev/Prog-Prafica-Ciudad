package com.graphics.ciudad; // Todo lo que forma la ciudad: mapa, manzanas, calles y mobiliario.

import com.graphics.motor.Renderizador; // Pincel con el que se dibuja cada caja.

/**
 * EDIFICIO: torre del centro de la ciudad.
 * Cuerpo de 7 x 7 con altura y color según su celda, ventanas que se encienden de noche, tienda en
 * planta baja con toldo, árboles de vereda, hidrante, basurero, banco y equipos sobre el techo.
 */
public final class Edificio {

    private static final float[][] TOLDOS = { // Toldos de las tiendas en planta baja.
        {0.85f, 0.20f, 0.20f}, {0.20f, 0.55f, 0.35f}, {0.95f, 0.65f, 0.15f}, {0.25f, 0.40f, 0.80f}
    };

    /** Clase de utilidades: no se crean objetos Edificio. */
    private Edificio() {
    }

    /** Dibuja el edificio de la celda (fila, columna) con centro en (x, z). */
    public static void dibujar(Renderizador r, int fila, int columna, float x, float z) {
        float altura = Mapa.alturaEdificio(fila, columna); // Altura entre 5 y 13.
        float[] color = Mapa.colorEdificio(fila, columna); // Color de la fachada.
        r.caja(x, altura / 2 + 0.3f, z, 7, altura, 7, color[0], color[1], color[2]); // Cuerpo sobre la acera.
        r.caja(x, altura + 0.45f, z, 7.3f, 0.3f, 7.3f, 0.20f, 0.26f, 0.32f); // Cubierta más ancha y oscura.
        if (r.vistaMapa) { // En el minimapa basta con el bloque.
            return; // Omite ventanas y detalles.
        }
        dibujarVentanas(r, x, z, altura); // Ventanas en las cuatro fachadas.
        dibujarDetalles(r, fila, columna, x, z); // Tienda, vereda y techo.
    }

    /** Distribuye ventanas por pisos en las cuatro paredes del edificio. */
    private static void dibujarVentanas(Renderizador r, float x, float z, float altura) {
        if (r.noche) { // Las ventanas simulan habitaciones encendidas en el ambiente nocturno.
            r.entero("uEmision", 1); // Permite ver el color de las ventanas sin depender de farolas.
        }
        for (float y = 1.7f; y < altura; y += 2) { // Recorre los pisos separados por dos unidades de altura.
            for (float desplazamiento = -2; desplazamiento <= 2; desplazamiento += 2) { // Coloca tres ventanas por fachada.
                r.caja(x + desplazamiento, y, z - 3.51f, 0.8f, 0.9f, 0.04f, 0.95f, 0.75f, 0.38f); // Ventana de la fachada norte.
                r.caja(x + desplazamiento, y, z + 3.51f, 0.8f, 0.9f, 0.04f, 0.95f, 0.75f, 0.38f); // Ventana de la fachada sur.
                r.caja(x - 3.51f, y, z + desplazamiento, 0.04f, 0.9f, 0.8f, 0.95f, 0.75f, 0.38f); // Ventana de la fachada oeste.
                r.caja(x + 3.51f, y, z + desplazamiento, 0.04f, 0.9f, 0.8f, 0.95f, 0.75f, 0.38f); // Ventana de la fachada este.
            }
        }
        r.entero("uEmision", 0); // Restablece la iluminación normal de los demás elementos.
    }

    /** Puerta con toldo, árboles de vereda, hidrante, basurero, banco y equipos sobre el techo. */
    private static void dibujarDetalles(Renderizador r, int fila, int columna, float x, float z) {
        float[] toldo = TOLDOS[(fila + columna * 3) % TOLDOS.length]; // Color del toldo de la tienda.
        r.caja(x, 1.05f, z + 3.53f, 1.0f, 1.5f, 0.04f, 0.15f, 0.20f, 0.25f); // Puerta de vidrio oscuro en la fachada sur.
        r.caja(x, 2.5f, z + 3.85f, 2.4f, 0.12f, 0.7f, toldo[0], toldo[1], toldo[2]); // Toldo sobre la puerta.
        r.caja(x, 2.38f, z + 4.18f, 2.4f, 0.18f, 0.04f, 0.95f, 0.95f, 0.92f); // Borde blanco del toldo.
        Arbol.dibujarDeCalle(r, x - 2.2f, z + 4.2f); // Árbol de vereda a la izquierda de la puerta.
        Arbol.dibujarDeCalle(r, x + 2.2f, z + 4.2f); // Árbol de vereda a la derecha.
        r.caja(x + 4.3f, 0.6f, z + 4.3f, 0.3f, 0.6f, 0.3f, 0.85f, 0.15f, 0.12f); // Hidrante rojo en la esquina sureste.
        r.caja(x + 4.3f, 0.95f, z + 4.3f, 0.2f, 0.12f, 0.2f, 0.85f, 0.15f, 0.12f); // Tapa del hidrante.
        r.caja(x - 4.3f, 0.75f, z + 4.3f, 0.45f, 0.9f, 0.45f, 0.20f, 0.45f, 0.30f); // Basurero verde en la esquina suroeste.
        r.caja(x + 4.25f, 0.62f, z, 0.5f, 0.1f, 1.6f, 0.55f, 0.32f, 0.15f); // Asiento del banco de la vereda este.
        r.caja(x + 4.02f, 0.9f, z, 0.1f, 0.5f, 1.6f, 0.55f, 0.32f, 0.15f); // Respaldo del lado del edificio: se sienta mirando la calle.
        r.caja(x + 4.25f, 0.45f, z, 0.4f, 0.3f, 1.2f, 0.25f, 0.25f, 0.28f); // Patas del banco.
        float techo = Mapa.alturaEdificio(fila, columna) + 0.6f; // Altura de la cubierta del edificio.
        r.cilindro(x - 1.6f, techo + 0.9f, z - 1.6f, 1.4f, 1.3f, 0.55f, 0.40f, 0.30f); // Tanque de agua.
        r.caja(x - 1.6f, techo + 1.6f, z - 1.6f, 1.5f, 0.12f, 1.5f, 0.40f, 0.30f, 0.22f); // Tapa del tanque.
        r.caja(x + 1.5f, techo + 0.35f, z + 1.2f, 1.2f, 0.7f, 0.9f, 0.70f, 0.72f, 0.75f); // Equipo de aire acondicionado.
        r.caja(x + 1.5f, techo + 0.72f, z + 1.2f, 0.6f, 0.05f, 0.6f, 0.30f, 0.32f, 0.35f); // Rejilla del ventilador.
        if ((fila + columna) % 2 == 0) { // Uno de cada dos edificios tiene antena.
            r.caja(x + 1.8f, techo + 1.5f, z - 1.8f, 0.08f, 3.0f, 0.08f, 0.35f, 0.35f, 0.38f); // Mástil de la antena.
            if (r.noche) { // La luz de aviso parpadea de noche.
                r.entero("uEmision", (int) (r.tiempo * 2) % 2); // Enciende y apaga cada medio segundo.
            }
            r.caja(x + 1.8f, techo + 3.05f, z - 1.8f, 0.18f, 0.18f, 0.18f, 0.95f, 0.15f, 0.12f); // Luz roja de la antena.
            r.entero("uEmision", 0); // Restablece la iluminación normal.
        }
    }
}
