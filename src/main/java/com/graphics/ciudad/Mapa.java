package com.graphics.ciudad; // Todo lo que forma la ciudad: mapa, manzanas, calles y mobiliario.

/**
 * MAPA: la ciudad descrita como una matriz de números.
 * Cada celda mide 10 unidades. Las filas corresponden a Z (norte arriba = -Z) y las columnas a X.
 * Aquí también vive la geometría que todos usan: convertir celdas a coordenadas y saber si un
 * rectángulo (el auto) cabe en una posición sin tocar ninguna manzana ni salir del mapa.
 */
public final class Mapa {

    // ==================== 1. TIPOS DE CELDA ====================
    public static final int CALLE = 0; // Celda transitable.
    public static final int EDIFICIO = 1; // Edificio alto del centro.
    public static final int PARQUE = 2; // Parque con árboles, banco y medusas.
    public static final int CRUSTACEO = 3; // Crustáceo Cascarudo.
    public static final int BALDE = 4; // Balde de Carnada.
    public static final int PINA = 5; // Casa de Bob.
    public static final int MOAI = 6; // Casa de Calamardo.
    public static final int ROCA = 7; // Casa de Patricio.
    public static final int BARRIO = 8; // Manzana residencial con cuatro casas.

    // ==================== 2. LA CIUDAD ====================
    public static final float CELDA = 10; // Ancho y profundidad de cada celda.

