package com.graphics.ciudad; // Todo lo que forma la ciudad: mapa, manzanas, calles y mobiliario.

import com.graphics.motor.Renderizador; // Pincel con el que se dibuja cada caja.
import com.graphics.personajes.Personajes; // Los vecinos famosos que viven en los lugares de la serie.

/**
 * CIUDAD: recorre el Mapa y dibuja cada celda delegando en el objeto que corresponde.
 * Calle → Calles. Edificio → Edificio. Parque → Parque. Barrio → Barrio. Lugares de la serie → LugaresBikini.
 * Además, cada manzana recibe un paso peatonal, un semáforo y una farola.
 */
public class Ciudad {

    private final LugaresBikini lugares; // Crustáceo, Balde, piña, moai y roca, con sus habitantes.

    /** Crea la ciudad; los personajes se dibujan dentro de sus lugares. */
    public Ciudad(Personajes personajes) {
        lugares = new LugaresBikini(personajes); // Los lugares necesitan saber a quién dibujar.
    }

    /** Dibuja el suelo, todas las celdas, sus señales y las farolas. */
    public void dibujar(Renderizador r) {
        float lado = Mapa.LIMITE * 2; // El suelo cubre todo el mapa: 110 unidades por lado.
        r.caja(0, -0.25f, 0, lado, 0.5f, lado, 0.16f, 0.19f, 0.23f); // Base de asfalto con su cara superior en Y = 0.
        for (int fila = 0; fila < Mapa.MAPA.length; fila++) { // Recorre el mapa de norte a sur.
            for (int columna = 0; columna < Mapa.MAPA[fila].length; columna++) { // Recorre cada fila de oeste a este.
                float x = Mapa.centro(columna); // Convierte la columna a posición X.
                float z = Mapa.centro(fila); // Convierte la fila a posición Z.
                int tipo = Mapa.MAPA[fila][columna]; // Lee qué hay en la celda.
                if (tipo == Mapa.CALLE) { // Celda transitable.
                    Calles.dibujarMarcas(r, fila, columna, x, z); // Líneas amarillas entre los cruces.
                    continue; // Las calles no tienen más contenido.
                }
                r.caja(x, 0.15f, z, Mapa.CELDA, 0.3f, Mapa.CELDA, 0.60f, 0.64f, 0.66f); // Acera elevada de la manzana.
                if (tipo == Mapa.EDIFICIO) { // Edificio alto del centro.
                    Edificio.dibujar(r, fila, columna, x, z); // Cuerpo, ventanas, tienda y techo.
                } else if (tipo == Mapa.PARQUE) { // Parque.
                    Parque.dibujar(r, x, z); // Césped, árboles y banco.
                } else if (tipo == Mapa.BARRIO) { // Manzana residencial.
                    Barrio.dibujar(r, x, z); // Cuatro casas con jardín y cerca.
                } else { // Tipos 3 a 7: lugares de la serie.
                    lugares.dibujar(r, tipo, x, z); // Edificio temático, letrero y personaje.
                }
                if (!r.vistaMapa) { // Las señales pequeñas no aparecen en el minimapa.
                    Calles.dibujarPasoPeatonal(r, x, z); // Cruce pintado en la calle norte de la manzana.
                    Semaforo.dibujar(r, x + 4, z - 4); // Semáforo en la esquina noreste.
                }
            }
        }
        if (!r.vistaMapa) { // Detalles de las calles solo en la vista principal.
            Calles.dibujarDetalles(r); // Bordillos, líneas de borde y alcantarillas.
        }
        for (float[] farola : Farola.POSICIONES) { // Una farola en la esquina noroeste de cada manzana.
            Farola.dibujar(r, farola[0], farola[1], farola[2]); // El vidrio queda donde el shader calcula la luz.
        }
    }
}
