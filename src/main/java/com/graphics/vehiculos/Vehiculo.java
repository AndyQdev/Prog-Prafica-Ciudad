package com.graphics.vehiculos; // Vehículos: el botemóvil del jugador y el tráfico autónomo.

import com.graphics.motor.Renderizador; // Pincel con el que se dibuja cada caja.

/**
 * VEHÍCULO: bote autónomo del tráfico, con un pez al volante.
 * Guarda posición, orientación, velocidad y ruta; Trafico decide cuándo debe frenar.
 * La ruta es una lista de cruces. Cada tramo une dos cruces por el centro de una calle, así que
 * el vehículo nunca atraviesa manzanas ni sale del mapa; circula desplazado hacia su carril derecho.
 */
public class Vehiculo {

    // ==================== 1. ESTADO DEL VEHÍCULO ====================
    public static final float CARRIL = 2; // Distancia del centro de la calle al centro del carril derecho.
    public float x; // Posición X sobre la línea central de la calle.
    public float z; // Posición Z sobre la línea central de la calle.
    public float angulo; // Orientación en radianes; cero mira hacia -Z, igual que el auto del jugador.
    public float velocidad = 0; // Unidades por segundo.
    public int destino = 1; // Índice del cruce hacia el que se dirige.
    public final float[][] ruta; // Cruces que recorre en ciclo; cada fila contiene X y Z.
    public final float velocidadMaxima; // Velocidad en las rectas.
    public final float rojo; // Color del casco: componente roja.
    public final float verde; // Color del casco: componente verde.
    public final float azul; // Color del casco: componente azul.

    /** Coloca el vehículo en el primer cruce de su ruta, mirando hacia el segundo. */
    public Vehiculo(float[][] ruta, float velocidadMaxima, float rojo, float verde, float azul) {
        this.ruta = ruta; // Guarda la ruta compartida.
        this.velocidadMaxima = velocidadMaxima; // Guarda la velocidad en recta.
        this.rojo = rojo; // Guarda el color rojo.
        this.verde = verde; // Guarda el color verde.
        this.azul = azul; // Guarda el color azul.
        x = ruta[0][0]; // Parte del primer cruce.
        z = ruta[0][1]; // Completa la posición inicial.
        angulo = anguloHacia(ruta[1][0] - x, ruta[1][1] - z); // Mira directamente al segundo cruce.
    }

    // ==================== 2. MOVIMIENTO POR CUADRO ====================

    /** Avanza por la ruta; bloqueado indica que hay alguien delante y debe frenar. */
    public void actualizar(float deltaTime, boolean bloqueado) {
        float objetivoX = ruta[destino][0]; // X del próximo cruce.
        float objetivoZ = ruta[destino][1]; // Z del próximo cruce.
        float distanciaX = objetivoX - x; // Separación horizontal al cruce.
        float distanciaZ = objetivoZ - z; // Separación en profundidad.
        float distancia = (float) Math.sqrt(distanciaX * distanciaX + distanciaZ * distanciaZ); // Distancia real al cruce.
        float deseada = Math.min(velocidadMaxima, 2.5f + distancia * 0.6f); // Reduce la velocidad al acercarse a la esquina.
        if (bloqueado) { // Hay un vehículo o el jugador delante.
            deseada = 0; // Se detiene hasta que el camino quede libre.
        }
        float ajuste = Math.min(1, 3 * deltaTime); // Fracción del cambio aplicada en este cuadro.
        velocidad += (deseada - velocidad) * ajuste; // Acelera o frena de forma gradual.
        float paso = velocidad * deltaTime; // Distancia recorrida en este cuadro.
        if (paso >= distancia) { // Llega al cruce o lo pasaría en este cuadro.
            x = objetivoX; // Se ubica exactamente en el cruce.
            z = objetivoZ; // Así nunca se sale de la línea central.
            destino = (destino + 1) % ruta.length; // Elige el siguiente cruce; al final vuelve al primero.
        } else { // Todavía no llega al cruce.
            x += distanciaX / distancia * paso; // Avanza sobre la recta en X.
            z += distanciaZ / distancia * paso; // Avanza sobre la recta en Z.
        }
        girarHacia(ruta[destino][0] - x, ruta[destino][1] - z, deltaTime); // Gira de a poco hacia el tramo actual.
    }

    /** Gira suavemente la orientación hacia una dirección; el giro visible ocurre en las esquinas. */
    private void girarHacia(float direccionX, float direccionZ, float deltaTime) {
        if (Math.abs(direccionX) + Math.abs(direccionZ) < 0.001f) { // Sin dirección definida no hay giro.
            return; // Conserva la orientación actual.
        }
        float diferencia = anguloHacia(direccionX, direccionZ) - angulo; // Cuánto falta girar.
        while (diferencia > Math.PI) { // Elige el giro corto en lugar de dar la vuelta completa.
            diferencia -= 2 * (float) Math.PI; // Resta una vuelta.
        }
        while (diferencia < -Math.PI) { // Repite para el otro sentido.
            diferencia += 2 * (float) Math.PI; // Suma una vuelta.
        }
        angulo += diferencia * Math.min(1, 6 * deltaTime); // Recorre una parte del giro en cada cuadro.
    }

    /** Ángulo cuyo frente (-sen, -cos) apunta en la dirección indicada, con la misma convención que Auto. */
    private static float anguloHacia(float direccionX, float direccionZ) {
        return (float) Math.atan2(-direccionX, -direccionZ); // Despeja el ángulo de frente = (-sen, -cos).
    }

