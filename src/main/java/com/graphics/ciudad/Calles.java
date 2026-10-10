package com.graphics.ciudad; // Todo lo que forma la ciudad: mapa, manzanas, calles y mobiliario.

import com.graphics.motor.Renderizador; // Pincel con el que se dibuja cada caja.

/**
 * CALLES: pintura y detalles del asfalto.
 * Líneas amarillas centrales, pasos peatonales, bordillos de las aceras, líneas blancas de borde
 * y alcantarillas en los cruces. Todo es decorativo: no afecta las colisiones.
 */
public final class Calles {

    /** Clase de utilidades: no se crean objetos Calles. */
    private Calles() {
    }

    /** Dibuja las líneas discontinuas amarillas dejando los cruces despejados. */
    public static void dibujarMarcas(Renderizador r, int fila, int columna, float x, float z) {
        if (fila % 2 == 0 && columna % 2 == 1) { // Identifica un tramo horizontal situado entre cruces.
            for (int desplazamiento = -3; desplazamiento <= 3; desplazamiento += 3) { // Coloca tres marcas en la celda.
                r.caja(x + desplazamiento, 0.025f, z, 1.6f, 0.03f, 0.13f, 1, 0.84f, 0.35f); // Dibuja una línea alargada en X.
            }
        }
        if (columna % 2 == 0 && fila % 2 == 1) { // Identifica un tramo vertical situado entre cruces.
            for (int desplazamiento = -3; desplazamiento <= 3; desplazamiento += 3) { // Repite las marcas sobre ese tramo.
                r.caja(x, 0.025f, z + desplazamiento, 0.13f, 0.03f, 1.6f, 1, 0.84f, 0.35f); // Dibuja una línea alargada en Z.
            }
        }
    }

    /** Dibuja las franjas blancas sobre la calle contigua al norte de la manzana. */
    public static void dibujarPasoPeatonal(Renderizador r, float x, float z) {
        for (int desplazamiento = -3; desplazamiento <= 3; desplazamiento++) { // Coloca siete franjas paralelas.
            r.caja(x + desplazamiento, 0.045f, z - 8, 0.45f, 0.04f, 2, 0.85f, 0.87f, 0.83f); // Eleva la pintura un poco sobre el suelo.
        }
    }

    /** Bordillos en cada manzana, líneas blancas de borde en cada tramo y alcantarillas en los cruces. */
    public static void dibujarDetalles(Renderizador r) {
        float mitad = Mapa.CELDA / 2; // Distancia del centro de una celda a su borde.
        for (int fila = 0; fila < Mapa.MAPA.length; fila++) { // Recorre las filas.
            for (int columna = 0; columna < Mapa.MAPA[fila].length; columna++) { // Recorre las columnas.
                float x = Mapa.centro(columna); // Centro X de la celda.
                float z = Mapa.centro(fila); // Centro Z de la celda.
                if (Mapa.MAPA[fila][columna] != 0) { // Manzana: bordillo claro alrededor de la acera.
                    r.caja(x, 0.32f, z - mitad + 0.08f, Mapa.CELDA, 0.08f, 0.16f, 0.80f, 0.80f, 0.74f); // Bordillo norte.
                    r.caja(x, 0.32f, z + mitad - 0.08f, Mapa.CELDA, 0.08f, 0.16f, 0.80f, 0.80f, 0.74f); // Bordillo sur.
                    r.caja(x - mitad + 0.08f, 0.32f, z, 0.16f, 0.08f, Mapa.CELDA, 0.80f, 0.80f, 0.74f); // Bordillo oeste.
                    r.caja(x + mitad - 0.08f, 0.32f, z, 0.16f, 0.08f, Mapa.CELDA, 0.80f, 0.80f, 0.74f); // Bordillo este.
                    continue; // Las líneas de calle no van sobre manzanas.
                }
                boolean horizontal = fila % 2 == 0 && columna % 2 == 1; // Tramo que corre a lo largo de X.
                boolean vertical = columna % 2 == 0 && fila % 2 == 1; // Tramo que corre a lo largo de Z.
                if (horizontal) { // Líneas de borde junto a las dos aceras.
                    r.caja(x, 0.022f, z - 4.3f, Mapa.CELDA, 0.02f, 0.12f, 0.88f, 0.88f, 0.85f); // Borde norte.
                    r.caja(x, 0.022f, z + 4.3f, Mapa.CELDA, 0.02f, 0.12f, 0.88f, 0.88f, 0.85f); // Borde sur.
                }
                if (vertical) { // Líneas de borde a lo largo de Z.
                    r.caja(x - 4.3f, 0.022f, z, 0.12f, 0.02f, Mapa.CELDA, 0.88f, 0.88f, 0.85f); // Borde oeste.
                    r.caja(x + 4.3f, 0.022f, z, 0.12f, 0.02f, Mapa.CELDA, 0.88f, 0.88f, 0.85f); // Borde este.
                }
                if (fila % 2 == 0 && columna % 2 == 0 && (fila + columna) % 4 == 0) { // Cruce: alcantarilla en uno de cada dos.
                    r.cilindro(x + 2.6f, 0.02f, z + 2.6f, 0.9f, 0.03f, 0.22f, 0.23f, 0.25f); // Tapa redonda de alcantarilla.
                    r.caja(x + 2.6f, 0.04f, z + 2.6f, 0.7f, 0.02f, 0.08f, 0.32f, 0.33f, 0.35f); // Ranura de la tapa.
                }
            }
        }
    }
}
