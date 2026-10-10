package com.graphics.vehiculos; // Vehículos: el botemóvil del jugador y el tráfico autónomo.

import com.graphics.motor.Renderizador; // Pincel con el que se dibuja cada caja.

/**
 * TRÁFICO: los cuatro botes que circulan solos por la ciudad.
 * Cada uno sigue su ruta de cruces; si el jugador o un bote que va en su mismo sentido está delante,
 * frena. También decide si el jugador puede moverse sin meterse dentro de un bote.
 */
public class Trafico {

    /*
     * Cada ruta es una lista de cruces (X, Z) en el centro de calles; el último vuelve al primero.
     * Como los cruces están en filas y columnas pares del Mapa, cada tramo recorre una calle completa.
     */
    public static final float[][][] RUTAS = {
        {{-50, -50}, {50, -50}, {50, 50}, {-50, 50}}, // Anillo exterior en sentido horario.
        {{-30, -30}, {-30, 30}, {30, 30}, {30, -30}}, // Vuelta alrededor de la ciudad original.
        {{-10, -50}, {-10, -10}, {30, -10}, {30, 30}, {50, 30}, {50, -50}}, // Ruta en zigzag con seis giros.
        {{-50, 30}, {10, 30}, {10, 50}, {-50, 50}} // Paseo frente al Crustáceo y el Balde.
    };

    private Vehiculo[] vehiculos = crear(); // Botes que circulan solos.

    /** Crea un bote por ruta, cada uno con su velocidad y color. */
    private static Vehiculo[] crear() {
        return new Vehiculo[] { // Arreglo con los cuatro vehículos.
            new Vehiculo(RUTAS[0], 7, 0.95f, 0.55f, 0.70f), // Bote rosado por el anillo exterior.
            new Vehiculo(RUTAS[1], 6, 0.45f, 0.80f, 0.45f), // Bote verde por la ciudad original.
            new Vehiculo(RUTAS[2], 8, 0.65f, 0.50f, 0.90f), // Bote violeta en zigzag.
            new Vehiculo(RUTAS[3], 6.5f, 1.0f, 0.65f, 0.25f) // Bote anaranjado frente a los restaurantes.
        };
    }

    /** Devuelve cada bote al inicio de su ruta, para que el nuevo recorrido empiece igual que el primero. */
    public void reiniciar() {
        vehiculos = crear(); // Crea los botes de nuevo.
    }

    /** Mueve cada bote; frena si el jugador o un bote de su mismo sentido está delante. */
    public void actualizar(float deltaTime, Auto auto) {
        for (Vehiculo vehiculo : vehiculos) { // Revisa cada bote.
            boolean bloqueado = vehiculo.tieneDelante(auto.x, auto.z); // Frena si el jugador está delante.
            for (Vehiculo otro : vehiculos) { // Compara con los demás botes.
                boolean delante = vehiculo.tieneDelante(otro.carrilX(), otro.carrilZ()); // El otro está en su camino.
                if (otro != vehiculo && vehiculo.mismoSentido(otro) && delante) { // Solo espera a quien va en su sentido.
                    bloqueado = true; // Se detiene detrás del otro bote.
                }
            }
            vehiculo.actualizar(deltaTime, bloqueado); // Avanza por su ruta.
        }
    }

    /**
     * Indica si el jugador puede pasar de (actualX, actualZ) a (nuevaX, nuevaZ) sin meterse dentro de un bote.
     * Alejarse siempre está permitido, así el jugador nunca queda atrapado.
     */
    public boolean permite(float nuevaX, float nuevaZ, float actualX, float actualZ) {
        float minimo = Auto.RADIO * 1.8f; // Dos círculos casi tocándose.
        for (Vehiculo vehiculo : vehiculos) { // Revisa cada bote.
            float nuevaDX = nuevaX - vehiculo.carrilX(); // Separación en X desde la posición propuesta.
            float nuevaDZ = nuevaZ - vehiculo.carrilZ(); // Separación en Z desde la posición propuesta.
            float actualDX = actualX - vehiculo.carrilX(); // Separación en X desde la posición actual.
            float actualDZ = actualZ - vehiculo.carrilZ(); // Separación en Z desde la posición actual.
            float nueva = nuevaDX * nuevaDX + nuevaDZ * nuevaDZ; // Distancia al cuadrado propuesta.
            float actual = actualDX * actualDX + actualDZ * actualDZ; // Distancia al cuadrado actual.
            if (nueva < minimo * minimo && nueva < actual) { // Solo bloquea si se acerca demasiado.
                return false; // Se metería dentro del bote.
            }
        }
        return true; // No hay ningún choque.
    }

    /** Dibuja todos los botes, también en el minimapa. */
    public void dibujar(Renderizador r) {
        for (Vehiculo vehiculo : vehiculos) { // Recorre los botes.
            vehiculo.dibujar(r); // Cada bote se dibuja en su carril.
        }
    }
}
