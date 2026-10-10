package com.graphics.ciudad; // Todo lo que forma la ciudad: mapa, manzanas, calles y mobiliario.

import com.graphics.motor.Renderizador; // Pincel con el que se dibuja cada caja.

/**
 * CASA: vivienda de un barrio, con cuatro estilos.
 * Techo a dos o cuatro aguas y uno o dos pisos. Según su semilla agrega porche con columnas o balcón,
 * contraventanas de color, alféizares, chimenea, flores, arbusto y buzón. "frente" indica hacia dónde
 * mira la fachada: -1 al norte y +1 al sur.
 */
public final class Casa {

    private static final float[][] PAREDES = { // Colores pastel de las casas.
        {0.95f, 0.80f, 0.62f}, {0.62f, 0.80f, 0.92f}, {0.95f, 0.70f, 0.75f}, {0.75f, 0.90f, 0.70f},
        {0.98f, 0.92f, 0.60f}, {0.80f, 0.72f, 0.92f}, {0.96f, 0.95f, 0.90f}, {0.98f, 0.76f, 0.58f}
    };
    private static final float[][] TECHOS = { // Colores de las tejas.
        {0.70f, 0.25f, 0.18f}, {0.25f, 0.35f, 0.55f}, {0.35f, 0.45f, 0.30f}, {0.45f, 0.30f, 0.22f}
    };
    private static final float[][] DETALLES = { // Puertas, contraventanas y buzones.
        {0.18f, 0.40f, 0.25f}, {0.15f, 0.25f, 0.45f}, {0.60f, 0.15f, 0.12f}, {0.40f, 0.25f, 0.15f}, {0.15f, 0.45f, 0.45f}
    };
    private static final float[][] FLORES = { // Colores de las flores del cantero.
        {0.90f, 0.30f, 0.30f}, {0.30f, 0.55f, 0.90f}, {0.95f, 0.80f, 0.25f}, {0.40f, 0.75f, 0.45f},
        {0.85f, 0.45f, 0.80f}, {0.95f, 0.95f, 0.95f}
    };

    /** Clase de utilidades: no se crean objetos Casa. */
    private Casa() {
    }