    // ==================== 3. CARRIL Y DETECCIÓN ====================

    /** Posición X dibujada: el centro de la calle desplazado hacia la derecha del vehículo. */
    public float carrilX() {
        return x + (float) Math.cos(angulo) * CARRIL; // La derecha de un ángulo a es (cos a, -sen a).
    }

    /** Posición Z dibujada, con el mismo desplazamiento hacia la derecha. */
    public float carrilZ() {
        return z - (float) Math.sin(angulo) * CARRIL; // Completa el vector derecho.
    }

    /** Indica si un punto está en su carril, delante y a menos de siete unidades. */
    public boolean tieneDelante(float otroX, float otroZ) {
        float frenteX = -(float) Math.sin(angulo); // Dirección frontal en X.
        float frenteZ = -(float) Math.cos(angulo); // Dirección frontal en Z.
        float relativaX = otroX - carrilX(); // Vector desde este vehículo hasta el otro punto.
        float relativaZ = otroZ - carrilZ(); // Completa el vector relativo.
        float adelante = relativaX * frenteX + relativaZ * frenteZ; // Proyección sobre el frente.
        float lateral = Math.abs(relativaX * frenteZ - relativaZ * frenteX); // Distancia al eje del vehículo.
        return adelante > 0 && adelante < 7 && lateral < 2.8f; // Delante, cerca y dentro del ancho de dos autos.
    }

    /** Indica si otro vehículo circula en el mismo sentido; solo esos se esperan entre sí. */
    public boolean mismoSentido(Vehiculo otro) {
        return Math.cos(angulo - otro.angulo) > 0.7f; // Diferencia menor que unos 45 grados.
    }

    // ==================== 4. DIBUJO ====================

    /** Bote del color del vehículo con un pez al volante; las piezas giran con el bote. */
    public void dibujar(Renderizador r) {
        r.origen(carrilX(), carrilZ(), angulo); // Usa la posición y el giro del vehículo.
        float[] lados = {-0.8f, 0.8f}; // Ubicación de las ruedas.
        for (float x : lados) { // Lado izquierdo y derecho.
            for (float z : lados) { // Eje delantero y trasero.
                r.parte(x, 0.35f, z, 0.22f, 0.5f, 0.5f, 0.06f, 0.07f, 0.08f); // Rueda.
            }
        }
        r.parte(0, 0.6f, 0.2f, 1.5f, 0.5f, 1.9f, rojo, verde, azul); // Casco del color del vehículo.
        r.parte(0, 0.62f, -0.95f, 1.1f, 0.46f, 0.4f, rojo, verde, azul); // Proa.
        r.parte(0, 0.72f, 0.2f, 1.52f, 0.1f, 1.92f, 0.95f, 0.95f, 0.95f); // Franja blanca.
        r.parte(0, 0.72f, -0.95f, 1.12f, 0.1f, 0.42f, 0.95f, 0.95f, 0.95f); // Franja blanca de la proa.
        r.parte(0, 1.0f, -0.45f, 1.2f, 0.3f, 0.06f, 0.55f, 0.80f, 0.95f); // Parabrisas.
        r.parte(-0.35f, 0.68f, -1.16f, 0.22f, 0.14f, 0.05f, 1, 0.95f, 0.65f); // Faro izquierdo.
        r.parte(0.35f, 0.68f, -1.16f, 0.22f, 0.14f, 0.05f, 1, 0.95f, 0.65f); // Faro derecho.
        r.parte(-0.55f, 0.68f, 1.16f, 0.22f, 0.12f, 0.05f, 0.85f, 0.05f, 0.05f); // Luz trasera izquierda.
        r.parte(0.55f, 0.68f, 1.16f, 0.22f, 0.12f, 0.05f, 0.85f, 0.05f, 0.05f); // Luz trasera derecha.
        float pezRojo = verde; // El pez usa los colores del bote rotados para contrastar.
        float pezVerde = azul; // Segunda componente rotada.
        float pezAzul = rojo; // Tercera componente rotada.
        float cola = (float) Math.sin(r.tiempo * 8 + velocidadMaxima) * 0.08f; // La cola se agita.
        r.parte(0, 1.35f, 0.3f, 0.45f, 0.55f, 0.8f, pezRojo, pezVerde, pezAzul); // Cuerpo del pez.
        r.parte(cola, 1.35f, 0.85f, 0.08f, 0.5f, 0.35f, pezRojo, pezVerde, pezAzul); // Cola.
        r.parte(0, 1.7f, 0.3f, 0.08f, 0.2f, 0.4f, pezRojo, pezVerde, pezAzul); // Aleta dorsal.
        r.parte(-0.23f, 1.45f, 0.05f, 0.02f, 0.16f, 0.16f, 1, 1, 1); // Ojo izquierdo, en el costado.
        r.parte(0.23f, 1.45f, 0.05f, 0.02f, 0.16f, 0.16f, 1, 1, 1); // Ojo derecho.
        r.parte(-0.24f, 1.45f, 0.03f, 0.02f, 0.08f, 0.08f, 0, 0, 0); // Pupila izquierda.
        r.parte(0.24f, 1.45f, 0.03f, 0.02f, 0.08f, 0.08f, 0, 0, 0); // Pupila derecha.
        r.parte(0, 1.25f, -0.11f, 0.2f, 0.05f, 0.02f, 0.30f, 0.05f, 0.05f); // Boca.
    }
}
