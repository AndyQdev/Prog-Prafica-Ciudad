package com.graphics.personajes; // Personajes y vida de la ciudad: Bob, vecinos famosos y animales marinos.

import com.graphics.juego.Entregas; // Don Cangrejo celebra cuando se completan las entregas.
import com.graphics.motor.Renderizador; // Pincel con el que se dibuja cada caja.
import com.graphics.vehiculos.Auto; // Los personajes reaccionan cuando el auto se acerca.

/**
 * PERSONAJES: los vecinos famosos de Fondo de Bikini.
 * Don Cangrejo, Plankton, Patricio y Calamardo se dibujan frente a sus lugares (LugaresBikini los llama).
 * Cuando el botemóvil está cerca giran para mirarlo y reaccionan: Patricio despierta y saluda,
 * Calamardo deja de tocar y tiembla molesto, Don Cangrejo celebra al terminar las entregas.
 */
public class Personajes {

    private static final float DISTANCIA_ATENCION = 14; // A esta distancia los personajes notan al botemóvil.
    private final Auto auto; // Auto del jugador, para saber dónde está.
    private final Entregas entregas; // Progreso de las entregas, para la celebración.

    /** Guarda las referencias que necesitan para reaccionar. */
    public Personajes(Auto auto, Entregas entregas) {
        this.auto = auto; // Auto del jugador.
        this.entregas = entregas; // Entregas del juego.
    }

    /** Indica si el botemóvil está lo bastante cerca para que el personaje reaccione. */
    private boolean autoCerca(float x, float z) {
        float distanciaX = auto.x - x; // Separación horizontal.
        float distanciaZ = auto.z - z; // Separación en profundidad.
        return distanciaX * distanciaX + distanciaZ * distanciaZ < DISTANCIA_ATENCION * DISTANCIA_ATENCION; // Sin raíz.
    }

    /** Fija el centro del personaje; si el bote está cerca, lo gira para mirarlo. */
    private void colocarPersonaje(Renderizador r, float x, float z) {
        float giro = 0; // Por defecto mira hacia la calle norte.
        if (autoCerca(x, z)) { // Reacciona solo cuando el bote se acerca.
            giro = (float) Math.atan2(-(auto.x - x), -(auto.z - z)); // Ángulo cuyo frente (-sen, -cos) apunta al bote.
        }
        r.origen(x, z, giro); // Usa ese centro y ese giro para las partes siguientes.
    }

    /** Don Cangrejo: aplaude con sus tenazas y salta de alegría al completar todas las entregas. */
    public void dibujarCangrejo(Renderizador r, float x, float z) {
        colocarPersonaje(r, x, z); // Lo orienta hacia el bote si está cerca.
        float base = 0.3f; // Altura de la acera.
        if (entregas.terminadas()) { // Todas las Cangreburgers fueron entregadas.
            base += Math.abs((float) Math.sin(r.tiempo * 6)) * 0.8f; // Salta celebrando.
        }
        float abre = ((float) Math.sin(r.tiempo * 6) + 1) * 0.1f; // Apertura de las tenazas entre 0 y 0.2.
        float[] lados = {-1, 1}; // Lado izquierdo y derecho.
        for (float lado : lados) { // Dibuja las partes simétricas.
            r.parte(lado * 0.3f, base + 0.3f, 0, 0.1f, 0.6f, 0.1f, 0.85f, 0.15f, 0.10f); // Pata.
            r.parte(lado * 0.2f, base + 1.65f, -0.1f, 0.08f, 0.5f, 0.08f, 0.85f, 0.15f, 0.10f); // Tallo del ojo.
            r.parte(lado * 0.2f, base + 1.95f, -0.1f, 0.22f, 0.22f, 0.22f, 1, 1, 1); // Ojo blanco.
            r.parte(lado * 0.2f, base + 1.95f, -0.22f, 0.08f, 0.08f, 0.02f, 0, 0, 0); // Pupila.
            r.parte(lado * 0.7f, base + 1.0f, -0.1f, 0.3f, 0.15f, 0.15f, 0.85f, 0.15f, 0.10f); // Brazo.
            r.parte(lado * 0.95f, base + 0.95f, -0.25f, 0.3f, 0.12f, 0.45f, 0.85f, 0.15f, 0.10f); // Tenaza inferior.
            r.parte(lado * 0.95f, base + 1.12f + abre, -0.25f, 0.3f, 0.12f, 0.45f, 0.85f, 0.15f, 0.10f); // Tenaza superior móvil.
        }
        r.parte(0, base + 0.75f, 0, 1.12f, 0.35f, 0.77f, 0.30f, 0.50f, 0.80f); // Camisa azul.
        r.parte(0, base + 1.15f, 0, 1.1f, 0.5f, 0.75f, 0.85f, 0.15f, 0.10f); // Caparazón rojo.
        r.parte(0, base + 1.1f, -0.38f, 0.4f, 0.08f, 0.02f, 0.30f, 0.05f, 0.05f); // Boca.
    }

