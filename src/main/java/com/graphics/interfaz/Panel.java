package com.graphics.interfaz; // Lo que se dibuja encima de la escena: minimapa y panel.

import com.graphics.juego.Entregas; // Progreso y destino activo.
import com.graphics.juego.Iluminacion; // Estado de los faros y del ambiente.
import com.graphics.motor.Renderizador; // Pincel con el que se dibuja cada caja.
import com.graphics.motor.Texto; // Escribe con la fuente de cuadritos.
import com.graphics.vehiculos.Auto; // Velocidad del jugador.
import static org.lwjgl.opengl.GL33.*; // Desactivar la profundidad al dibujar sobre la escena.

/**
 * PANEL: información dibujada encima de la imagen, en coordenadas de píxeles (uMapa = 2).
 * Abajo a la izquierda: velocidad, faros, día/noche, entregas, cronómetro y destino.
 * En pausa (tecla P): menú central con la ayuda de controles; al iniciar se llama FONDO DE BIKINI.
 */
public class Panel {

    private Renderizador r; // Pincel del cuadro actual.
    private int ancho; // Ancho de la ventana en píxeles.
    private int alto; // Alto de la ventana en píxeles.
    private float velocidad; // Velocidad del auto.
    private boolean faros; // Faros encendidos.
    private int entregas; // Entregas completadas.
    private float tiempo; // Cronómetro de la partida.
    private boolean iniciado; // false hasta la primera vez que se presiona P.

    /** Dibuja el panel y, si el juego está en pausa, el menú. */
    public void dibujar(Renderizador r, int ancho, int alto, Auto auto, Iluminacion luz, Entregas progreso,
                        boolean pausado, boolean iniciado) {
        this.r = r; // Guarda los datos del cuadro para los métodos de abajo.
        this.ancho = ancho; // Ancho actual.
        this.alto = alto; // Alto actual.
        this.velocidad = auto.velocidad; // Velocidad que se muestra.
        this.faros = luz.faros; // Estado de los faros.
        this.entregas = progreso.entregas; // Entregas completadas.
        this.tiempo = progreso.tiempo; // Cronómetro.
        this.iniciado = iniciado; // Título del menú.
        glDisable(GL_DEPTH_TEST); // El panel siempre queda encima; se dibuja en orden.
        r.entero("uMapa", 2); // Selecciona la proyección de pantalla del shader de vértices.
        r.vector2("uPantalla", ancho, alto); // Informa el tamaño actual de la ventana.
        try { // Restaura el estado aunque falle el dibujo.
            dibujarPanel(); // Velocidad, luces y entregas.
            if (pausado) { // El menú solo aparece en pausa.
                dibujarMenu(); // Ayuda de controles.
            }
        } finally { // El próximo cuadro debe empezar en perspectiva.
            r.entero("uMapa", 0); // Recupera la perspectiva.
            glEnable(GL_DEPTH_TEST); // Recupera la prueba de profundidad.
        }
        if (glGetError() != GL_NO_ERROR) { // Comprueba el pase del panel.
            throw new IllegalStateException("Error OpenGL en el panel"); // Expone el error en la consola.
        }
    }

    /** Escribe en la pantalla; (x, y) es la esquina inferior izquierda en píxeles. */
    private void escribir(String mensaje, float x, float y, float p, float rojo, float verde, float azul) {
        Texto.escribir(r, mensaje, x, y, 0, p, 1, rojo, verde, azul); // En la pantalla las letras avanzan hacia la derecha.
    }

    /** Dibuja un rectángulo de pantalla a partir de su esquina inferior izquierda, en píxeles. */
    private void rectangulo(float x, float y, float anchoRect, float altoRect, float rojo, float verde, float azul) {
        r.caja(x + anchoRect / 2, y + altoRect / 2, 0, anchoRect, altoRect, 1, rojo, verde, azul); // caja() recibe el centro.
    }

    /** Escala del panel según la ventana, para que se lea igual al redimensionarla. */
    private float escala() {
        float segunAlto = alto / 760f; // Proporción respecto del alto inicial.
        float segunAncho = ancho / 1100f; // Proporción respecto del ancho inicial.
        return Math.max(0.45f, Math.min(1.6f, Math.min(segunAlto, segunAncho))); // Usa la menor, con límites.
    }

