package com.graphics.ciudad; // Todo lo que forma la ciudad: mapa, manzanas, calles y mobiliario.

import com.graphics.motor.Renderizador; // Pincel con el que se dibuja cada caja.

/**
 * FAROLA: farol clásico en la esquina noroeste de cada manzana.
 * POSICIONES se usa dos veces: aquí para dibujar el farol y en Iluminacion para enviar al shader
 * dónde está cada bombilla. Así la luz siempre sale del vidrio que se ve.
 */
public final class Farola {

    private static final float CUARTO = (float) Math.PI / 4; // 45 grados, para el techo en punta.

    /** Posición X, Y, Z de cada bombilla: una por manzana, 16 de ellas en los sectores nuevos. */
    public static final float[][] POSICIONES = crearPosiciones();

    /** Clase de utilidades: no se crean objetos Farola. */
    private Farola() {
    }

    /** Coloca una farola en la esquina noroeste de cada manzana, sobre la acera y junto a dos calles. */
    private static float[][] crearPosiciones() {
        int cantidad = 0; // Cuenta cuántas manzanas tiene el mapa.
        for (int[] filaMapa : Mapa.MAPA) { // Recorre cada fila del mapa.
            for (int tipo : filaMapa) { // Recorre cada celda de esa fila.
                if (tipo != 0) { // Toda celda que no es calle es una manzana.
                    cantidad++; // Reserva una farola para ella.
                }
            }
        }
        float[][] luces = new float[cantidad][]; // Crea el arreglo con el tamaño exacto.
        int indice = 0; // Señala la siguiente posición libre del arreglo.
        for (int fila = 0; fila < Mapa.MAPA.length; fila++) { // Recorre las filas de norte a sur.
            for (int columna = 0; columna < Mapa.MAPA[fila].length; columna++) { // Recorre las columnas de oeste a este.
                if (Mapa.MAPA[fila][columna] != 0) { // Solo las manzanas reciben farola.
                    float x = Mapa.centro(columna) - 4; // Ubica el poste a 4 unidades al oeste del centro.
                    float z = Mapa.centro(fila) - 4; // Ubica el poste a 4 unidades al norte del centro.
                    luces[indice] = new float[] {x, 4.5f, z}; // Guarda la bombilla a 4.5 unidades de altura.
                    indice++; // Avanza a la siguiente posición libre.
                }
            }
        }
        return luces; // Entrega las 25 farolas del mapa de 11 x 11.
    }

    /** Dibuja un farol con el vidrio en (x, y, z): base, poste, vidrio enmarcado y techo en punta. */
    public static void dibujar(Renderizador r, float x, float y, float z) {
        r.caja(x, 0.5f, z, 0.42f, 0.4f, 0.42f, 0.16f, 0.20f, 0.18f); // Base.
        r.caja(x, 0.95f, z, 0.26f, 0.12f, 0.26f, 0.16f, 0.20f, 0.18f); // Anillo decorativo.
        r.caja(x, 2.4f, z, 0.14f, 3.5f, 0.14f, 0.16f, 0.22f, 0.20f); // Poste verde oscuro.
        r.caja(x, y - 0.42f, z, 0.26f, 0.14f, 0.26f, 0.16f, 0.20f, 0.18f); // Soporte del farol.
        r.caja(x, y - 0.3f, z, 0.5f, 0.08f, 0.5f, 0.12f, 0.14f, 0.13f); // Base del farol.
        if (r.noche) { // Encendido solo de noche.
            r.entero("uEmision", 1); // El vidrio brilla con color propio.
            r.caja(x, y, z, 0.4f, 0.5f, 0.4f, 1.0f, 0.85f, 0.50f); // Vidrio iluminado.
            r.entero("uEmision", 0); // Restablece la iluminación normal.
        } else { // De día es un vidrio claro.
            r.caja(x, y, z, 0.4f, 0.5f, 0.4f, 0.80f, 0.88f, 0.90f); // Vidrio apagado.
        }
        for (float esquinaX : new float[] {-0.21f, 0.21f}) { // Marco metálico del farol.
            for (float esquinaZ : new float[] {-0.21f, 0.21f}) { // Cuatro aristas verticales.
                r.caja(x + esquinaX, y, z + esquinaZ, 0.05f, 0.52f, 0.05f, 0.12f, 0.14f, 0.13f); // Arista.
            }
        }
        r.caja(x, y + 0.3f, z, 0.62f, 0.08f, 0.62f, 0.12f, 0.14f, 0.13f); // Alero del techo.
        r.cajaGirada(x, y + 0.42f, z, 0.4f, 0.18f, 0.4f, 0.12f, 0.14f, 0.13f, CUARTO); // Techo en punta.
        r.caja(x, y + 0.58f, z, 0.06f, 0.14f, 0.06f, 0.12f, 0.14f, 0.13f); // Remate.
    }
}
