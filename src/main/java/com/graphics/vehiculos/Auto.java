package com.graphics.vehiculos; // Vehículos: el botemóvil del jugador y el tráfico autónomo.

import com.graphics.ciudad.Mapa; // La ciudad decide dónde puede estar el auto.
import com.graphics.motor.Renderizador; // Pincel con el que se dibuja cada caja.
import com.graphics.motor.Ventana; // Teclado para conducir.
import com.graphics.personajes.Bob; // El conductor sentado al volante.
import static org.lwjgl.glfw.GLFW.*; // Constantes de las teclas.

/**
 * AUTO: el botemóvil del jugador.
 * Guarda posición, orientación y velocidad; las actualiza con el teclado usando el tiempo entre
 * cuadros; choca como un rectángulo con su forma real; se desliza por las aceras; gira sobre el eje
 * trasero y se dibuja con Bob al volante. Su frente local es -Z y el ángulo está en radianes.
 */
public class Auto {

    // ==================== 1. ESTADO Y MEDIDAS ====================
    public static final float INICIO_X = -30; // Posición inicial: centro de una calle, junto al Crustáceo.
    public static final float INICIO_Z = 30; // Avenida que separa la ciudad original del sector sur.
    public static final float MEDIO_ANCHO = 1.0f; // Mitad del ancho del auto, de rueda a rueda.
    public static final float MEDIO_LARGO = 1.4f; // Mitad del largo del auto, de faro a luz trasera.
    public static final float RADIO = 1.65f; // Radio que contiene al auto; se usa contra el tráfico.
    private static final float EJE_TRASERO = 0.8f; // Distancia del centro al eje trasero, alrededor del cual gira.
    public float x = INICIO_X; // Posición horizontal actual.
    public float z = INICIO_Z; // Posición en profundidad actual.
    public float angulo = 0; // Orientación en radianes; cero apunta hacia -Z (norte).
    public float velocidad = 0; // Unidades por segundo; un valor negativo significa reversa.
    public boolean frenando = false; // true mientras el conductor frena; enciende las luces de freno.
    private Trafico trafico; // Botes con los que también puede chocar; puede ser null.

    /** Indica con qué tráfico debe comprobar choques (null si no hay tráfico, como en los tests). */
    public void usarTrafico(Trafico trafico) {
        this.trafico = trafico; // Guarda la referencia.
    }

    /** Coloca nuevamente el auto en su punto de partida. */
    public void reiniciar() {
        x = INICIO_X; // Recupera la coordenada X de inicio.
        z = INICIO_Z; // Recupera la coordenada Z de inicio.
        angulo = 0; // Orienta el frente hacia -Z.
        velocidad = 0; // Detiene cualquier movimiento previo.
    }

    // ==================== 2. CONDUCCIÓN POR CUADRO ====================

    /** Lee el teclado y mueve el auto; deltaTime son los segundos transcurridos desde el cuadro anterior. */
    public void actualizar(float deltaTime, Ventana teclado) {
        float acelerador = 0; // Sin teclas pulsadas no se aplica aceleración del motor.
        if (teclado.pulsada(GLFW_KEY_W) || teclado.pulsada(GLFW_KEY_UP)) { // Acepta W o flecha arriba para avanzar.
            acelerador += 1; // Solicita aceleración hacia delante.
        }
        if (teclado.pulsada(GLFW_KEY_S) || teclado.pulsada(GLFW_KEY_DOWN)) { // Acepta S o flecha abajo para retroceder.
            acelerador -= 1; // Primero reduce la velocidad positiva y después entra en reversa.
        }
        boolean contraMarcha = acelerador * velocidad < -0.25f; // Pedir el sentido contrario al movimiento también frena.
        velocidad += acelerador * 9 * deltaTime; // Integra la aceleración de 9 unidades por segundo cuadrado.
        float resistencia = 0.7f; // Define la pérdida de velocidad normal al rodar.
        frenando = contraMarcha; // Las luces de freno se encienden al frenar con S o con W en reversa.
        if (teclado.pulsada(GLFW_KEY_SPACE)) { // Detecta si el usuario mantiene presionado el freno.
            resistencia = 7; // Aumenta la pérdida de velocidad para detenerse rápidamente.
            frenando = true; // El freno de mano también enciende las luces de freno.
        }
        float factorFrenado = (float) Math.exp(-resistencia * deltaTime); // Calcula la fracción de velocidad conservada.
        velocidad *= factorFrenado; // Aplica resistencia de forma proporcional al tiempo transcurrido.
        velocidad = Math.max(-6, Math.min(16, velocidad)); // Limita la reversa a -6 y el avance a 16.
        float direccion = 0; // Sin dirección presionada el volante permanece recto.
        if (teclado.pulsada(GLFW_KEY_A) || teclado.pulsada(GLFW_KEY_LEFT)) { // Comprueba el giro hacia la izquierda.
            direccion += 1; // Selecciona el sentido positivo de rotación.
        }
        if (teclado.pulsada(GLFW_KEY_D) || teclado.pulsada(GLFW_KEY_RIGHT)) { // Comprueba el giro hacia la derecha.
            direccion -= 1; // Selecciona el sentido negativo de rotación.
        }
        girar(direccion * velocidad * 0.11f * deltaTime); // Gira según la velocidad; en reversa invierte el giro.
        mover(deltaTime); // Avanza en la dirección del frente.
    }

