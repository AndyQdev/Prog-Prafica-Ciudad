package com.graphics.ciudad; // Todo lo que forma la ciudad: mapa, manzanas, calles y mobiliario.

import com.graphics.motor.Renderizador; // Pincel con el que se dibuja cada caja.

/**
 * BARRIO: manzana residencial (tipo 8 del Mapa).
 * Cuatro casas distintas en sus lotes, césped, cerca con un portón frente a cada puerta,
 * un árbol central y setos. Cada barrio elige colores y estilos con una "semilla" fija.
 */
public final class Barrio {

    private static final float CERCA = 4.2f; // Distancia del centro de la manzana a la cerca.
    private static final float[][] CERCAS = { // Cerca blanca, de madera o celeste.
        {0.94f, 0.94f, 0.90f}, {0.62f, 0.42f, 0.25f}, {0.72f, 0.82f, 0.90f}
    };

    /** Clase de utilidades: no se crean objetos Barrio. */
    private Barrio() {
    }

    /** Dibuja el barrio con centro en (x, z). */
    public static void dibujar(Renderizador r, float x, float z) {
        int semilla = (int) Math.abs(x * 3 + z * 7); // Número fijo por manzana para variar colores y estilos.
        float cesped = 0.55f + (semilla % 3) * 0.03f; // Cada barrio tiene un verde algo distinto.
        r.caja(x, 0.33f, z, CERCA * 2, 0.06f, CERCA * 2, 0.33f, cesped, 0.28f); // Césped dentro de la cerca.
        float[] lados = {-2.1f, 2.1f}; // Centros de los cuatro lotes.
        int indice = 0; // Cuenta las casas para que ninguna sea igual a su vecina.
        for (float loteZ : lados) { // Fila de casas del norte y del sur.
            for (float loteX : lados) { // Casa del oeste y del este.
                float frente = Math.signum(loteZ); // Las casas del norte miran al norte (-1) y las del sur al sur (+1).
                Casa.dibujar(r, x + loteX, z + loteZ, frente, semilla + indice * 7); // Dibuja la casa del lote.
                indice++; // Pasa a la siguiente combinación.
            }
        }
        if (r.vistaMapa) { // En el minimapa bastan los techos.
            return; // Omite cercas y jardines.
        }
        dibujarCerca(r, x, z, CERCAS[semilla % CERCAS.length]); // Cerca blanca, de madera o celeste según el barrio.
        Arbol.dibujarDeCalle(r, x, z); // Árbol delgado en el centro, entre las cuatro casas.
        r.caja(x - 3.85f, 0.65f, z, 0.6f, 0.6f, 1.6f, 0.20f, 0.48f, 0.22f); // Seto del lado oeste.
        r.caja(x + 3.85f, 0.65f, z, 0.6f, 0.6f, 1.6f, 0.20f, 0.48f, 0.22f); // Seto del lado este.
    }

    /** Cerca alrededor del barrio, con un portón frente a cada puerta (norte y sur). */
    private static void dibujarCerca(Renderizador r, float x, float z, float[] color) {
        float[] alturas = {0.6f, 0.88f}; // Alturas de los dos travesaños.
        for (float lado : new float[] {-CERCA, CERCA}) { // Lados oeste y este, sin portones.
            for (float altura : alturas) { // Dos travesaños.
                r.caja(x + lado, altura, z, 0.06f, 0.07f, CERCA * 2, color[0], color[1], color[2]); // Travesaño a lo largo de Z.
            }
            for (float paso = -CERCA; paso <= CERCA; paso += 0.7f) { // Postes cada 0.7 unidades.
                r.caja(x + lado, 0.72f, z + paso, 0.1f, 0.72f, 0.1f, color[0], color[1], color[2]); // Poste.
            }
        }
        float[][] tramos = {{-CERCA, -3.1f}, {-2.1f, 1.1f}, {2.1f, CERCA}}; // Tramos entre los portones (puertas en -2.6 y 1.6).
        for (float lado : new float[] {-CERCA, CERCA}) { // Lados norte y sur.
            for (float[] tramo : tramos) { // Cada tramo continuo de cerca.
                float largo = tramo[1] - tramo[0]; // Largo del tramo.
                float medio = (tramo[0] + tramo[1]) / 2; // Centro del tramo.
                for (float altura : alturas) { // Dos travesaños.
                    r.caja(x + medio, altura, z + lado, largo, 0.07f, 0.06f, color[0], color[1], color[2]); // Travesaño a lo largo de X.
                }
                for (float paso = tramo[0]; paso <= tramo[1] + 0.01f; paso += Math.max(0.5f, largo / 4)) { // Postes del tramo.
                    r.caja(x + paso, 0.72f, z + lado, 0.1f, 0.72f, 0.1f, color[0], color[1], color[2]); // Poste.
                }
            }
        }
    }
}
