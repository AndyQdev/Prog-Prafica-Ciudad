package com.graphics.motor; // Piezas técnicas que no saben nada de la ciudad: ventana, dibujo y cámara.

/**
 * TEXTO: escribe palabras con cajas usando la fuente de 5 x 7 cuadritos.
 * Sirve tanto para el panel de la pantalla como para los letreros dentro de la ciudad.
 */
public final class Texto {

    /** Clase de utilidades: no se crean objetos Texto. */
    private Texto() {
    }

    /**
     * Escribe un texto. (x, y, z) es la esquina inferior del primer carácter, p el tamaño de cada
     * cuadrito y sentido +1 o -1 la dirección en X en que avanzan las letras: +1 en la pantalla y
     * -1 en letreros que miran al norte, porque quien los lee mira hacia +Z.
     */
    public static void escribir(Renderizador r, String mensaje, float x, float y, float z, float p, float sentido,
                                float rojo, float verde, float azul) {
        for (int indice = 0; indice < mensaje.length(); indice++) { // Recorre los caracteres.
            String[] filas = Fuente.filas(mensaje.charAt(indice)); // Busca su dibujo.
            if (filas == null) { // Espacio o carácter sin dibujo.
                continue; // Solo deja el hueco.
            }
            for (int fila = 0; fila < 7; fila++) { // Recorre las filas de arriba hacia abajo.
                float filaY = y + (6 - fila) * p + p / 2; // La fila 0 queda arriba.
                int columna = 0; // Columna que se está revisando.
                while (columna < 5) { // Recorre la fila buscando tramos de unos.
                    if (filas[fila].charAt(columna) != '1') { // Cuadrito apagado.
                        columna++; // Pasa al siguiente.
                        continue; // Sigue buscando.
                    }
                    int fin = columna; // Busca dónde termina el tramo de unos.
                    while (fin < 5 && filas[fila].charAt(fin) == '1') { // Avanza mientras haya cuadritos encendidos.
                        fin++; // Extiende el tramo.
                    }
                    int largo = fin - columna; // Cantidad de cuadritos seguidos.
                    float local = (indice * 6 + columna + largo / 2f) * p; // Centro del tramo; cada letra ocupa 6 columnas.
                    r.caja(x + sentido * local, filaY, z, largo * p, p, p * 0.5f, rojo, verde, azul); // Un tramo = una caja.
                    columna = fin; // Continúa después del tramo.
                }
            }
        }
    }

    /** Ancho total de un texto: 6 cuadritos por letra, sin el espacio final. */
    public static float ancho(String mensaje, float p) {
        return (mensaje.length() * 6 - 1) * p; // Cinco columnas más una de separación por letra.
    }
}
