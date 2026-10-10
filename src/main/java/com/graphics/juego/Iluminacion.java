package com.graphics.juego; // Reglas del juego: iluminación y entregas.

import com.graphics.ciudad.Farola; // Posiciones de las bombillas.
import com.graphics.ciudad.Semaforo; // Luces de color de los semáforos.
import com.graphics.motor.Renderizador; // Destino de los datos de iluminación.
import com.graphics.vehiculos.Auto; // Los faros siguen al auto.

/**
 * ILUMINACIÓN: día o noche (tecla N) y faros encendidos o apagados (tecla F).
 * En cada cuadro envía al shader todo lo que necesita para calcular la luz: ambiente, posición de
 * las farolas, luces de color de los semáforos y posición y dirección de los faros.
 * El cálculo en sí (Lambert, atenuación, conos) está en Shaders.
 */
public class Iluminacion {

    public boolean noche = true; // Inicia la escena con iluminación nocturna.
    public boolean faros = true; // Inicia los faros del auto encendidos.

    /** Alterna entre noche y día. */
    public void alternarNoche() {
        noche = !noche; // Invierte el ambiente.
    }

    /** Enciende o apaga los faros. */
    public void alternarFaros() {
        faros = !faros; // Invierte el interruptor.
    }

    /** Color del fondo: agua clara de día y azul profundo de noche. */
    public float[] colorCielo() {
        if (noche) { // Ambiente nocturno.
            return new float[] {0.02f, 0.07f, 0.16f}; // Agua profunda nocturna.
        }
        return new float[] {0.32f, 0.66f, 0.82f}; // Agua clara de Fondo de Bikini.
    }

    /** Envía al shader el ambiente, las farolas, los semáforos y los faros del auto. */
    public void enviar(Renderizador r, Auto auto) {
        r.noche = noche; // Ventanas, medusas y faroles consultan este valor al dibujarse.
        r.entero("uNoche", noche ? 1 : 0); // Comunica al shader el ambiente elegido.
        r.entero("uFaros", faros ? 1 : 0); // Activa o desactiva los conos de luz del auto.
        r.entero("uEmision", 0); // Hace que los objetos normales reciban iluminación.
        for (int indice = 0; indice < Farola.POSICIONES.length; indice++) { // Recorre todas las bombillas.
            float[] bombilla = Farola.POSICIONES[indice]; // Posición X, Y, Z de esta farola.
            r.vector("uLuces[" + indice + "]", bombilla[0], bombilla[1], bombilla[2]); // Envía esa posición al arreglo GLSL.
        }
        Semaforo.enviarLuces(r); // Color y posición de cada cabezal encendido.
        float frenteX = -(float) Math.sin(auto.angulo); // Calcula hacia dónde apunta el auto en X.
        float frenteZ = -(float) Math.cos(auto.angulo); // Calcula hacia dónde apunta el auto en Z.
        r.vector("uAuto", auto.x, 0.7f, auto.z); // Envía el centro del vehículo a la altura de sus faros.
        r.vector("uFrente", frenteX, 0, frenteZ); // Envía la dirección frontal del vehículo.
    }

    /** Texto para el título de la ventana. */
    public String estado() {
        return " | N: " + (noche ? "noche" : "dia") + " | F: faros " + (faros ? "ON" : "OFF"); // Ambos indicadores.
    }
}
