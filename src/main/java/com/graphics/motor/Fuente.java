package com.graphics.motor; // Piezas técnicas que no saben nada de la ciudad: ventana, dibujo y cámara.

import java.util.HashMap; // Permite buscar el dibujo de cada carácter.
import java.util.Map; // Define el tipo del diccionario.

/**
 * FUENTE DE 5 x 7 CUADRITOS.
 * Cada letra es una pequeña matriz, igual que el MAPA de la ciudad: 1 = cuadrito encendido, 0 = vacío.
 * Las 7 filas van de arriba hacia abajo y se separan con espacios. Texto dibuja cada tramo de
 * unos como una caja (ver Texto), así que no se necesitan imágenes ni bibliotecas de texto.
 * Incluye las letras A-Z (sin Ñ ni tildes), los dígitos y algunos signos; lo demás se dibuja como espacio.
 */
public class Fuente {

    private static final Map<Character, String> LETRAS = new HashMap<>(); // Carácter → filas de cuadritos.

    static { // Se ejecuta una vez, al usar la clase por primera vez.
        LETRAS.put('A', "01110 10001 10001 11111 10001 10001 10001"); // A
        LETRAS.put('B', "11110 10001 10001 11110 10001 10001 11110"); // B
        LETRAS.put('C', "01110 10001 10000 10000 10000 10001 01110"); // C
        LETRAS.put('D', "11110 10001 10001 10001 10001 10001 11110"); // D
        LETRAS.put('E', "11111 10000 10000 11110 10000 10000 11111"); // E
        LETRAS.put('F', "11111 10000 10000 11110 10000 10000 10000"); // F
        LETRAS.put('G', "01110 10001 10000 10111 10001 10001 01111"); // G
        LETRAS.put('H', "10001 10001 10001 11111 10001 10001 10001"); // H
        LETRAS.put('I', "01110 00100 00100 00100 00100 00100 01110"); // I
        LETRAS.put('J', "00111 00010 00010 00010 00010 10010 01100"); // J
        LETRAS.put('K', "10001 10010 10100 11000 10100 10010 10001"); // K
        LETRAS.put('L', "10000 10000 10000 10000 10000 10000 11111"); // L
        LETRAS.put('M', "10001 11011 10101 10101 10001 10001 10001"); // M
        LETRAS.put('N', "10001 11001 10101 10011 10001 10001 10001"); // N
        LETRAS.put('O', "01110 10001 10001 10001 10001 10001 01110"); // O
        LETRAS.put('P', "11110 10001 10001 11110 10000 10000 10000"); // P
        LETRAS.put('Q', "01110 10001 10001 10001 10101 10010 01101"); // Q
        LETRAS.put('R', "11110 10001 10001 11110 10100 10010 10001"); // R
        LETRAS.put('S', "01111 10000 10000 01110 00001 00001 11110"); // S
        LETRAS.put('T', "11111 00100 00100 00100 00100 00100 00100"); // T
        LETRAS.put('U', "10001 10001 10001 10001 10001 10001 01110"); // U
        LETRAS.put('V', "10001 10001 10001 10001 10001 01010 00100"); // V
        LETRAS.put('W', "10001 10001 10001 10101 10101 10101 01010"); // W
        LETRAS.put('X', "10001 10001 01010 00100 01010 10001 10001"); // X
        LETRAS.put('Y', "10001 10001 01010 00100 00100 00100 00100"); // Y
        LETRAS.put('Z', "11111 00001 00010 00100 01000 10000 11111"); // Z
        LETRAS.put('0', "01110 10001 10001 10001 10001 10001 01110"); // 0
        LETRAS.put('1', "00100 01100 00100 00100 00100 00100 01110"); // 1
        LETRAS.put('2', "01110 10001 00001 00010 00100 01000 11111"); // 2
        LETRAS.put('3', "11111 00010 00100 00010 00001 10001 01110"); // 3
        LETRAS.put('4', "00010 00110 01010 10010 11111 00010 00010"); // 4
        LETRAS.put('5', "11111 10000 11110 00001 00001 10001 01110"); // 5
        LETRAS.put('6', "00110 01000 10000 11110 10001 10001 01110"); // 6
        LETRAS.put('7', "11111 00001 00010 00100 01000 01000 01000"); // 7
        LETRAS.put('8', "01110 10001 10001 01110 10001 10001 01110"); // 8
        LETRAS.put('9', "01110 10001 10001 01111 00001 00010 01100"); // 9
        LETRAS.put(':', "00000 01100 01100 00000 01100 01100 00000"); // Dos puntos.
        LETRAS.put('/', "00000 00001 00010 00100 01000 10000 00000"); // Barra.
        LETRAS.put('!', "00100 00100 00100 00100 00100 00000 00100"); // Exclamación.
        LETRAS.put('-', "00000 00000 00000 11111 00000 00000 00000"); // Guion.
        LETRAS.put('.', "00000 00000 00000 00000 00000 01100 01100"); // Punto.
    }

    /** Devuelve las 7 filas del carácter, o null si no está definido (se dibuja como espacio). */
    public static String[] filas(char caracter) {
        String dibujo = LETRAS.get(Character.toUpperCase(caracter)); // Las minúsculas usan el dibujo de la mayúscula.
        if (dibujo == null) { // Espacio u otro carácter sin dibujo.
            return null; // Indica que no hay cuadritos que dibujar.
        }
        return dibujo.split(" "); // Separa las siete filas.
    }
}