    /** Avanza; si choca de costado se desliza por la acera y si choca de frente rebota un poco. */
    private void mover(float deltaTime) {
        float frenteX = -(float) Math.sin(angulo); // Obtiene la componente X del frente del vehículo.
        float frenteZ = -(float) Math.cos(angulo); // Obtiene la componente Z; con ángulo cero vale -1.
        float siguienteX = x + frenteX * velocidad * deltaTime; // Propone la nueva posición X.
        float siguienteZ = z + frenteZ * velocidad * deltaTime; // Propone la nueva posición Z.
        float roce = (float) Math.exp(-2.5f * deltaTime); // Velocidad que se conserva al rozar una acera.
        if (puedeOcupar(siguienteX, siguienteZ, angulo)) { // Comprueba la posición antes de mover el auto.
            x = siguienteX; // Acepta el desplazamiento horizontal.
            z = siguienteZ; // Acepta el desplazamiento en profundidad.
        } else if (puedeOcupar(siguienteX, z, angulo)) { // Choca de costado: intenta avanzar solo en X.
            x = siguienteX; // Se desliza a lo largo de la acera, que es paralela al eje X.
            velocidad *= roce; // El roce con el bordillo le quita algo de velocidad.
        } else if (puedeOcupar(x, siguienteZ, angulo)) { // Intenta avanzar solo en Z.
            z = siguienteZ; // Se desliza a lo largo de una acera paralela al eje Z.
            velocidad *= roce; // El roce le quita algo de velocidad.
        } else { // Choque de frente: ningún movimiento es posible.
            velocidad *= -0.15f; // Rebota un poco hacia atrás en lugar de quedar pegado.
        }
    }

    /** Gira el auto alrededor de su eje trasero; si el giro lo metería en una acera, no gira. */
    private void girar(float giro) {
        if (giro == 0) { // Sin volante o sin velocidad no hay giro.
            return; // Conserva posición y orientación.
        }
        float traseraX = x + (float) Math.sin(angulo) * EJE_TRASERO; // Eje trasero: centro menos el frente por la distancia.
        float traseraZ = z + (float) Math.cos(angulo) * EJE_TRASERO; // Completa la posición del eje trasero.
        float nuevoAngulo = angulo + giro; // Orientación propuesta.
        float nuevaX = traseraX - (float) Math.sin(nuevoAngulo) * EJE_TRASERO; // El centro gira alrededor del eje trasero.
        float nuevaZ = traseraZ - (float) Math.cos(nuevoAngulo) * EJE_TRASERO; // Completa el nuevo centro.
        if (puedeOcupar(nuevaX, nuevaZ, nuevoAngulo)) { // Comprueba que la carrocería girada quepa.
            x = nuevaX; // Acepta el nuevo centro.
            z = nuevaZ; // Acepta el nuevo centro en Z.
            angulo = nuevoAngulo; // Acepta la nueva orientación.
        }
    }

    // ==================== 3. COLISIONES ====================

    /** Comprueba si el auto cabe en (x, z) con el giro dado: ni manzanas, ni bordes, ni botes del tráfico. */
    public boolean puedeOcupar(float nuevaX, float nuevaZ, float giro) {
        if (!Mapa.puedeOcupar(nuevaX, nuevaZ, giro, MEDIO_ANCHO, MEDIO_LARGO)) { // Primero revisa la ciudad.
            return false; // Una manzana o el borde bloquean el paso.
        }
        return trafico == null || trafico.permite(nuevaX, nuevaZ, x, z); // Después revisa el tráfico.
    }

    // ==================== 4. DIBUJO: BOTEMÓVIL CON BOB ====================