    /** Panel inferior izquierdo: velocidad, faros, día/noche, entregas y destino. */
    private void dibujarPanel() {
        float s = escala(); // Escala de todo el panel.
        float x = 14 * s; // Margen izquierdo.
        float y = 14 * s; // Margen inferior.
        float anchoPanel = 340 * s; // Ancho del panel.
        float altoPanel = 150 * s; // Alto del panel.
        rectangulo(x - 3 * s, y - 3 * s, anchoPanel + 6 * s, altoPanel + 6 * s, 0.80f, 0.87f, 0.94f); // Marco claro, como el del minimapa.
        rectangulo(x, y, anchoPanel, altoPanel, 0.06f, 0.10f, 0.15f); // Fondo oscuro.
        String velocidadTexto = String.valueOf(Math.round(Math.abs(velocidad) * 3.6f)); // Km/h, como en el título.
        escribir(velocidadTexto, x + 14 * s, y + 82 * s, 6 * s, 1, 1, 1); // Velocidad en números grandes.
        float despues = x + 14 * s + Texto.ancho(velocidadTexto, 6 * s) + 10 * s; // Posición a la derecha del número.
        escribir("KM/H", despues, y + 82 * s, 2 * s, 0.65f, 0.72f, 0.80f); // Unidad.
        escribir("P: AYUDA", x + anchoPanel - Texto.ancho("P: AYUDA", 1.5f * s) - 10 * s, y + altoPanel - 18 * s, 1.5f * s, 0.65f, 0.72f, 0.80f); // Recordatorio.
        if (faros) { // Faros encendidos.
            escribir("FAROS ON", x + 14 * s, y + 56 * s, 2 * s, 1, 0.85f, 0.35f); // En amarillo.
        } else { // Faros apagados.
            escribir("FAROS OFF", x + 14 * s, y + 56 * s, 2 * s, 0.50f, 0.55f, 0.60f); // En gris.
        }
        if (r.noche) { // Ambiente nocturno.
            escribir("NOCHE", x + 190 * s, y + 56 * s, 2 * s, 0.55f, 0.75f, 1); // En celeste.
        } else { // Ambiente diurno.
            escribir("DIA", x + 190 * s, y + 56 * s, 2 * s, 1, 0.85f, 0.35f); // En amarillo.
        }
        String progreso = "ENTREGAS " + entregas + "/" + Entregas.DESTINOS.length + "  " + (int) tiempo + " S"; // Avance y cronómetro.
        escribir(progreso, x + 14 * s, y + 34 * s, 2 * s, 1, 1, 1); // En blanco.
        if (entregas == Entregas.DESTINOS.length) { // Recorrido completo.
            escribir("GANASTE! R: OTRA VEZ", x + 14 * s, y + 12 * s, 2 * s, 0.45f, 0.95f, 0.45f); // En verde.
        } else { // Todavía quedan entregas.
            escribir("DESTINO: " + Entregas.NOMBRES[entregas], x + 14 * s, y + 12 * s, 2 * s, 1, 0.72f, 0.12f); // En dorado, como la marca.
        }
    }

    /** Menú central de inicio o pausa con la ayuda de controles. */
    private void dibujarMenu() {
        float s = escala(); // Escala del menú.
        float anchoMenu = 580 * s; // Ancho del menú.
        float altoMenu = 430 * s; // Alto del menú.
        float x = (ancho - anchoMenu) / 2; // Centra horizontalmente en la ventana.
        float y = (alto - altoMenu) / 2; // Centra verticalmente.
        rectangulo(x - 4 * s, y - 4 * s, anchoMenu + 8 * s, altoMenu + 8 * s, 1, 0.85f, 0.30f); // Marco amarillo, color de Bob.
        rectangulo(x, y, anchoMenu, altoMenu, 0.05f, 0.12f, 0.22f); // Fondo azul marino.
        String titulo = "PAUSA"; // Título cuando el juego ya empezó.
        if (!iniciado) { // Primera vez: menú de inicio.
            titulo = "FONDO DE BIKINI"; // Nombre de nuestra ciudad.
        }
        escribir(titulo, x + (anchoMenu - Texto.ancho(titulo, 5 * s)) / 2, y + altoMenu - 60 * s, 5 * s, 1, 0.85f, 0.30f); // Título centrado.
        String[] ayuda = { // Controles del juego.
            "WASD / FLECHAS: CONDUCIR", // Conducción.
            "ESPACIO: FRENO", // Freno.
            "C: CAMARA", // Cámara.
            "N: DIA / NOCHE", // Iluminación.
            "F: FAROS", // Faros.
            "M: MINIMAPA", // Minimapa.
            "R: REINICIAR", // Reinicio.
            "ESC: SALIR" // Salida.
        };
        float lineaY = y + altoMenu - 100 * s; // Primera línea bajo el título.
        for (String linea : ayuda) { // Escribe cada control.
            escribir(linea, x + 40 * s, lineaY, 2.5f * s, 0.90f, 0.93f, 0.97f); // Texto claro.
            lineaY -= 27 * s; // Baja una línea.
        }
        lineaY -= 10 * s; // Separa el objetivo de los controles.
        escribir("ENTREGA LAS " + Entregas.DESTINOS.length + " CANGREBURGERS", x + 40 * s, lineaY, 2.5f * s, 0.45f, 0.95f, 0.45f); // Objetivo.
        escribir("FRENA EN LA MARCA DORADA", x + 40 * s, lineaY - 27 * s, 2.5f * s, 1, 0.72f, 0.12f); // Cómo entregar.
        String pie = "P: CONTINUAR"; // Pie cuando el juego ya empezó.
        if (!iniciado) { // Primera vez.
            pie = "P: JUGAR"; // Invita a empezar.
        }
        escribir(pie, x + (anchoMenu - Texto.ancho(pie, 3 * s)) / 2, y + 24 * s, 3 * s, 1, 0.85f, 0.30f); // Pie centrado.
    }
}
