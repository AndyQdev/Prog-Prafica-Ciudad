package com.graphics.ciudad; // Todo lo que forma la ciudad: mapa, manzanas, calles y mobiliario.

import com.graphics.motor.Renderizador; // Pincel con el que se dibuja cada caja.
import com.graphics.motor.Texto; // Escribe los nombres de los letreros.
import com.graphics.personajes.Personajes; // Cada lugar dibuja a su habitante.

/**
 * LUGARES DE FONDO DE BIKINI: los tipos 3 a 7 del Mapa.
 * Crustáceo Cascarudo (con Don Cangrejo), Balde de Carnada (con Plankton), piña de Bob, cabeza moai de
 * Calamardo (con él tocando el clarinete) y roca de Patricio (con él durmiendo). Dos lugares llevan
 * letreros con su nombre escrito con cajas.
 */
public class LugaresBikini {

    private final Personajes personajes; // Habitantes que se dibujan frente a cada lugar.

    /** Guarda a los personajes para dibujarlos junto a su casa o restaurante. */
    public LugaresBikini(Personajes personajes) {
        this.personajes = personajes; // Referencia compartida con el juego.
    }

    /** Elige el lugar según el número del Mapa y luego agrega su letrero. */
    public void dibujar(Renderizador r, int tipo, float x, float z) {
        if (tipo == Mapa.CRUSTACEO) { // Restaurante de Don Cangrejo.
            dibujarCrustaceo(r, x, z); // Trampa para langostas de madera.
        } else if (tipo == Mapa.BALDE) { // Restaurante de Plankton.
            dibujarBalde(r, x, z); // Balde metálico.
        } else if (tipo == Mapa.PINA) { // Casa de Bob.
            dibujarPina(r, x, z); // La piña.
        } else if (tipo == Mapa.MOAI) { // Casa de Calamardo.
            dibujarMoai(r, x, z); // La cabeza de piedra.
        } else if (tipo == Mapa.ROCA) { // Casa de Patricio.
            dibujarRoca(r, x, z); // La roca.
        }
        dibujarLetreros(r, tipo, x, z); // Nombres en el Crustáceo y en Calle Concha.
    }

    /** Letreros legibles: el texto mira hacia la calle norte. */
    private void dibujarLetreros(Renderizador r, int tipo, float x, float z) {
        if (r.vistaMapa) { // En el minimapa no se leerían.
            return; // Omite los letreros.
        }
        if (tipo == 5) { // Junto a la piña: letrero de la calle.
            r.caja(x + 2, 1.5f, z - 4.6f, 0.12f, 2.9f, 0.12f, 0.40f, 0.42f, 0.45f); // Poste gris que termina bajo la placa.
        }
        if (r.noche) { // De noche los letreros se ven iluminados.
            r.entero("uEmision", 1); // Color directo.
        }
        if (tipo == 3) { // Crustáceo Cascarudo.
            float p = 0.042f; // Tamaño de cada cuadrito: el nombre ocupa el centro claro del letrero.
            float inicio = x + 2.5f + Texto.ancho("CRUSTACEO", p) / 2; // Centra el texto sobre el letrero.
            Texto.escribir(r, "CRUSTACEO", inicio, 7.2f - 3.5f * p, z - 4.6f, p, -1, 0.75f, 0.12f, 0.10f); // Letras rojas.
        }
        if (tipo == 5) { // Calle Concha.
            r.caja(x + 2, 3.3f, z - 4.6f, 3.9f, 0.7f, 0.08f, 0.10f, 0.45f, 0.25f); // Placa verde.
            float p = 0.048f; // Tamaño de cada cuadrito.
            float inicio = x + 2 + Texto.ancho("CALLE CONCHA", p) / 2; // Centra el texto sobre la placa.
            Texto.escribir(r, "CALLE CONCHA", inicio, 3.3f - 3.5f * p, z - 4.66f, p, -1, 0.95f, 0.95f, 0.95f); // Letras blancas.
        }
        r.entero("uEmision", 0); // Restablece la iluminación normal.
    }