    /*
     * Matriz de 11 x 11. El recuadro central (filas y columnas 2 a 8) es la ciudad original de 7 x 7:
     * es el centro con edificios altos, y los barrios residenciales lo rodean.
     * Las filas y columnas pares son calles; así todas las manzanas quedan rodeadas por la red vial.
     */
    public static final int[][] MAPA = {
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // Fila norte: Calle Concha, continua.
        {0, 8, 0, 8, 0, 5, 0, 6, 0, 7, 0}, // Barrios y casas de Bob (5), Calamardo (6) y Patricio (7).
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // Avenida que separa el sector norte de la ciudad original.
        {0, 8, 0, 1, 0, 1, 0, 2, 0, 8, 0}, // Primera fila original, con barrios en ambos extremos.
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // Avenida horizontal original.
        {0, 8, 0, 2, 0, 1, 0, 1, 0, 8, 0}, // Fila central original, con barrios en ambos extremos.
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // Avenida horizontal original.
        {0, 8, 0, 1, 0, 2, 0, 1, 0, 2, 0}, // Última fila original, con barrio al oeste y un parque al este.
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // Avenida donde arranca el auto, en X = -30 y Z = 30.
        {0, 3, 0, 4, 0, 8, 0, 8, 0, 8, 0}, // Crustáceo (3), Balde de Carnada (4) y tres barrios.
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} // Calle del borde sur.
    };

    /** Distancia del origen a cada borde: 11 celdas de 10 unidades dan 110, y la mitad es 55. */
    public static final float LIMITE = MAPA.length * CELDA / 2;

    /** Cantidad de manzanas (celdas que no son calle): cada una tiene farola y semáforo. */
    public static final int MANZANAS = contarManzanas();

    private static final float[][] FACHADAS = { // Ladrillo, crema, turquesa, arena, gris azulado y terracota.
        {0.62f, 0.30f, 0.25f}, {0.88f, 0.82f, 0.66f}, {0.30f, 0.58f, 0.62f}, {0.80f, 0.66f, 0.45f},
        {0.45f, 0.52f, 0.66f}, {0.75f, 0.45f, 0.32f}
    };

    /** Clase de utilidades: el mapa es único, no se crean objetos Mapa. */
    private Mapa() {
    }

    // ==================== 3. COORDENADAS ====================

    /** Convierte el índice de una fila o columna al centro de su celda. */
    public static float centro(int indice) {
        return -LIMITE + CELDA * (indice + 0.5f); // Parte del borde negativo y avanza hasta la mitad de la celda.
    }

    /** Altura de un edificio alto: varía de forma reproducible entre 5 y 13. */
    public static float alturaEdificio(int fila, int columna) {
        return 5 + (fila * 3 + columna * 7) % 9; // Misma fórmula del proyecto original.
    }

    /** Color de la fachada de un edificio, elegido de una paleta según su celda. */
    public static float[] colorEdificio(int fila, int columna) {
        float[] base = FACHADAS[(fila * 5 + columna * 3) % FACHADAS.length]; // Elige un color fijo por manzana.
        float matiz = 0.94f + (fila + columna) % 3 * 0.04f; // Pequeña variación de brillo entre vecinos del mismo color.
        return new float[] {base[0] * matiz, base[1] * matiz, base[2] * matiz}; // Color final de la fachada.
    }

    /** Cuenta las celdas que no son calle. */
    private static int contarManzanas() {
        int cantidad = 0; // Manzanas encontradas.
        for (int[] fila : Mapa.MAPA) { // Recorre las filas.
            for (int tipo : fila) { // Recorre las celdas.
                if (tipo != 0) { // No es calle.
                    cantidad++; // Suma una manzana.
                }
            }
        }
        return cantidad; // 25 en el mapa de 11 x 11.
    }

    // ==================== 4. COLISIONES ====================

    /**
     * Comprueba si un rectángulo (medioAncho x medioLargo, girado según giro) cabe en (x, z) sin tocar
     * manzanas ni salir del mapa. A diferencia de un círculo, tiene la forma real del auto: llega justo
     * hasta el bordillo por los costados y por el frente.
     */
    public static boolean puedeOcupar(float x, float z, float giro, float medioAncho, float medioLargo) {
        float coseno = Math.abs((float) Math.cos(giro)); // Cuánto se alinea el ancho del auto con el eje X.
        float seno = Math.abs((float) Math.sin(giro)); // Cuánto se alinea el largo del auto con el eje X.
        float alcanceX = coseno * medioAncho + seno * medioLargo; // Mitad de la sombra del auto sobre el eje X.
        float alcanceZ = seno * medioAncho + coseno * medioLargo; // Mitad de la sombra del auto sobre el eje Z.
        if (Math.abs(x) + alcanceX > LIMITE || Math.abs(z) + alcanceZ > LIMITE) { // Detecta salida por cualquier borde.
            return false; // Rechaza la posición exterior.
        }
        float mitad = CELDA / 2; // La acera ocupa la celda completa: 5 unidades a cada lado del centro.
        for (int fila = 0; fila < MAPA.length; fila++) { // Recorre las filas del mapa.
            for (int columna = 0; columna < MAPA[fila].length; columna++) { // Recorre las celdas de cada fila.
                if (MAPA[fila][columna] == CALLE) { // Las calles no son obstáculos.
                    continue; // Pasa a la siguiente celda.
                }
                if (chocaManzana(x, z, giro, medioAncho, medioLargo, centro(columna), centro(fila), mitad)) { // Prueba la manzana.
                    return false; // Rechaza el movimiento que produciría una colisión.
                }
            }
        }
        return true; // Acepta la posición porque no se encontró ningún obstáculo.
    }

    /**
     * Teorema del eje separador: dos rectángulos no se tocan si existe un eje en el que sus sombras no se cruzan.
     * Basta probar cuatro ejes: X y Z (lados de la manzana) y el ancho y el largo del auto.
     */
    private static boolean chocaManzana(float x, float z, float giro, float medioAncho, float medioLargo,
                                        float centroX, float centroZ, float mitad) {
        float distanciaX = x - centroX; // Separación horizontal entre centros.
        float distanciaZ = z - centroZ; // Separación en profundidad entre centros.
        float coseno = (float) Math.cos(giro); // Coseno del giro del auto.
        float seno = (float) Math.sin(giro); // Seno del giro del auto.
        float cosenoAbs = Math.abs(coseno); // Valores absolutos para medir sombras.
        float senoAbs = Math.abs(seno); // Valor absoluto del seno.
        if (Math.abs(distanciaX) >= mitad + cosenoAbs * medioAncho + senoAbs * medioLargo) { // Eje X separa.
            return false; // No hay choque.
        }
        if (Math.abs(distanciaZ) >= mitad + senoAbs * medioAncho + cosenoAbs * medioLargo) { // Eje Z separa.
            return false; // No hay choque.
        }
        float sobreAncho = Math.abs(distanciaX * coseno - distanciaZ * seno); // Distancia medida a lo ancho del auto.
        if (sobreAncho >= medioAncho + mitad * (cosenoAbs + senoAbs)) { // Eje del ancho separa.
            return false; // No hay choque.
        }
        float sobreLargo = Math.abs(distanciaX * seno + distanciaZ * coseno); // Distancia medida a lo largo del auto.
        if (sobreLargo >= medioLargo + mitad * (senoAbs + cosenoAbs)) { // Eje del largo separa.
            return false; // No hay choque.
        }
        return true; // Ningún eje los separa: los rectángulos se tocan.
    }
}