    /** Plankton: diminuto, verde, con un solo ojo; salta enojado todo el tiempo. */
    public void dibujarPlankton(Renderizador r, float x, float z) {
        colocarPersonaje(r, x, z); // Lo orienta hacia el bote si está cerca.
        float base = 0.3f + Math.abs((float) Math.sin(r.tiempo * 9)) * 0.3f; // Saltitos rápidos.
        r.parte(0, base + 0.45f, 0, 0.35f, 0.9f, 0.25f, 0.20f, 0.60f, 0.30f); // Cuerpo verde.
        r.parte(0, base + 0.7f, -0.13f, 0.24f, 0.24f, 0.02f, 1, 1, 1); // Ojo único.
        r.parte(0, base + 0.7f, -0.145f, 0.12f, 0.12f, 0.02f, 0.80f, 0.10f, 0.10f); // Iris rojo.
        r.parte(-0.08f, base + 1.05f, 0, 0.03f, 0.3f, 0.03f, 0.20f, 0.60f, 0.30f); // Antena izquierda.
        r.parte(0.08f, base + 1.05f, 0, 0.03f, 0.3f, 0.03f, 0.20f, 0.60f, 0.30f); // Antena derecha.
    }

    /** Patricio: duerme si el bote está lejos y salta saludando cuando se acerca. */
    public void dibujarPatricio(Renderizador r, float x, float z) {
        colocarPersonaje(r, x, z); // Lo orienta hacia el bote si está cerca.
        boolean despierto = autoCerca(x, z); // Solo despierta cuando el bote llega.
        float base = 0.3f; // Altura de la acera.
        float brazo = -0.25f; // Brazos caídos mientras duerme.
        if (despierto) { // Reacción de bienvenida.
            base += Math.abs((float) Math.sin(r.tiempo * 6)) * 0.7f; // Salta de alegría.
            brazo = (float) Math.sin(r.tiempo * 12) * 0.25f + 0.2f; // Agita los brazos.
        }
        float rosa = 1.0f; // Componente roja del color de Patricio.
        r.parte(-0.25f, base + 0.3f, 0, 0.35f, 0.6f, 0.4f, rosa, 0.60f, 0.65f); // Pierna izquierda.
        r.parte(0.25f, base + 0.3f, 0, 0.35f, 0.6f, 0.4f, rosa, 0.60f, 0.65f); // Pierna derecha.
        r.parte(0, base + 0.75f, 0, 0.95f, 0.4f, 0.6f, 0.45f, 0.75f, 0.25f); // Bermuda verde.
        r.parte(-0.25f, base + 0.78f, -0.31f, 0.15f, 0.15f, 0.02f, 0.60f, 0.30f, 0.75f); // Flor violeta.
        r.parte(0.25f, base + 0.7f, -0.31f, 0.15f, 0.15f, 0.02f, 0.60f, 0.30f, 0.75f); // Otra flor violeta.
        r.parte(0, base + 1.35f, 0, 0.85f, 0.8f, 0.55f, rosa, 0.60f, 0.65f); // Torso.
        r.parte(0, base + 1.95f, 0, 0.55f, 0.4f, 0.45f, rosa, 0.60f, 0.65f); // Cabeza que se angosta.
        r.parte(0, base + 2.25f, 0, 0.3f, 0.2f, 0.3f, rosa, 0.60f, 0.65f); // Punta de la estrella.
        r.parte(-0.65f, base + 1.45f + brazo, 0, 0.5f, 0.28f, 0.35f, rosa, 0.60f, 0.65f); // Brazo izquierdo.
        r.parte(0.65f, base + 1.45f + brazo, 0, 0.5f, 0.28f, 0.35f, rosa, 0.60f, 0.65f); // Brazo derecho.
        float alturaOjos = 0.03f; // Ojos casi cerrados mientras duerme.
        if (despierto) { // Abre los ojos al despertar.
            alturaOjos = 0.2f; // Ojo completo.
        }
        r.parte(-0.13f, base + 1.9f, -0.235f, 0.16f, alturaOjos, 0.02f, 1, 1, 1); // Ojo izquierdo.
        r.parte(0.13f, base + 1.9f, -0.235f, 0.16f, alturaOjos, 0.02f, 1, 1, 1); // Ojo derecho.
        r.parte(0, base + 1.6f, -0.28f, 0.3f, 0.06f, 0.02f, 0.55f, 0.10f, 0.10f); // Boca.
        if (despierto) { // Las pupilas solo se ven con los ojos abiertos.
            r.parte(-0.13f, base + 1.88f, -0.25f, 0.07f, 0.09f, 0.02f, 0, 0, 0); // Pupila izquierda.
            r.parte(0.13f, base + 1.88f, -0.25f, 0.07f, 0.09f, 0.02f, 0, 0, 0); // Pupila derecha.
        } else { // Mientras duerme suelta burbujas de ronquido.
            for (int indice = 0; indice < 3; indice++) { // Tres burbujas desfasadas.
                float t = (r.tiempo * 0.5f + indice / 3f) % 1; // Progreso de la burbuja.
                float tamano = 0.1f + t * 0.15f; // Crece al subir.
                r.parte(0.3f + t * 0.4f, base + 2.5f + t * 1.5f, 0, tamano, tamano, tamano, 0.85f, 0.95f, 1); // Burbuja.
            }
        }
    }