    /** Crustáceo Cascarudo: trampa para langostas de madera, techo a dos aguas y letrero rojo. */
    private void dibujarCrustaceo(Renderizador r, float x, float z) {
        r.caja(x, 2.2f, z, 7, 3.8f, 6, 0.60f, 0.40f, 0.22f); // Cuerpo de madera.
        for (int tabla = -3; tabla <= 3; tabla++) { // Tablas verticales de la fachada norte.
            r.caja(x + tabla, 2.2f, z - 3.02f, 0.15f, 3.8f, 0.06f, 0.78f, 0.58f, 0.35f); // Tabla clara.
        }
        r.caja(x, 4.35f, z, 7.4f, 0.5f, 6.4f, 0.42f, 0.27f, 0.15f); // Primer escalón del techo.
        r.caja(x, 4.85f, z, 7.4f, 0.5f, 4.6f, 0.42f, 0.27f, 0.15f); // Segundo escalón, más angosto.
        r.caja(x, 5.35f, z, 7.4f, 0.5f, 2.8f, 0.42f, 0.27f, 0.15f); // Tercer escalón.
        r.caja(x, 5.8f, z, 7.4f, 0.4f, 1.0f, 0.42f, 0.27f, 0.15f); // Cumbrera del techo.
        r.vidrio(x - 2.3f, 2.6f, z - 3.06f, 1.0f, 1.0f, 0.06f); // Ventana izquierda.
        r.vidrio(x + 2.3f, 2.6f, z - 3.06f, 1.0f, 1.0f, 0.06f); // Ventana derecha.
        r.caja(x, 1.4f, z - 3.06f, 1.6f, 2.2f, 0.06f, 0.25f, 0.45f, 0.75f); // Puerta azul.
        r.caja(x + 2.5f, 3.8f, z - 4.4f, 0.25f, 7, 0.25f, 0.42f, 0.27f, 0.15f); // Poste del letrero.
        if (r.noche) { // El letrero se ilumina de noche.
            r.entero("uEmision", 1); // Color directo, como una bombilla.
        }
        r.caja(x + 2.5f, 7.2f, z - 4.4f, 3.0f, 1.4f, 0.25f, 0.85f, 0.20f, 0.15f); // Letrero rojo.
        r.caja(x + 2.5f, 7.2f, z - 4.55f, 2.4f, 0.8f, 0.06f, 0.98f, 0.85f, 0.50f); // Centro claro del letrero.
        r.entero("uEmision", 0); // Restablece la iluminación normal.
        if (!r.vistaMapa) { // Los personajes solo se dibujan en la vista principal.
            personajes.dibujarCangrejo(r, x - 1, z - 4); // Don Cangrejo espera en la puerta.
        }
    }

    /** Balde de Carnada: balde metálico redondo que se ensancha hacia arriba, con asa. */
    private void dibujarBalde(Renderizador r, float x, float z) {
        for (int nivel = 0; nivel < 4; nivel++) { // Cuatro anillos, cada uno más ancho.
            float ancho = 4.6f + nivel * 0.3f; // Ensancha el balde al subir.
            float y = 1.1f + nivel * 1.6f; // Apila los anillos.
            r.cilindro(x, y, z, ancho, 1.6f, 0.55f, 0.58f, 0.62f); // Anillo de metal gris.
        }
        r.cilindro(x, 3.6f, z, 5.25f, 0.4f, 0.30f, 0.32f, 0.35f); // Franja oscura.
        r.cilindro(x, 6.85f, z, 5.8f, 0.3f, 0.30f, 0.32f, 0.35f); // Borde superior.
        r.caja(x - 2.8f, 7.9f, z, 0.2f, 2.0f, 0.2f, 0.30f, 0.32f, 0.35f); // Lado izquierdo del asa.
        r.caja(x + 2.8f, 7.9f, z, 0.2f, 2.0f, 0.2f, 0.30f, 0.32f, 0.35f); // Lado derecho del asa.
        r.caja(x, 8.9f, z, 5.8f, 0.2f, 0.2f, 0.30f, 0.32f, 0.35f); // Parte superior del asa.
        r.caja(x, 1.4f, z - 2.32f, 1.4f, 2.2f, 0.06f, 0.20f, 0.25f, 0.32f); // Puerta metálica.
        r.vidrio(x, 5.2f, z - 2.62f, 1.4f, 0.8f, 0.06f); // Ventana alta.
        if (!r.vistaMapa) { // Los personajes solo se dibujan en la vista principal.
            personajes.dibujarPlankton(r, x + 1, z - 4); // Plankton vigila la entrada.
        }
    }

