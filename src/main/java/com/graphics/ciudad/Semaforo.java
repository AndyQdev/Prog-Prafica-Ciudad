package com.graphics.ciudad; // Todo lo que forma la ciudad: mapa, manzanas, calles y mobiliario.

import com.graphics.motor.Renderizador; // Pincel con el que se dibuja cada caja.

/**
 * SEMÁFORO: uno por manzana, en su esquina noreste, con dos cabezales.
 * El cabezal alto controla la dirección norte-sur y el bajo la este-oeste; ambos tienen luces en sus
 * dos caras para verse desde cualquier lado. Ciclo de 14 segundos: verde 5, amarillo 2, rojo 7,
 * con las dos direcciones desfasadas para que nunca estén ambas en verde.
 * De noche cada cabezal tiñe su esquina con su color (luces de color del shader).
 */
public final class Semaforo {

    private static final float[][] COLORES_SEMAFORO = { // Índice 0 rojo, 1 amarillo, 2 verde.
        {1.0f, 0.12f, 0.08f}, {1.0f, 0.70f, 0.05f}, {0.10f, 1.0f, 0.35f}
    };
    private static final float CICLO_SEMAFORO = 14; // Segundos de un ciclo completo.

    /** Clase de utilidades: no se crean objetos Semaforo. */
    private Semaforo() {
    }

    /**
     * Color encendido en un segundo cualquiera: 0 rojo, 1 amarillo o 2 verde.
     * Es estático y no depende de la ventana para poder probarlo en los tests.
     */
    public static int fase(float segundo, boolean norteSur) {
        float t = segundo % CICLO_SEMAFORO; // Segundo actual dentro del ciclo.
        if (!norteSur) { // La otra dirección.
            t = (t + CICLO_SEMAFORO / 2) % CICLO_SEMAFORO; // Medio ciclo de desfase.
        }
        if (t < 5) { // Primeros cinco segundos.
            return 2; // Verde.
        }
        if (t < 7) { // Dos segundos siguientes.
            return 1; // Amarillo.
        }
        return 0; // Resto del ciclo: rojo.
    }

    /** Color encendido ahora, según el tiempo de animación del renderizador (se detiene en pausa). */
    private static int luzEncendida(Renderizador r, boolean norteSur) {
        return fase(r.tiempo, norteSur); // Aplica la regla al tiempo actual.
    }

    /** Dibuja el semáforo en (x, z): base, poste y dos cabezales. */
    public static void dibujar(Renderizador r, float x, float z) {
        r.caja(x, 0.45f, z, 0.34f, 0.3f, 0.34f, 0.25f, 0.27f, 0.30f); // Base de concreto.
        r.caja(x, 2.3f, z, 0.14f, 4.0f, 0.14f, 0.22f, 0.24f, 0.26f); // Poste.
        dibujarCabezal(r, x, 3.95f, z, true); // Cabezal alto: luces hacia el norte y el sur.
        dibujarCabezal(r, x, 2.6f, z, false); // Cabezal bajo: luces hacia el este y el oeste.
    }

    /** Cabezal con placa amarilla, carcasa negra y tres lámparas con visera en sus dos caras. */
    private static void dibujarCabezal(Renderizador r, float x, float y, float z, boolean norteSur) {
        if (norteSur) { // Las caras con luces miran a ±Z.
            r.caja(x, y, z, 0.62f, 1.36f, 0.24f, 0.95f, 0.75f, 0.10f); // Placa reflectante amarilla.
            r.caja(x, y, z, 0.44f, 1.22f, 0.38f, 0.08f, 0.09f, 0.10f); // Carcasa negra.
        } else { // Las caras con luces miran a ±X.
            r.caja(x, y, z, 0.24f, 1.36f, 0.62f, 0.95f, 0.75f, 0.10f); // Placa reflectante amarilla.
            r.caja(x, y, z, 0.38f, 1.22f, 0.44f, 0.08f, 0.09f, 0.10f); // Carcasa negra.
        }
        int encendida = luzEncendida(r, norteSur); // Lámpara activa de esta dirección.
        for (int lampara = 0; lampara < 3; lampara++) { // Rojo arriba, amarillo al centro y verde abajo.
            float alturaLampara = y + 0.38f - lampara * 0.38f; // Separa las tres lámparas.
            float[] colorBase = COLORES_SEMAFORO[lampara]; // Color propio de la lámpara.
            float brillo = lampara == encendida ? 1 : 0.2f; // Apagada se ve como un vidrio de color oscuro.
            for (float lado : new float[] {-1, 1}) { // Las dos caras del cabezal.
                r.entero("uEmision", 1); // El vidrio de color no depende de la iluminación.
                if (norteSur) { // Lámpara en la cara norte o sur.
                    r.caja(x, alturaLampara, z + lado * 0.2f, 0.28f, 0.28f, 0.04f, colorBase[0] * brillo, colorBase[1] * brillo, colorBase[2] * brillo); // Lámpara.
                } else { // Lámpara en la cara este u oeste.
                    r.caja(x + lado * 0.2f, alturaLampara, z, 0.04f, 0.28f, 0.28f, colorBase[0] * brillo, colorBase[1] * brillo, colorBase[2] * brillo); // Lámpara.
                }
                r.entero("uEmision", 0); // Restablece la iluminación normal.
                if (norteSur) { // Visera que hace sombra sobre la lámpara.
                    r.caja(x, alturaLampara + 0.17f, z + lado * 0.3f, 0.34f, 0.04f, 0.2f, 0.06f, 0.06f, 0.07f); // Visera norte o sur.
                } else { // Visera en la cara este u oeste.
                    r.caja(x + lado * 0.3f, alturaLampara + 0.17f, z, 0.2f, 0.04f, 0.34f, 0.06f, 0.06f, 0.07f); // Visera este u oeste.
                }
            }
        }
    }

    /** Dos luces de color por semáforo: una por cabezal. El shader se compila con este tamaño. */
    public static int cantidadLuces() {
        return Mapa.MANZANAS * 2; // Cada manzana tiene un semáforo con dos cabezales.
    }

    /** Envía al shader la posición de cada cabezal y el color de su lámpara encendida. */
    public static void enviarLuces(Renderizador r) {
        int indice = 0; // Posición libre del arreglo GLSL.
        for (int fila = 0; fila < Mapa.MAPA.length; fila++) { // Recorre las filas.
            for (int columna = 0; columna < Mapa.MAPA[fila].length; columna++) { // Recorre las columnas.
                if (Mapa.MAPA[fila][columna] == 0) { // Las calles no tienen semáforo.
                    continue; // Pasa a la siguiente celda.
                }
                float x = Mapa.centro(columna) + 4; // El semáforo está en la esquina noreste (lo coloca Ciudad).
                float z = Mapa.centro(fila) - 4; // Completa la posición de la esquina.
                float[] norteSur = COLORES_SEMAFORO[luzEncendida(r, true)]; // Color del cabezal alto.
                float[] esteOeste = COLORES_SEMAFORO[luzEncendida(r, false)]; // Color del cabezal bajo.
                r.vector("uColorPos[" + indice + "]", x, 3.95f, z); // Posición del cabezal alto.
                r.vector("uColorLuz[" + indice + "]", norteSur[0], norteSur[1], norteSur[2]); // Su color.
                indice++; // Siguiente luz.
                r.vector("uColorPos[" + indice + "]", x, 2.6f, z); // Posición del cabezal bajo.
                r.vector("uColorLuz[" + indice + "]", esteOeste[0], esteOeste[1], esteOeste[2]); // Su color.
                indice++; // Siguiente luz.
            }
        }
    }
}
