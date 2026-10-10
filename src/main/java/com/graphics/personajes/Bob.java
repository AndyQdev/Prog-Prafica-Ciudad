package com.graphics.personajes; // Personajes y vida de la ciudad: Bob, vecinos famosos y animales marinos.

import com.graphics.motor.Renderizador; // Pincel con el que se dibuja cada caja.

/**
 * BOB ESPONJA: el conductor del botemóvil.
 * Auto lo dibuja después de fijar su origen, así cada pieza gira con el auto. Rebota más cuanto
 * más rápido va el bote.
 */
public final class Bob {

    /** Clase de utilidades: no se crean objetos Bob. */
    private Bob() {
    }

    /** Dibuja a Bob sentado en el asiento del auto actual; velocidad controla cuánto rebota. */
    public static void dibujar(Renderizador r, float velocidad) {
        float amplitud = 0.02f + Math.abs(velocidad) * 0.005f; // El rebote crece con la velocidad.
        float b = Math.abs((float) Math.sin(r.tiempo * 7)) * amplitud; // Rebote siempre hacia arriba.
        float z = 0.35f; // Bob se sienta detrás del volante.
        r.parte(0, 0.98f + b, z, 0.8f, 0.18f, 0.32f, 0.50f, 0.32f, 0.15f); // Pantalón marrón.
        r.parte(0, 1.1f + b, z, 0.82f, 0.1f, 0.32f, 0.95f, 0.95f, 0.95f); // Camisa blanca.
        r.parte(0, 1.06f + b, z - 0.165f, 0.1f, 0.18f, 0.02f, 0.85f, 0.10f, 0.10f); // Corbata roja.
        r.parte(0, 1.55f + b, z, 0.8f, 0.8f, 0.3f, 1.0f, 0.92f, 0.25f); // Cuerpo cuadrado amarillo.
        r.parte(0.25f, 1.78f + b, z - 0.16f, 0.12f, 0.12f, 0.02f, 0.70f, 0.66f, 0.15f); // Agujero de esponja.
        r.parte(-0.28f, 1.3f + b, z - 0.16f, 0.1f, 0.1f, 0.02f, 0.70f, 0.66f, 0.15f); // Otro agujero de esponja.
        float[] ojos = {-0.16f, 0.16f}; // Separación de los ojos.
        for (float x : ojos) { // Dibuja cada ojo por capas, de atrás hacia delante.
            r.parte(x, 1.68f + b, z - 0.16f, 0.26f, 0.28f, 0.02f, 1, 1, 1); // Blanco del ojo.
            r.parte(x, 1.66f + b, z - 0.175f, 0.13f, 0.13f, 0.02f, 0.30f, 0.60f, 0.95f); // Iris celeste.
            r.parte(x, 1.66f + b, z - 0.19f, 0.06f, 0.06f, 0.02f, 0, 0, 0); // Pupila negra.
        }
        r.parte(0, 1.55f + b, z - 0.23f, 0.1f, 0.1f, 0.16f, 1.0f, 0.92f, 0.25f); // Nariz que sobresale.
        r.parte(0, 1.36f + b, z - 0.16f, 0.4f, 0.05f, 0.02f, 0.55f, 0.10f, 0.10f); // Sonrisa.
        r.parte(-0.06f, 1.31f + b, z - 0.165f, 0.08f, 0.08f, 0.02f, 1, 1, 1); // Diente izquierdo.
        r.parte(0.06f, 1.31f + b, z - 0.165f, 0.08f, 0.08f, 0.02f, 1, 1, 1); // Diente derecho.
        r.parte(-0.42f, 1.25f + b, 0.1f, 0.1f, 0.1f, 0.45f, 1.0f, 0.92f, 0.25f); // Brazo izquierdo hacia el volante.
        r.parte(0.42f, 1.25f + b, 0.1f, 0.1f, 0.1f, 0.45f, 1.0f, 0.92f, 0.25f); // Brazo derecho hacia el volante.
    }
}