    /** Calamardo: toca el clarinete con notas que flotan; si el bote se acerca, tiembla molesto. */
    public void dibujarCalamardo(Renderizador r, float x, float z) {
        colocarPersonaje(r, x, z); // Lo orienta hacia el bote si está cerca.
        boolean molesto = autoCerca(x, z); // El ruido del bote interrumpe su música.
        float base = 0.3f; // Altura de la acera.
        float vaiven = (float) Math.sin(r.tiempo * 2) * 0.1f; // Balanceo lento mientras toca.
        if (molesto) { // Reacción de enojo.
            vaiven = (float) Math.sin(r.tiempo * 30) * 0.04f; // Temblor rápido.
        }
        float[] patas = {-0.2f, 0.2f}; // Posición de los tentáculos.
        for (float pataX : patas) { // Recorre izquierda y derecha.
            for (float pataZ : patas) { // Recorre delante y detrás.
                r.parte(pataX, base + 0.35f, pataZ * 0.5f, 0.14f, 0.7f, 0.14f, 0.55f, 0.78f, 0.75f); // Tentáculo.
            }
        }
        r.parte(vaiven, base + 1.05f, 0, 0.6f, 0.75f, 0.45f, 0.55f, 0.35f, 0.20f); // Camisa marrón.
        r.parte(vaiven, base + 1.5f, 0, 0.3f, 0.2f, 0.3f, 0.55f, 0.78f, 0.75f); // Cuello.
        r.parte(vaiven, base + 1.95f, 0, 0.75f, 0.75f, 0.65f, 0.55f, 0.78f, 0.75f); // Cabeza grande.
        r.parte(vaiven, base + 2.4f, 0.05f, 0.6f, 0.2f, 0.55f, 0.55f, 0.78f, 0.75f); // Parte alta de la cabeza.
        r.parte(vaiven, base + 1.75f, -0.4f, 0.2f, 0.45f, 0.2f, 0.55f, 0.78f, 0.75f); // Nariz colgante.
        float[] ojos = {-0.17f, 0.17f}; // Separación de los ojos.
        for (float ojoX : ojos) { // Dibuja cada ojo con su párpado caído.
            r.parte(vaiven + ojoX, base + 2.05f, -0.33f, 0.24f, 0.2f, 0.02f, 1, 1, 0.85f); // Blanco del ojo.
            r.parte(vaiven + ojoX, base + 2.13f, -0.345f, 0.26f, 0.08f, 0.02f, 0.45f, 0.66f, 0.63f); // Párpado.
            r.parte(vaiven + ojoX, base + 2.0f, -0.35f, 0.07f, 0.07f, 0.02f, 0.80f, 0.10f, 0.10f); // Pupila roja.
        }
        r.parte(vaiven - 0.3f, base + 1.1f, -0.25f, 0.12f, 0.12f, 0.5f, 0.55f, 0.78f, 0.75f); // Brazo izquierdo.
        r.parte(vaiven + 0.3f, base + 1.1f, -0.25f, 0.12f, 0.12f, 0.5f, 0.55f, 0.78f, 0.75f); // Brazo derecho.
        if (molesto) { // Deja de tocar cuando está molesto.
            return; // No dibuja clarinete ni notas.
        }
        float soplido = (float) Math.sin(r.tiempo * 4) * 0.05f; // El clarinete sube y baja al tocar.
        r.parte(vaiven, base + 1.3f + soplido, -0.55f, 0.08f, 0.7f, 0.08f, 0.08f, 0.08f, 0.10f); // Clarinete negro.
        r.parte(vaiven, base + 0.95f + soplido, -0.55f, 0.16f, 0.08f, 0.16f, 0.75f, 0.75f, 0.80f); // Campana plateada.
        for (int indice = 0; indice < 3; indice++) { // Tres notas musicales desfasadas.
            float t = (r.tiempo * 0.4f + indice / 3f) % 1; // Progreso de la nota.
            float notaX = -0.4f - t * 0.6f + (float) Math.sin(r.tiempo * 3 + indice) * 0.1f; // Se aleja ondulando.
            float notaY = base + 2.0f + t * 1.8f; // Sube sobre la cabeza.
            r.parte(notaX, notaY, -0.6f, 0.14f, 0.12f, 0.06f, 0.25f, 0.10f, 0.40f); // Cabeza de la nota.
            r.parte(notaX + 0.06f, notaY + 0.18f, -0.6f, 0.03f, 0.35f, 0.03f, 0.25f, 0.10f, 0.40f); // Plica de la nota.
        }
    }
}