    /** Dibuja el botemóvil; las piezas usan coordenadas locales que giran y se trasladan con el auto. */
    public void dibujar(Renderizador r, boolean faros) {
        r.origen(x, z, angulo); // Todas las piezas siguientes giran alrededor del auto.
        float[] lados = {-0.85f, 0.85f}; // Ubica las ruedas a izquierda y derecha.
        for (float x : lados) { // Selecciona un lado del bote.
            for (float z : lados) { // Selecciona el eje delantero o trasero.
                r.parte(x, 0.35f, z, 0.22f, 0.5f, 0.5f, 0.06f, 0.07f, 0.08f); // Dibuja una rueda oscura.
            }
        }
        r.parte(0, 0.6f, 0.25f, 1.6f, 0.5f, 2.0f, 0.93f, 0.90f, 0.82f); // Casco principal color crema.
        r.parte(0, 0.62f, -0.95f, 1.25f, 0.46f, 0.4f, 0.93f, 0.90f, 0.82f); // Primer tramo de la proa, más angosto.
        r.parte(0, 0.64f, -1.22f, 0.75f, 0.42f, 0.25f, 0.93f, 0.90f, 0.82f); // Punta de la proa.
        r.parte(0, 0.72f, 0.25f, 1.62f, 0.12f, 2.02f, 0.20f, 0.45f, 0.80f); // Franja azul del casco.
        r.parte(0, 0.72f, -0.95f, 1.27f, 0.12f, 0.42f, 0.20f, 0.45f, 0.80f); // Franja azul del primer tramo de proa.
        r.parte(0, 0.72f, -1.22f, 0.77f, 0.12f, 0.27f, 0.20f, 0.45f, 0.80f); // Franja azul de la punta.
        r.parte(-0.75f, 1.0f, 0.3f, 0.12f, 0.3f, 1.8f, 0.93f, 0.90f, 0.82f); // Borda izquierda.
        r.parte(0.75f, 1.0f, 0.3f, 0.12f, 0.3f, 1.8f, 0.93f, 0.90f, 0.82f); // Borda derecha.
        r.parte(0, 1.0f, 1.2f, 1.6f, 0.3f, 0.12f, 0.93f, 0.90f, 0.82f); // Borda trasera.
        r.parte(0, 1.05f, -0.45f, 1.3f, 0.3f, 0.06f, 0.55f, 0.80f, 0.95f); // Parabrisas celeste.
        r.parte(0, 1.12f, -0.15f, 0.32f, 0.22f, 0.05f, 0.15f, 0.15f, 0.18f); // Volante bajo, para no tapar la cara de Bob.
        r.parte(0, 0.9f, 1.2f, 0.5f, 0.6f, 0.35f, 0.40f, 0.42f, 0.45f); // Motor fuera de borda, dentro del largo del auto.
        r.parte(0, 1.25f, 1.2f, 0.55f, 0.15f, 0.4f, 0.85f, 0.20f, 0.15f); // Tapa roja del motor.
        float[] ladosLuces = {-0.3f, 0.3f}; // Separación de los faros en la proa.
        for (float x : ladosLuces) { // Repite para ambos faros.
            r.entero("uEmision", faros ? 1 : 0); // Con los faros encendidos el vidrio brilla.
            float brilloFaro = faros ? 1 : 0.55f; // Apagado se ve como vidrio gris.
            r.parte(x, 0.68f, -1.35f, 0.25f, 0.15f, 0.05f, brilloFaro, brilloFaro * 0.95f, brilloFaro * 0.75f); // Faro delantero.
            boolean trasera = r.noche || frenando; // Las luces traseras se encienden de noche o al frenar.
            r.entero("uEmision", trasera ? 1 : 0); // Encendidas, no dependen de la iluminación.
            float rojo = frenando ? 1.0f : (r.noche ? 0.6f : 0.75f); // Al frenar son más intensas.
            float ancho = frenando ? 0.3f : 0.25f; // Y se ven un poco más grandes.
            r.parte(x * 2, 0.68f, 1.26f, ancho, 0.14f, 0.05f, rojo, 0.04f, 0.03f); // Luz trasera roja.
        }
        r.entero("uEmision", 0); // Restablece la iluminación normal.
        Bob.dibujar(r, velocidad); // Sienta a Bob al volante.
        if (Math.abs(velocidad) > 0.5f) { // Solo hay burbujas del motor mientras el bote avanza.
            dibujarBurbujasMotor(r); // Añade burbujas que salen por detrás.
        }
    }

    /** Suelta tres burbujas que suben y se alejan del motor en un ciclo de dos tercios de segundo. */
    private void dibujarBurbujasMotor(Renderizador r) {
        for (int indice = 0; indice < 3; indice++) { // Cada burbuja está desfasada un tercio del ciclo.
            float t = (r.tiempo * 1.5f + indice / 3f) % 1; // Progreso de 0 a 1 de esta burbuja.
            float tamano = 0.2f + t * 0.15f; // La burbuja crece al subir.
            float lateral = (float) Math.sin(r.tiempo * 6 + indice) * 0.1f; // Oscila un poco hacia los lados.
            r.parte(lateral, 0.6f + t * 2, 1.5f + t * 0.8f, tamano, tamano, tamano, 0.80f, 0.92f, 1.0f); // Burbuja.
        }
    }
}
