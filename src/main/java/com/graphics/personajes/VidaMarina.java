package com.graphics.personajes; // Personajes y vida de la ciudad: Bob, vecinos famosos y animales marinos.

import com.graphics.ciudad.Mapa; // Las medusas flotan sobre los parques.
import com.graphics.motor.Renderizador; // Pincel con el que se dibuja cada caja.

/**
 * VIDA MARINA: lo que hace sentir que la ciudad está bajo el agua.
 * Dos medusas por parque que giran, flotan, laten y brillan de noche, y 40 burbujas que suben.
 */
public final class VidaMarina {

    /** Clase de utilidades: no se crean objetos VidaMarina. */
    private VidaMarina() {
    }

    /** Dibuja medusas y burbujas; no aparecen en el minimapa. */
    public static void dibujar(Renderizador r) {
        if (r.vistaMapa) { // Demasiado pequeñas para el minimapa.
            return; // No dibuja nada.
        }
        dibujarMedusas(r); // Medusas sobre los parques.
        dibujarBurbujas(r); // Burbujas por toda la ciudad.
    }

    /** Dos medusas por parque que giran, flotan y laten; de noche brillan como en los Campos de Medusas. */
    private static void dibujarMedusas(Renderizador r) {
        if (r.noche) { // Las medusas son bioluminiscentes.
            r.entero("uEmision", 1); // Su color se ve completo en la oscuridad.
        }
        for (int fila = 0; fila < Mapa.MAPA.length; fila++) { // Recorre las filas del mapa.
            for (int columna = 0; columna < Mapa.MAPA[fila].length; columna++) { // Recorre las columnas.
                if (Mapa.MAPA[fila][columna] != 2) { // Solo los parques tienen medusas.
                    continue; // Pasa a la siguiente celda.
                }
                for (int medusa = 0; medusa < 2; medusa++) { // Dos medusas en lados opuestos.
                    float fase = fila * 1.3f + columna * 0.7f + medusa * (float) Math.PI; // Desfase propio.
                    float x = Mapa.centro(columna) + (float) Math.cos(r.tiempo * 0.6f + fase) * 2.8f; // Órbita en X.
                    float z = Mapa.centro(fila) + (float) Math.sin(r.tiempo * 0.6f + fase) * 2.8f; // Órbita en Z.
                    float y = 6.2f + (float) Math.sin(r.tiempo * 1.7f + fase) * 0.6f; // Flota arriba y abajo, sobre las copas.
                    float pulso = 1 + (float) Math.sin(r.tiempo * 5 + fase) * 0.15f; // Late al nadar.
                    r.caja(x, y, z, 0.9f * pulso, 0.4f, 0.9f * pulso, 1.0f, 0.55f, 0.85f); // Campana rosada.
                    r.caja(x, y + 0.3f, z, 0.6f * pulso, 0.25f, 0.6f * pulso, 1.0f, 0.70f, 0.92f); // Cúpula superior.
                    float[] tentaculos = {-0.25f, 0.25f}; // Posiciones de los tentáculos.
                    for (float tx : tentaculos) { // Recorre izquierda y derecha.
                        for (float tz : tentaculos) { // Recorre delante y detrás.
                            r.caja(x + tx, y - 0.55f, z + tz, 0.06f, 0.7f / pulso, 0.06f, 1.0f, 0.55f, 0.85f); // Se estiran al contraerse.
                        }
                    }
                }
            }
        }
        r.entero("uEmision", 0); // Restablece la iluminación normal.
    }

    /** Burbujas que suben desde el fondo y reaparecen abajo; las posiciones son fijas pero parecen al azar. */
    private static void dibujarBurbujas(Renderizador r) {
        r.entero("uEmision", 1); // Las burbujas no se oscurecen por los lados: siempre parecen brillantes.
        float lado = Mapa.LIMITE * 2; // Las burbujas cubren todo el mapa.
        for (int indice = 0; indice < 40; indice++) { // Cuarenta burbujas.
            float x = (indice * 7919) % (int) lado - Mapa.LIMITE; // Multiplicar por un primo reparte las posiciones.
            float z = (indice * 104729) % (int) lado - Mapa.LIMITE; // Otro primo para Z.
            float rapidez = 1 + (indice % 5) * 0.3f; // Cada burbuja sube a su propio ritmo.
            float y = (r.tiempo * rapidez + indice * 1.7f) % 18; // Sube hasta 18 unidades y vuelve a empezar.
            x += (float) Math.sin(r.tiempo * 2 + indice) * 0.3f; // Oscila de lado a lado al subir.
            float tamano = 0.25f + (indice % 3) * 0.1f; // Tres tamaños distintos.
            float brillo = 0.95f; // De día las burbujas se ven casi blancas.
            if (r.noche) { // De noche se ven tenues pero visibles.
                brillo = 0.5f; // Atenúa el color.
            }
            r.cajaGirada(x, y, z, tamano, tamano, tamano, brillo * 0.85f, brillo * 0.95f, brillo, r.tiempo + indice); // Burbuja.
        }
        r.entero("uEmision", 0); // Restablece la iluminación normal.
    }
}