    /** Dibuja una casa con centro en (x, z); la semilla elige colores y estilo. */
    public static void dibujar(Renderizador r, float x, float z, float frente, int semilla) {
        float[] pared = PAREDES[semilla % PAREDES.length]; // Color de las paredes.
        float[] techo = TECHOS[(semilla / 2 + 1) % TECHOS.length]; // Color de las tejas.
        float[] detalle = DETALLES[(semilla / 3) % DETALLES.length]; // Color de puerta y contraventanas.
        int estilo = (semilla / 5) % 4; // 0 y 1: dos aguas; 2 y 3: cuatro aguas; impares: dos pisos.
        boolean dosPisos = estilo % 2 == 1; // Estilos 1 y 3.
        boolean cuatroAguas = estilo >= 2; // Estilos 2 y 3.
        float alto = dosPisos ? 4.0f : 2.6f; // Altura de las paredes.
        float base = 0.36f; // Altura del césped.
        r.caja(x, base + 0.12f, z, 2.55f, 0.24f, 2.55f, 0.55f, 0.52f, 0.48f); // Zócalo de piedra.
        r.caja(x, base + alto / 2, z, 2.4f, alto, 2.4f, pared[0], pared[1], pared[2]); // Paredes.
        float y = base + alto; // Altura donde empieza el techo.
        if (cuatroAguas) { // Techo de cuatro aguas: pirámide escalonada.
            float[] anchos = {2.8f, 2.2f, 1.6f, 1.0f, 0.45f}; // Cada nivel más angosto en ambos ejes.
            for (int nivel = 0; nivel < anchos.length; nivel++) { // Apila los niveles.
                float sombra = 1 - nivel * 0.04f; // Los niveles altos algo más oscuros dan volumen.
                r.caja(x, y + 0.13f + nivel * 0.24f, z, anchos[nivel], 0.26f, anchos[nivel], techo[0] * sombra, techo[1] * sombra, techo[2] * sombra); // Nivel.
            }
        } else { // Techo a dos aguas: la cumbrera corre a lo largo de X.
            r.caja(x, y + 0.15f, z, 2.7f, 0.3f, 2.7f, techo[0], techo[1], techo[2]); // Alero del techo.
            r.caja(x, y + 0.45f, z, 2.7f, 0.3f, 1.9f, techo[0], techo[1], techo[2]); // Segundo escalón del techo.
            r.caja(x, y + 0.75f, z, 2.7f, 0.3f, 1.1f, techo[0], techo[1], techo[2]); // Tercer escalón del techo.
            r.caja(x, y + 1.0f, z, 2.7f, 0.2f, 0.4f, techo[0] * 0.8f, techo[1] * 0.8f, techo[2] * 0.8f); // Cumbrera más oscura.
        }
        if (r.vistaMapa) { // El minimapa no necesita detalles.
            return; // Termina aquí.
        }
        r.caja(x + 0.75f, y + 0.9f, z - frente * 0.45f, 0.35f, 1.1f, 0.35f, 0.60f, 0.30f, 0.22f); // Chimenea de ladrillo.
        if (dosPisos) { // Moldura blanca entre los dos pisos.
            r.caja(x, base + 2.6f, z, 2.46f, 0.12f, 2.46f, 0.95f, 0.95f, 0.92f); // Separa visualmente los pisos.
        }
        float cara = z + frente * 1.21f; // Fachada principal, hacia la calle.
        r.caja(x - 0.5f, base + 0.85f, cara, 0.76f, 1.76f, 0.03f, 0.95f, 0.95f, 0.92f); // Marco blanco de la puerta.
        r.caja(x - 0.5f, base + 0.8f, cara + frente * 0.025f, 0.6f, 1.6f, 0.03f, detalle[0], detalle[1], detalle[2]); // Puerta de color.
        r.caja(x - 0.32f, base + 0.8f, cara + frente * 0.05f, 0.06f, 0.06f, 0.04f, 0.95f, 0.80f, 0.30f); // Picaporte dorado.
        dibujarVentanaFrente(r, x + 0.5f, base + 1.4f, cara, frente, detalle); // Ventana de la sala con contraventanas.
        r.vidrio(x - 1.21f, base + 1.4f, z, 0.04f, 0.6f, 0.6f); // Ventana lateral oeste.
        r.vidrio(x + 1.21f, base + 1.4f, z, 0.04f, 0.6f, 0.6f); // Ventana lateral este.
        if (dosPisos) { // El segundo piso tiene dos ventanas más.
            dibujarVentanaFrente(r, x - 0.5f, base + 3.2f, cara, frente, detalle); // Ventana alta izquierda.
            dibujarVentanaFrente(r, x + 0.5f, base + 3.2f, cara, frente, detalle); // Ventana alta derecha.
            r.vidrio(x - 1.21f, base + 3.2f, z, 0.04f, 0.6f, 0.6f); // Ventana lateral alta oeste.
            r.vidrio(x + 1.21f, base + 3.2f, z, 0.04f, 0.6f, 0.6f); // Ventana lateral alta este.
            if (semilla % 2 == 0) { // La mitad de las casas de dos pisos tiene balcón.
                r.caja(x + 0.5f, base + 2.72f, z + frente * 1.42f, 1.1f, 0.08f, 0.45f, 0.95f, 0.95f, 0.92f); // Piso del balcón.
                r.caja(x + 0.5f, base + 2.95f, z + frente * 1.62f, 1.1f, 0.38f, 0.04f, 0.95f, 0.95f, 0.92f); // Baranda.
            }
        }
        boolean porche = !dosPisos || semilla % 2 == 1; // Las casas sin balcón tienen porche.
        if (porche) { // Techito sobre la puerta sostenido por dos columnas.
            r.caja(x - 0.5f, base + 1.95f, z + frente * 1.5f, 1.4f, 0.1f, 0.7f, techo[0], techo[1], techo[2]); // Techo del porche.
            r.caja(x - 1.1f, base + 0.97f, z + frente * 1.78f, 0.08f, 1.94f, 0.08f, 0.95f, 0.95f, 0.92f); // Columna izquierda.
            r.caja(x + 0.1f, base + 0.97f, z + frente * 1.78f, 0.08f, 1.94f, 0.08f, 0.95f, 0.95f, 0.92f); // Columna derecha.
        } else { // Sin porche hay un arbusto junto a la puerta.
            r.caja(x - 1.0f, base + 0.28f, z + frente * 1.55f, 0.45f, 0.55f, 0.4f, 0.18f, 0.45f, 0.20f); // Arbusto.
        }
        if (r.noche) { // De noche se enciende la luz del porche.
            r.entero("uEmision", 1); // Color directo.
            r.caja(x - 0.5f, base + 1.8f, cara + frente * 0.06f, 0.18f, 0.12f, 0.08f, 1, 0.85f, 0.45f); // Foco sobre la puerta.
            r.entero("uEmision", 0); // Restablece la iluminación normal.
        }
        r.caja(x - 0.5f, base + 0.05f, z + frente * 1.45f, 0.9f, 0.1f, 0.5f, 0.70f, 0.68f, 0.62f); // Escalón de entrada.
        r.caja(x - 0.5f, base + 0.01f, z + frente * 1.9f, 0.6f, 0.04f, 0.5f, 0.78f, 0.75f, 0.68f); // Sendero hasta el portón.
        for (int flor = 0; flor < 4; flor++) { // Cantero de flores bajo la ventana.
            float[] color = FLORES[(semilla + flor) % FLORES.length]; // Reutiliza la paleta alegre.
            r.caja(x + 0.25f + flor * 0.22f, base + 0.12f, z + frente * 1.55f, 0.14f, 0.2f, 0.14f, color[0], color[1], color[2]); // Flor.
        }
        r.caja(x + 0.85f, base + 0.4f, z + frente * 1.95f, 0.06f, 0.8f, 0.06f, 0.30f, 0.30f, 0.32f); // Poste del buzón.
        r.caja(x + 0.85f, base + 0.85f, z + frente * 1.95f, 0.22f, 0.2f, 0.36f, detalle[0], detalle[1], detalle[2]); // Buzón del color de la puerta.
    }

    /** Ventana de fachada con alféizar blanco y contraventanas de color a ambos lados. */
    private static void dibujarVentanaFrente(Renderizador r, float x, float y, float cara, float frente, float[] detalle) {
        r.vidrio(x, y, cara, 0.6f, 0.6f, 0.04f); // Vidrio; de noche se enciende.
        r.caja(x, y - 0.34f, cara + frente * 0.05f, 0.72f, 0.06f, 0.1f, 0.95f, 0.95f, 0.92f); // Alféizar.
        r.caja(x - 0.4f, y, cara + frente * 0.02f, 0.13f, 0.64f, 0.03f, detalle[0], detalle[1], detalle[2]); // Contraventana izquierda.
        r.caja(x + 0.4f, y, cara + frente * 0.02f, 0.13f, 0.64f, 0.03f, detalle[0], detalle[1], detalle[2]); // Contraventana derecha.
    }
}
