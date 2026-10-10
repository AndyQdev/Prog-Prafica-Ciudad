package com.graphics.ciudad; // Todo lo que forma la ciudad: mapa, manzanas, calles y mobiliario.

import com.graphics.motor.Renderizador; // Pincel con el que se dibuja cada caja.

/**
 * PARQUE: césped con cuatro árboles y un banco en el centro.
 * Las medusas que flotan encima las dibuja VidaMarina.
 */
public final class Parque {

    /** Clase de utilidades: no se crean objetos Parque. */
    private Parque() {
    }

    /** Dibuja el parque con centro en (x, z). */
    public static void dibujar(Renderizador r, float x, float z) {
        r.caja(x, 0.32f, z, 9, 0.1f, 9, 0.20f, 0.45f, 0.28f); // Césped verde sobre la acera.
        if (r.vistaMapa) { // En el minimapa basta con el césped.
            return; // Omite árboles y banco.
        }
        float[] posiciones = {-2.5f, 2.5f}; // Define desplazamientos respecto al centro de la parcela.
        for (float desplazamientoX : posiciones) { // Selecciona el lado izquierdo o derecho del parque.
            for (float desplazamientoZ : posiciones) { // Selecciona el lado delantero o trasero.
                float arbolX = x + desplazamientoX; // Convierte el desplazamiento local en coordenada X mundial.
                float arbolZ = z + desplazamientoZ; // Convierte el desplazamiento local en coordenada Z mundial.
                Arbol.dibujar(r, arbolX, arbolZ); // Planta un árbol en esa esquina del parque.
            }
        }
        r.caja(x, 0.65f, z, 3, 0.25f, 0.8f, 0.55f, 0.30f, 0.13f); // Dibuja el asiento de madera del banco.
        r.caja(x, 0.4f, z, 2, 0.6f, 0.35f, 0.22f, 0.24f, 0.24f); // Dibuja el soporte oscuro del banco.
        r.caja(x, 1, z + 0.35f, 3, 0.7f, 0.15f, 0.55f, 0.30f, 0.13f); // Dibuja el respaldo detrás del asiento.
    }
}
