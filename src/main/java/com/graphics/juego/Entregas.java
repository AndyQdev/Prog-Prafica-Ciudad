package com.graphics.juego; // Reglas del juego: iluminación y entregas.

import com.graphics.motor.Renderizador; // Pincel con el que se dibuja la marca del destino.
import com.graphics.vehiculos.Auto; // Para saber si el auto llegó y frenó.

/**
 * ENTREGAS: el objetivo del juego.
 * Cinco Cangreburgers que se entregan en orden. Una entrega se completa al llegar a menos de 3
 * unidades del destino activo y frenar a menos de 1 unidad por segundo. Lleva el cronómetro y
 * dibuja la marca dorada con su baliza giratoria.
 */
public class Entregas {

    public static final float[][] DESTINOS = { // Cada fila contiene X y Z de una parada sobre la calle.
        {0, -50}, // Primera: Calle Concha, frente a la casa de Bob.
        {20, -50}, // Segunda: Calle Concha, frente a la casa de Calamardo.
        {40, -50}, // Tercera: Calle Concha, frente a la roca de Patricio.
        {-20, 30}, // Cuarta: frente al Balde de Carnada.
        {-40, 30} // Quinta: regreso al Crustáceo Cascarudo, junto al inicio.
    };
    public static final String[] NOMBRES = {"BOB", "CALAMARDO", "PATRICIO", "BALDE DE CARNADA", "CRUSTACEO"}; // Para el panel.

    public int entregas = 0; // Entregas completadas; también es el índice del destino activo.
    public float tiempo = 0; // Segundos de la partida; se detiene al completar el recorrido.

    /** Vuelve a la primera parada con el cronómetro en cero. */
    public void reiniciar() {
        entregas = 0; // Primera parada.
        tiempo = 0; // Cronómetro en cero.
    }

    /** Indica si ya se entregaron todas las Cangreburgers. */
    public boolean terminadas() {
        return entregas >= DESTINOS.length; // Todas las paradas completadas.
    }

    /** Avanza el cronómetro y comprueba si el auto llegó y frenó en el destino activo. */
    public void actualizar(float deltaTime, Auto auto) {
        if (terminadas()) { // Ya no quedan paradas.
            return; // Conserva el tiempo final y evita leer fuera del arreglo.
        }
        tiempo += deltaTime; // Suma los segundos de este cuadro al cronómetro.
        float distanciaX = auto.x - DESTINOS[entregas][0]; // Separación horizontal al destino activo.
        float distanciaZ = auto.z - DESTINOS[entregas][1]; // Separación en profundidad al destino.
        float distanciaCuadrada = distanciaX * distanciaX + distanciaZ * distanciaZ; // Cercanía sin raíz cuadrada.
        boolean estaCerca = distanciaCuadrada < 3 * 3; // Acepta un radio de llegada de tres unidades.
        boolean estaFrenando = Math.abs(auto.velocidad) < 1; // Exige circular a menos de una unidad por segundo.
        if (estaCerca && estaFrenando) { // Solo completa la entrega si ambas condiciones se cumplen.
            entregas++; // Selecciona la siguiente parada o completa el juego.
        }
    }

    /** Marca la próxima parada con una plataforma dorada y una baliza flotante. */
    public void dibujar(Renderizador r) {
        if (terminadas()) { // Sin destino activo no hay marca.
            return; // Nada que dibujar.
        }
        float x = DESTINOS[entregas][0]; // Lee el X de la próxima entrega.
        float z = DESTINOS[entregas][1]; // Lee el Z de la próxima entrega.
        r.entero("uEmision", 1); // Hace que el objetivo sea visible incluso de noche.
        r.caja(x, 0.06f, z, 5, 0.08f, 5, 1, 0.72f, 0.12f); // Dibuja una marca dorada sobre el asfalto.
        if (!r.vistaMapa) { // Evita añadir una baliza tridimensional al mapa pequeño.
            float alturaBaliza = 3.5f + (float) Math.sin(tiempo * 2) * 0.3f; // Hace oscilar la baliza suavemente.
            r.cajaGirada(x, alturaBaliza, z, 0.8f, 0.8f, 0.8f, 1, 0.8f, 0.15f, tiempo); // Dibuja el cubo giratorio del objetivo.
        }
        r.entero("uEmision", 0); // Devuelve a los siguientes objetos su iluminación normal.
    }

    /** Texto para el título de la ventana. */
    public String estado() {
        if (terminadas()) { // Recorrido completo.
            return "GANASTE en " + (int) tiempo + " s! R: jugar otra vez"; // Tiempo final y opción de reinicio.
        }
        return "Entregas " + entregas + "/" + DESTINOS.length + " | Frena en la marca dorada | " + (int) tiempo + " s"; // Progreso.
    }
}