    /** Piña de Bob: prisma octogonal anaranjado con franjas, ventanas redondas y hojas que se mecen. */
    private void dibujarPina(Renderizador r, float x, float z) {
        r.octogono(x, 3.8f, z, 5, 7, 0.93f, 0.62f, 0.15f); // Cuerpo de la piña.
        for (float y = 1.5f; y < 7; y += 1.5f) { // Franjas horizontales cada metro y medio.
            r.octogono(x, y, z, 5.06f, 0.15f, 0.70f, 0.42f, 0.10f); // Franja más oscura.
        }
        r.octogono(x, 7.8f, z, 3.8f, 1.0f, 0.93f, 0.62f, 0.15f); // Parte superior más angosta.
        for (int hoja = 0; hoja < 6; hoja++) { // Seis hojas exteriores alrededor de la copa.
            float angulo = hoja * (float) Math.PI / 3 + (float) Math.sin(r.tiempo + hoja) * 0.15f; // Cada hoja se mece.
            float hojaX = x + (float) Math.cos(angulo) * 1.2f; // Reparte la hoja en un círculo.
            float hojaZ = z + (float) Math.sin(angulo) * 1.2f; // Completa la posición circular.
            r.cajaGirada(hojaX, 9.3f, hojaZ, 0.35f, 2.4f, 1.3f, 0.20f, 0.55f, 0.20f, angulo); // Hoja verde.
        }
        r.caja(x, 10.0f, z, 0.6f, 3.0f, 0.6f, 0.25f, 0.65f, 0.25f); // Hoja central más alta.
        r.caja(x, 1.5f, z - 2.53f, 1.2f, 2.2f, 0.08f, 0.35f, 0.45f, 0.60f); // Puerta gris azulada.
        r.vidrio(x - 1.2f, 4.5f, z - 2.53f, 0.9f, 0.9f, 0.08f); // Ventana alta.
        r.vidrio(x + 0.9f, 3.2f, z - 2.53f, 0.9f, 0.9f, 0.08f); // Ventana baja.
    }

    /** Casa de Calamardo: cabeza de piedra con ceja, nariz larga, orejas y ventanas como ojos. */
    private void dibujarMoai(Renderizador r, float x, float z) {
        r.caja(x, 4.55f, z, 4, 8.5f, 3.6f, 0.45f, 0.53f, 0.58f); // Bloque principal de piedra.
        r.caja(x, 6.6f, z - 1.95f, 4.1f, 0.7f, 0.6f, 0.38f, 0.45f, 0.50f); // Ceja que sobresale.
        r.caja(x, 4.9f, z - 2.2f, 1.0f, 2.6f, 1.0f, 0.45f, 0.53f, 0.58f); // Nariz larga.
        r.caja(x, 3.7f, z - 2.45f, 1.2f, 0.4f, 0.8f, 0.45f, 0.53f, 0.58f); // Punta de la nariz.
        r.caja(x - 2.1f, 5.2f, z, 0.3f, 2.0f, 0.9f, 0.40f, 0.48f, 0.53f); // Oreja izquierda.
        r.caja(x + 2.1f, 5.2f, z, 0.3f, 2.0f, 0.9f, 0.40f, 0.48f, 0.53f); // Oreja derecha.
        r.vidrio(x - 1.1f, 5.9f, z - 1.83f, 0.8f, 0.6f, 0.06f); // Ojo izquierdo, que es una ventana.
        r.vidrio(x + 1.1f, 5.9f, z - 1.83f, 0.8f, 0.6f, 0.06f); // Ojo derecho.
        r.caja(x, 1.5f, z - 1.83f, 1.4f, 2.4f, 0.06f, 0.35f, 0.25f, 0.18f); // Puerta de madera.
        if (!r.vistaMapa) { // Los personajes solo se dibujan en la vista principal.
            personajes.dibujarCalamardo(r, x + 1.5f, z - 3.6f); // Calamardo toca el clarinete frente a su casa, lejos de la farola.
        }
    }

    /** Casa de Patricio: roca en forma de cúpula con una veleta que gira. */
    private void dibujarRoca(Renderizador r, float x, float z) {
        r.cilindro(x, 0.9f, z, 5.2f, 1.2f, 0.55f, 0.38f, 0.25f); // Base de la roca.
        r.cilindro(x, 1.9f, z, 4.3f, 0.8f, 0.55f, 0.38f, 0.25f); // Segundo nivel, más angosto.
        r.cilindro(x, 2.6f, z, 3.0f, 0.7f, 0.55f, 0.38f, 0.25f); // Tercer nivel.
        r.caja(x, 3.1f, z, 1.6f, 0.4f, 1.6f, 0.55f, 0.38f, 0.25f); // Cima de la cúpula.
        r.caja(x, 4.1f, z, 0.08f, 1.8f, 0.08f, 0.20f, 0.20f, 0.22f); // Poste de la veleta.
        r.cajaGirada(x, 4.9f, z, 1.0f, 0.1f, 0.12f, 0.20f, 0.20f, 0.22f, r.tiempo); // Veleta que gira con el reloj.
        if (!r.vistaMapa) { // Los personajes solo se dibujan en la vista principal.
            personajes.dibujarPatricio(r, x + 1, z - 4.1f); // Patricio frente a su roca.
        }
    }
}
