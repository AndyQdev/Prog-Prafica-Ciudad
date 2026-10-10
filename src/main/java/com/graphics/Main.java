package com.graphics; // Paquete raíz: el juego y su punto de entrada.

/**
 * MAIN: punto de entrada del programa.
 * Solo crea el Juego y lo pone en marcha; todo lo demás ocurre dentro de Juego.
 */
public class Main {

    /** Arranca Fondo de Bikini. */
    public static void main(String[] args) {
        new Juego().run(); // Crea el juego y ejecuta su ciclo hasta que se cierre la ventana.
    }
}
