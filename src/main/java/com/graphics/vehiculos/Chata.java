package com.graphics.vehiculos; // Vehículos: el botemóvil del jugador, su chata y el tráfico autónomo.

import com.graphics.motor.Renderizador; // Pincel con el que se dibuja cada caja.

/**
 * CHATA: remolque enganchado detrás del auto.
 * No está pegada al auto: cuelga del ENGANCHE y gira sobre él como una bisagra. En cada cuadro
 * "mira" hacia el enganche desde donde estaba y se acerca hasta quedar a la distancia de la LANZA,
 * como un juguete arrastrado con una cuerda. Por eso, cuando el auto dobla, la chata lo sigue
 * con retraso y se va alineando mientras avanza.
 */
public class Chata {

    public static final float MEDIO_ANCHO = 0.95f; // Mitad del ancho, con las ruedas incluidas.
    public static final float MEDIO_LARGO = 1.0f; // Mitad del largo de la caja.
    private static final float ENGANCHE = 1.6f; // Del centro del auto al enganche: justo detrás de su parachoques.
    private static final float LANZA = 2.0f; // Del enganche al centro de la chata.
    public float x = Auto.INICIO_X; // Posición horizontal del centro de la chata.
    public float z = Auto.INICIO_Z + ENGANCHE + LANZA; // Empieza detrás del auto, que mira al norte.
    public float angulo = 0; // Orientación; su frente (-Z local) apunta al enganche.

    /** Coloca la chata recta detrás del auto (al reiniciar). */
    public void colocarDetras(float autoX, float autoZ, float autoAngulo) {
        x = autoX + (float) Math.sin(autoAngulo) * (ENGANCHE + LANZA); // Atrás del auto es (+sen, +cos).
        z = autoZ + (float) Math.cos(autoAngulo) * (ENGANCHE + LANZA); // Completa la posición.
        angulo = autoAngulo; // Alineada con el auto.
    }

    /**
     * Dónde quedaría la chata si el auto estuviera en (autoX, autoZ, autoAngulo). No la mueve:
     * así Auto puede preguntar antes de moverse si la chata cabría. Devuelve {x, z, angulo}.
     */
    public float[] posicionTras(float autoX, float autoZ, float autoAngulo) {
        float engancheX = autoX + (float) Math.sin(autoAngulo) * ENGANCHE; // Enganche detrás del auto.
        float engancheZ = autoZ + (float) Math.cos(autoAngulo) * ENGANCHE; // Completa el enganche.
        float dx = x - engancheX; // Dirección del enganche hacia donde está ahora la chata.
        float dz = z - engancheZ; // Completa la dirección.
        float largo = (float) Math.sqrt(dx * dx + dz * dz); // Distancia actual entre ambos.
        float nuevaX = engancheX + dx / largo * LANZA; // La chata queda en esa dirección, a la distancia de la lanza.
        float nuevaZ = engancheZ + dz / largo * LANZA; // Completa la nueva posición.
        float nuevoAngulo = (float) Math.atan2(dx, dz); // Su frente apunta al enganche.
        return new float[] {nuevaX, nuevaZ, nuevoAngulo}; // Posición propuesta.
    }

    /** Arrastra la chata detrás del auto (se llama después de que el auto se movió). */
    public void seguir(float autoX, float autoZ, float autoAngulo) {
        float[] nueva = posicionTras(autoX, autoZ, autoAngulo); // Calcula a dónde la lleva el enganche.
        x = nueva[0]; // Acepta la nueva X.
        z = nueva[1]; // Acepta la nueva Z.
        angulo = nueva[2]; // Acepta la nueva orientación.
    }

    /** Dibuja la chata: caja anaranjada con barandas, dos ruedas y la lanza hasta el enganche. */
    public void dibujar(Renderizador r) {
        r.origen(x, z, angulo); // Las piezas siguientes giran con la chata.
        r.parte(-0.85f, 0.35f, 0.2f, 0.2f, 0.6f, 0.6f, 0.06f, 0.07f, 0.08f); // Rueda izquierda.
        r.parte(0.85f, 0.35f, 0.2f, 0.2f, 0.6f, 0.6f, 0.06f, 0.07f, 0.08f); // Rueda derecha.
        r.parte(0, 0.65f, 0, 1.6f, 0.15f, 2.0f, 0.55f, 0.35f, 0.20f); // Piso de madera.
        r.parte(-0.75f, 0.95f, 0, 0.1f, 0.45f, 2.0f, 0.90f, 0.45f, 0.15f); // Baranda izquierda.
        r.parte(0.75f, 0.95f, 0, 0.1f, 0.45f, 2.0f, 0.90f, 0.45f, 0.15f); // Baranda derecha.
        r.parte(0, 0.95f, -0.95f, 1.6f, 0.45f, 0.1f, 0.90f, 0.45f, 0.15f); // Baranda delantera.
        r.parte(0, 0.95f, 0.95f, 1.6f, 0.45f, 0.1f, 0.90f, 0.45f, 0.15f); // Baranda trasera.
        r.parte(0, 0.55f, -1.5f, 0.12f, 0.1f, 1.0f, 0.30f, 0.30f, 0.32f); // Lanza: del frente de la caja al enganche.
    }
}
