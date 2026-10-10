package com.graphics.ciudad; // Todo lo que forma la ciudad: mapa, manzanas, calles y mobiliario.

import com.graphics.motor.Renderizador; // Pincel con el que se dibuja cada caja.

/**
 * ÁRBOL: dos modelos hechos con cajas.
 * El grande (parques) tiene tronco alto y copa de tres niveles alternados a 45 grados, en tres
 * tamaños. El de calle es más delgado y va en un cantero. Ambos se mecen suavemente con el agua.
 */
public final class Arbol {

    private static final float CUARTO = (float) Math.PI / 4; // 45 grados, para alternar los niveles de las copas.

    /** Clase de utilidades: no se crean objetos Arbol. */
    private Arbol() {
    }

    /** Árbol grande: su tamaño y verde dependen de la posición, así cada parque se ve distinto. */
    public static void dibujar(Renderizador r, float x, float z) {
        int variante = (int) Math.abs(x * 7 + z * 13) % 3; // Variante fija según la posición: 0, 1 o 2.
        float escala = 1.0f + variante * 0.2f; // Árboles de tres tamaños.
        float vaiven = (float) Math.sin(r.tiempo * 1.3f + x * 0.3f + z * 0.2f) * 0.06f; // Balanceo suave de la copa.
        float verde = 0.38f + variante * 0.05f; // Cada variante tiene un verde algo distinto.
        r.caja(x, 0.3f + 1.2f * escala, z, 0.4f, 2.4f * escala, 0.4f, 0.40f, 0.25f, 0.14f); // Tronco.
        r.cajaGirada(x, 0.3f + 2.6f * escala, z, 2.6f, 1.3f, 2.6f, 0.12f, verde, 0.20f, vaiven); // Copa baja y ancha.
        r.cajaGirada(x, 0.3f + 3.5f * escala, z, 2.0f, 1.1f, 2.0f, 0.16f, verde + 0.08f, 0.22f, CUARTO + vaiven); // Copa media, girada.
        r.cajaGirada(x, 0.3f + 4.3f * escala, z, 1.2f, 0.8f, 1.2f, 0.22f, verde + 0.16f, 0.25f, vaiven); // Punta clara.
    }

    /** Árbol delgado para veredas y patios, dentro de un cantero. */
    public static void dibujarDeCalle(Renderizador r, float x, float z) {
        float vaiven = (float) Math.sin(r.tiempo * 1.3f + x * 0.3f + z * 0.2f) * 0.06f; // Balanceo de la copa.
        r.caja(x, 0.36f, z, 0.8f, 0.12f, 0.8f, 0.35f, 0.25f, 0.18f); // Cantero de tierra.
        r.caja(x, 1.4f, z, 0.25f, 2.2f, 0.25f, 0.40f, 0.25f, 0.14f); // Tronco.
        r.cajaGirada(x, 2.8f, z, 1.2f, 1.1f, 1.2f, 0.15f, 0.45f, 0.22f, vaiven); // Copa.
        r.cajaGirada(x, 3.5f, z, 0.8f, 0.7f, 0.8f, 0.22f, 0.55f, 0.26f, CUARTO + vaiven); // Punta.
    }
}
