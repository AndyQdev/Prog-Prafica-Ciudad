package com.graphics.motor; // Piezas técnicas que no saben nada de la ciudad: ventana, dibujo y cámara.

import com.graphics.ciudad.Mapa; // El tamaño de la ciudad define la distancia de la cámara aérea.

/**
 * CÁMARA: decide desde dónde se mira la ciudad.
 * Modo seguimiento: detrás del auto, a 12 unidades y 9 de altura. Modo aéreo: una vista oblicua
 * de toda la ciudad, cuya distancia crece con el tamaño del mapa. La tecla C alterna entre ambos.
 */
public class Camara {

    public boolean aerea = false; // false: seguir el auto; true: observar toda la ciudad.
    private static final float ORBITA = 0.6f; // Ángulo fijo de la vista aérea, en radianes.

    /** Cambia entre la cámara de seguimiento y la aérea. */
    public void alternar() {
        aerea = !aerea; // Invierte el modo actual.
    }

    /** Envía al shader la posición de la cámara y el punto observado, según el modo. */
    public void enviar(Renderizador r, float autoX, float autoZ, float angulo, int ancho, int alto) {
        if (aerea) { // Vista general de la ciudad.
            float radio = Mapa.LIMITE * 1.85f; // Órbita proporcional al tamaño de la ciudad: unas 102 unidades.
            float alturaCamara = Mapa.LIMITE * 1.6f; // Altura proporcional: 88 unidades.
            float camaraX = (float) Math.sin(ORBITA) * radio; // Calcula la posición X sobre la órbita.
            float camaraZ = (float) Math.cos(ORBITA) * radio; // Calcula la posición Z de esa misma órbita.
            r.vector("uOjo", camaraX, alturaCamara, camaraZ); // Envía la posición elevada de la cámara.
            r.vector("uObjetivo", 0, 0, 0); // Apunta la cámara hacia el centro de la ciudad.
        } else { // Cámara detrás del auto.
            float camaraX = autoX + (float) Math.sin(angulo) * 12; // Coloca la cámara 12 unidades detrás en X.
            float camaraZ = autoZ + (float) Math.cos(angulo) * 12; // Coloca la cámara 12 unidades detrás en Z.
            r.vector("uOjo", camaraX, 9, camaraZ); // Envía la posición de la cámara, a 9 unidades de altura.
            r.vector("uObjetivo", autoX, 0.8f, autoZ); // Orienta la cámara hacia la carrocería.
        }
        r.decimal("uAspecto", (float) ancho / alto); // Mantiene las proporciones al redimensionar la ventana.
    }
}
